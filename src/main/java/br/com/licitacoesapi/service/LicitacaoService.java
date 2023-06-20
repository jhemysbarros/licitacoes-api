package br.com.licitacoesapi.service;

import br.com.licitacoesapi.model.Licitacao;
import br.com.licitacoesapi.model.StatusEnum;
import br.com.licitacoesapi.repository.LicitacaoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LicitacaoService {

	private final LicitacaoRepository licitacaoRepository;

	public LicitacaoService(LicitacaoRepository licitacaoRepository) {
		this.licitacaoRepository = licitacaoRepository;
	}

	public Page<Licitacao> buscaLicitacoes(StatusEnum status, LocalDate dataInicioEdital, LocalDateTime dataEntregaProposta, PageRequest page) {
		return licitacaoRepository.findAllByStatusAndDataInicioEditalAndDataEntregaPropostaOOrCodigoUASG(status, dataInicioEdital, dataEntregaProposta, page);
	}

	public Licitacao atualizaLicitacao(Licitacao licitacao) {
		Licitacao licitacaoEncontrada = buscaLicitacao(licitacao.getId());
		Optional.ofNullable(licitacao.getStatus()).ifPresent(licitacaoEncontrada::setStatus);
		return licitacaoRepository.save(licitacaoEncontrada);
	}

	public List<Licitacao> salvaLicitacoes(List<Licitacao> licitacoes) {
		if (licitacoes.isEmpty()) {
			return Collections.emptyList();
		}
		List<Licitacao> licitacoesExistentes = licitacaoRepository.findAll();

		return licitacoes.stream()
			.filter(licitacao -> !licitacoesExistentes.contains(licitacao))
			.map(licitacaoRepository::save)
			.collect(Collectors.toList());
	}

	private Licitacao buscaLicitacao(Long id) {
		return licitacaoRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Licitação não encontrada"));
	}
}
