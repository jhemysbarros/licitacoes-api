package br.com.licitacoesapi.service;

import br.com.licitacoesapi.model.Licitacao;
import br.com.licitacoesapi.repository.LicitacaoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LicitacaoService {

	private final LicitacaoRepository licitacaoRepository;

	public LicitacaoService(LicitacaoRepository licitacaoRepository) {
		this.licitacaoRepository = licitacaoRepository;
	}

	public Page<Licitacao> buscaLicitacoes(PageRequest page) {
		return licitacaoRepository.findAll(page);
	}

	public Licitacao atualizaLicitacao(Licitacao licitacao) {
		Licitacao licitacaoEncontrada = buscaLicitacao(licitacao.getId());
		Optional.ofNullable(licitacao.getStatus()).ifPresent(licitacaoEncontrada::setStatus);
		return licitacaoRepository.save(licitacaoEncontrada);
	}

	private Licitacao buscaLicitacao(Long id) {
		return licitacaoRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Licitação não encontrada"));
	}
}
