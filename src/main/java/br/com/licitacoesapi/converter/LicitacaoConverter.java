package br.com.licitacoesapi.converter;

import br.com.licitacoesapi.model.Licitacao;
import io.swagger.model.LicitacaoRequest;
import io.swagger.model.LicitacaoResponse;
import io.swagger.model.LicitacoesPaginada;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LicitacaoConverter {
	LicitacaoResponse toResponse(Licitacao licitacao);
	List<LicitacaoResponse> toResponse(List<Licitacao> licitacoes);
	Licitacao toDomain(Long id, LicitacaoRequest licitacaoRequest);

	default LicitacoesPaginada toResponsePaginada(Page<Licitacao> licitacoes) {
		LicitacoesPaginada licitacoesPaginada = new LicitacoesPaginada();
		licitacoesPaginada.setLicitacoes(toResponse(licitacoes.toList()));
		licitacoesPaginada.setPage(licitacoes.getNumber());
		licitacoesPaginada.setSize(licitacoes.getSize());
		licitacoesPaginada.setHasNext(licitacoes.hasNext());
		licitacoesPaginada.setTotalElements(licitacoes.getTotalElements());
		return licitacoesPaginada;
	}
}
