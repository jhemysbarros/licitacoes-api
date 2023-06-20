package br.com.licitacoesapi.api.impl;

import br.com.licitacoesapi.converter.LicitacaoConverter;
import br.com.licitacoesapi.model.Licitacao;
import br.com.licitacoesapi.model.StatusEnum;
import br.com.licitacoesapi.service.LicitacaoService;
import io.swagger.api.LicitacoesApi;
import io.swagger.model.LicitacaoRequest;
import io.swagger.model.LicitacaoResponse;
import io.swagger.model.LicitacoesPaginada;
import org.jboss.logging.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/v1/licitacoes")
public class LicitacoesApiImpl implements LicitacoesApi {
	private static final int DEFAULT_SIZE = 20;
	private static final int DEFAULT_PAGE = 0;

	private final LicitacaoService licitacaoService;
	private final LicitacaoConverter licitacaoConverter;
	private final Logger logger = Logger.getLogger(this.getClass());

	public LicitacoesApiImpl(LicitacaoService licitacaoService, LicitacaoConverter licitacaoConverter) {
		this.licitacaoService = licitacaoService;
		this.licitacaoConverter = licitacaoConverter;
	}

	@Override
	@GetMapping
	public ResponseEntity<LicitacoesPaginada> buscaLicitacoes(
		@RequestParam(value = "size", required = false) Integer size,
		@RequestParam(value = "page", required = false) Integer page,
		@RequestParam(value = "status", required = false) String status,
		@RequestParam(value = "dataInicioEdital", required = false) String dataInicioEdital,
		@RequestParam(value = "dataEntregaProposta", required = false) String dataEntregaProposta
	) {
		logger.info("Buscando licitações");
		int pageSize = Optional.ofNullable(size).orElse(DEFAULT_SIZE);
		int pageNumber = Optional.ofNullable(page).orElse(DEFAULT_PAGE);
		StatusEnum statusFornecido = Objects.nonNull(status) ? StatusEnum.valueOf(status) : null;
		LocalDate dataInicioFornecida = Objects.nonNull(dataInicioEdital) ? LocalDate.parse(dataInicioEdital) : null;
		LocalDateTime dataEntregaFornecida = Objects.nonNull(dataEntregaProposta) ? LocalDateTime.parse(dataEntregaProposta) : null;
		Page<Licitacao> paginaDeLicitacoes = licitacaoService.buscaLicitacoes(
			statusFornecido,
			dataInicioFornecida,
			dataEntregaFornecida,
			PageRequest.of(pageNumber, pageSize)
		);
		LicitacoesPaginada licitacoesPaginada = licitacaoConverter.toResponsePaginada(paginaDeLicitacoes);
		return ResponseEntity.ok(licitacoesPaginada);
	}

	@Override
	@PatchMapping("/{id}")
	public ResponseEntity<LicitacaoResponse> atualizaLicitacao(
		@PathVariable("id") Long id,
		@RequestBody LicitacaoRequest licitacaoRequest
	) {
		logger.infof("Atualizando licitação... %s", id);
		Licitacao licitacao = licitacaoConverter.toDomain(id, licitacaoRequest);
		Licitacao licitacaoAtualizada = licitacaoService.atualizaLicitacao(licitacao);
		return ResponseEntity.ok(licitacaoConverter.toResponse(licitacaoAtualizada));
	}
}
