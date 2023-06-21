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
	private static final String TARGET_WEB_URL = "http://comprasnet.gov.br/ConsultaLicitacoes/ConsLicitacaoDia.asp?pagina=%s";

	private final LicitacaoService licitacaoService;
	private static final Logger logger = Logger.getLogger(WebScrappingService.class);

	public WebScrappingService(LicitacaoService licitacaoService) {
		this.licitacaoService = licitacaoService;
	}

	public List<Licitacao> extraiDadosDaWeb() throws IOException {
		Elements elementos = retrieveElementsFromWeb();
		List<Map<String, String>> dadosExtraidos = mapeiaDadosExtraidos(elementos);
		List<Licitacao> licitacoesConvertidas = converteDadosExtraidos(dadosExtraidos);

		return licitacaoService.salvaLicitacoes(licitacoesConvertidas);
	}

	private List<Licitacao> converteDadosExtraidos(List<Map<String, String>> dadosExtraidos) {
		return dadosExtraidos.stream().map((keyValues) -> {
			Licitacao licitacao = new Licitacao();
			licitacao.setEndereco(keyValues.get(ENDERECO).trim());
			licitacao.setFax(keyValues.get(FAX).trim());
			licitacao.setModalidade(keyValues.get(MODALIDADE).trim());
			licitacao.setOrgao(keyValues.get(ORGAO).trim());
			licitacao.setCodigoUASG(keyValues.get(CODIGO_DA_UASG).trim());
			licitacao.setDataEntregaProposta(keyValues.get(ENTREGA_DA_PROPOSTA).trim());
			licitacao.setDataInicioEdital(keyValues.get(EDITAL_A_PARTIR_DE).trim());
			licitacao.setTelefone(keyValues.get(TELEFONE).trim());
			licitacao.setObjeto(keyValues.get(OBJETO).trim());
			licitacao.setStatus(StatusEnum.NAO_LIDO);
			return licitacao;
		}).collect(Collectors.toList());
	}

	private Elements retrieveElementsFromWeb() throws IOException {
		logger.info("Buscando elementos da web...");
		List<Document> documents = new ArrayList<>();
		Elements elementsFound = new Elements();
		for (int pagina = 1;;pagina++) {
			Document document = Jsoup.connect(String.format(TARGET_WEB_URL, pagina)).get();
			String elementWebIdentifier = document.select("table tr td form")
				.select("tbody tr td")
				.get(0)
				.toString()
				.replaceAll("[<tr>|</td>]", "");
			if (!documents.isEmpty() && elementWebIdentifier.equals(FIRST_ELEMENT_WEB_IDENTIFIER)) {
				break;
			}
			documents.add(document);
		}
		documents.stream()
			.map(document -> document.select("table tr td form"))
			.forEach(elementsFound::addAll);

		logger.infof("%d elementos encontrados.", elementsFound.size());
		return elementsFound;
	}

	private List<Map<String, String>> mapeiaDadosExtraidos(Elements elements) {
		List<Map<String, String>> elementKeyValues = new ArrayList<>();

		for (Element element : elements) {
			List<String> elementItems = new ArrayList<>(List.of(element.select("table tbody tr td").get(1).toString()
				.split("<b>")));
			String linhaDefeituosa = elementItems.get(1);
			elementItems.remove(1);
			String orgao = new ArrayList<>(List.of(linhaDefeituosa.split("\n")))
				.stream()
				.filter(item -> {
					if (item.contains("Código da UASG")) {
						elementItems.add(item);
					}
					return !item.contains("Código da UASG");
				})
				.collect(Collectors.joining(", "));

			elementItems.add("Orgao: " + orgao);
			elementItems.remove(0);
			Map<String, String> elementItemsFinal = elementItems.stream()
                .map(this::formatarItem)
				.collect(Collectors.toMap((string) -> string.split(":")[0].replaceAll(" ", "_").toUpperCase(), (string) -> {
					StringBuilder stringCompleta = new StringBuilder();
					boolean podeIncrementar = false;
					for (char charEscolhido : string.toCharArray()) {
						if (podeIncrementar) {
							stringCompleta.append(charEscolhido);
						}
						if (charEscolhido == ':') {
							podeIncrementar = true;
						}
					}
					return stringCompleta.toString();
				}));
			elementKeyValues.add(elementItemsFinal);
		}
		return elementKeyValues;
	}

    private String formatarItem(String item) {
        if (!item.contains(":")) {
            item = "Modalidade:" + item;
        }
        if (item.contains("Entrega da Proposta")) {
            item = item.substring(0, 51);
        }
        return item.replaceAll("Objeto:</b>&nbsp; |<br>|<b>|</b>|</br>|&nbsp;|<span>|</span>|  |<span class=\"mensagem\">", "");
    }
}