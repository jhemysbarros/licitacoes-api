package br.com.licitacoesapi.service;

import br.com.licitacoesapi.model.Licitacao;
import br.com.licitacoesapi.model.StatusEnum;
import org.jboss.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WebScrappingService {

	private static final String ENTREGA_DA_PROPOSTA = "ENTREGA_DA_PROPOSTA";
	private static final String EDITAL_A_PARTIR_DE = "EDITAL_A_PARTIR_DE";
	private static final String CODIGO_DA_UASG = "CÓDIGO_DA_UASG";
	private static final String TELEFONE = "TELEFONE";
	private static final String MODALIDADE = "MODALIDADE";
	private static final String ENDERECO = "ENDEREÇO";
	private static final String OBJETO = "OBJETO";
	private static final String FAX = "FAX";
	private static final String ORGAO = "ORGAO";
	private static final String FIRST_ELEMENT_WEB_IDENTIFIER = "1";
	private static final String URL_SISTEMA_WEB = "http://comprasnet.gov.br/ConsultaLicitacoes/ConsLicitacaoDia.asp?pagina=%s";

	private final LicitacaoService licitacaoService;
	private static final Logger logger = Logger.getLogger(WebScrappingService.class);

	public WebScrappingService(LicitacaoService licitacaoService) {
		this.licitacaoService = licitacaoService;
	}

	public List<Licitacao> extraiDadosDaWeb() throws IOException {
		Elements elementos = obtemElementosDaWeb();
		List<Map<String, String>> dadosExtraidos = mapeiaDadosExtraidos(elementos);
		List<Licitacao> licitacoesConvertidas = converteDadosExtraidos(dadosExtraidos);

		return licitacaoService.salvaLicitacoes(licitacoesConvertidas);
	}

	private List<Licitacao> converteDadosExtraidos(List<Map<String, String>> dadosExtraidos) {
		return dadosExtraidos.stream().map(dadosAgrupadosPorChaveValor -> {
			Licitacao licitacao = new Licitacao();
			licitacao.setEndereco(dadosAgrupadosPorChaveValor.get(ENDERECO).trim());
			licitacao.setFax(dadosAgrupadosPorChaveValor.get(FAX).trim());
			licitacao.setModalidade(dadosAgrupadosPorChaveValor.get(MODALIDADE).trim());
			licitacao.setOrgao(dadosAgrupadosPorChaveValor.get(ORGAO).trim());
			licitacao.setCodigoUASG(dadosAgrupadosPorChaveValor.get(CODIGO_DA_UASG).trim());
			licitacao.setDataEntregaProposta(dadosAgrupadosPorChaveValor.get(ENTREGA_DA_PROPOSTA).trim());
			licitacao.setDataInicioEdital(dadosAgrupadosPorChaveValor.get(EDITAL_A_PARTIR_DE).trim());
			licitacao.setTelefone(dadosAgrupadosPorChaveValor.get(TELEFONE).trim());
			licitacao.setObjeto(dadosAgrupadosPorChaveValor.get(OBJETO).trim());
			licitacao.setStatus(StatusEnum.NAO_LIDO);
			return licitacao;
		}).collect(Collectors.toList());
	}

	private Elements obtemElementosDaWeb() throws IOException {
		logger.info("Buscando elementos da web...");
		List<Document> documentos = obtemDocumentosDaWeb();
		Elements elementosEncontrados = new Elements();

		documentos.stream()
			.map(document -> document.select("table tr td form"))
			.forEach(elementosEncontrados::addAll);

		logger.infof("%d elementos encontrados.", elementosEncontrados.size());
		return elementosEncontrados;
	}

	private List<Document> obtemDocumentosDaWeb() throws IOException {
		List<Document> documentos = new ArrayList<>();

		for (int pagina = 1;;pagina++) {
			Document documento = Jsoup.connect(String.format(URL_SISTEMA_WEB, pagina)).get();
			String elementWebIdentifier = documento.select("table tr td form")
				.select("tbody tr td")
				.get(0)
				.toString()
				.replaceAll("[<tr>|</td>]", "");

			if (!documentos.isEmpty() && elementWebIdentifier.equals(FIRST_ELEMENT_WEB_IDENTIFIER)) {
				break;
			}

			documentos.add(documento);
		}
		return  documentos;
	}

	private List<Map<String, String>> mapeiaDadosExtraidos(Elements elementos) {
		List<Map<String, String>> dadosAgrupadosPorChaveValor = new ArrayList<>();

		for (Element elemento : elementos) {
			List<String> atributosDoElemento = obtemAtributosDoElemento(elemento);
			String elementosTrataveis = atributosDoElemento.get(1);
			atributosDoElemento = removeElementosDesnecessarios(atributosDoElemento);
			List<String> elementosTratados = List.of(obtemCodigoDaUASG(elementosTrataveis), obtemOrgao(elementosTrataveis));
			atributosDoElemento.addAll(elementosTratados);

			Map<String, String> elementItemsFinal = atributosDoElemento.stream()
                .map(this::formataItem)
				.collect(Collectors.toMap(this::obtemChaveDoItem, this::obtemValorDoItem));
			dadosAgrupadosPorChaveValor.add(elementItemsFinal);
		}
		return dadosAgrupadosPorChaveValor;
	}

	private List<String> removeElementosDesnecessarios(List<String> atributosDoElemento) {
		atributosDoElemento.remove(0);
		atributosDoElemento.remove(0);
		return atributosDoElemento;
	}

	private List<String> obtemAtributosDoElemento(Element elemento) {
		return new ArrayList<>(List.of(elemento.select("table tbody tr td").get(1)
			.toString()
			.split("<b>")));
	}

	private String obtemChaveDoItem(String item) {
		return item.split(":")[0].replaceAll(" ", "_").toUpperCase();
	}

	private String obtemValorDoItem(String item) {
		StringBuilder stringCompleta = new StringBuilder();
		boolean podeIncrementar = false;
		for (char charEscolhido : item.toCharArray()) {
			if (podeIncrementar) {
				stringCompleta.append(charEscolhido);
			}
			if (charEscolhido == ':') {
				podeIncrementar = true;
			}
		}
		return stringCompleta.toString();
	}

	private String obtemOrgao(String elementosAgrupados) {
		List<String> elementosSeparadosPorLinha = List.of(elementosAgrupados.split("\n"));
		String orgaoAgrupado = elementosSeparadosPorLinha.stream()
			.filter(item -> !item.contains("Código da UASG"))
			.collect(Collectors.joining(", "));
		return String.format("Orgao: %s", orgaoAgrupado);
	}

	private String obtemCodigoDaUASG(String elementosAgrupados) {
		List<String> elementosSeparadosPorLinha = List.of(elementosAgrupados.split("\n"));
		return elementosSeparadosPorLinha.stream()
			.filter(item -> item.contains("Código da UASG"))
			.findFirst().get();
	}

    private String formataItem(String item) {
        if (!item.contains(":")) {
            item = "Modalidade:" + item;
        }
        if (item.contains("Entrega da Proposta")) {
            item = item.substring(0, 51);
        }
        return item.replaceAll("Objeto:</b>&nbsp; |<br>|<b>|</b>|</br>|&nbsp;|<span>|</span>|  |<span class=\"mensagem\">", "");
    }
}
