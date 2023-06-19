package br.com.licitacoesapi.service;

import org.springframework.stereotype.Service;

@Service
public class WebScrappingService {

	private final LicitacaoService licitacaoService;

	public WebScrappingService(LicitacaoService licitacaoService) {
		this.licitacaoService = licitacaoService;
	}

	public void extraiDadosDaWeb() {

	}
}
