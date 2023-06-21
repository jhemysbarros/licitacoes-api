package br.com.licitacoesapi.api.impl;

import br.com.licitacoesapi.api.impl.LicitacoesApiImpl;
import br.com.licitacoesapi.converter.LicitacaoConverter;
import br.com.licitacoesapi.model.Licitacao;
import br.com.licitacoesapi.model.StatusEnum;
import br.com.licitacoesapi.service.LicitacaoService;
import io.swagger.model.LicitacaoRequest;
import io.swagger.model.LicitacaoResponse;
import io.swagger.model.LicitacoesPaginada;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
@SpringJUnitConfig
@AutoConfigureMockMvc
class LicitacoesApiTest {

    @Test
    void testBuscaLicitacoes() {
        LicitacaoConverter licitacaoConverter = Mockito.mock(LicitacaoConverter.class);
        LicitacaoService licitacaoService = Mockito.mock(LicitacaoService.class);

        Integer size = 10;
        Integer page = 1;
        StatusEnum status = StatusEnum.NAO_LIDO;
        String dataInicioEdital = "2023-06-20";
        String dataEntregaProposta = "2023-07-05T09:00:00";

        LocalDate dataInicioFornecida = Objects.nonNull(dataInicioEdital) ? LocalDate.parse(dataInicioEdital) : null;
        LocalDateTime dataEntregaFornecida = Objects.nonNull(dataEntregaProposta) ? LocalDateTime.parse(dataEntregaProposta) : null;

        List<Licitacao> licitacoes = new ArrayList<>();

        Page<Licitacao> paginaDeLicitacoes = new PageImpl<>(licitacoes);

        Mockito.when(licitacaoService.buscaLicitacoes(Mockito.any(StatusEnum.class),
                        Mockito.any(LocalDate.class), Mockito.any(LocalDateTime.class), Mockito.any(PageRequest.class)))
                .thenReturn(paginaDeLicitacoes);

        LicitacoesApiImpl licitacaoController = new LicitacoesApiImpl(licitacaoService, licitacaoConverter);

        ResponseEntity<LicitacoesPaginada> responseEntity = licitacaoController.buscaLicitacoes(size, page, String.valueOf(status),
                dataInicioEdital, dataEntregaProposta);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        Mockito.verify(licitacaoService, Mockito.times(1)).buscaLicitacoes(Mockito.any(StatusEnum.class),
                Mockito.any(LocalDate.class), Mockito.any(LocalDateTime.class), Mockito.any(PageRequest.class));
    }

    @Test
    void testAtualizaLicitacao() {
        LicitacaoConverter licitacaoConverter = Mockito.mock(LicitacaoConverter.class);
        LicitacaoService licitacaoService = Mockito.mock(LicitacaoService.class);

        Long id = 1L;
        LicitacaoRequest licitacaoRequest = new LicitacaoRequest();

        Licitacao licitacao = new Licitacao();
        Licitacao licitacaoAtualizada = new Licitacao();

        when(licitacaoConverter.toDomain(id, licitacaoRequest)).thenReturn(licitacao);

        when(licitacaoService.atualizaLicitacao(licitacao)).thenReturn(licitacaoAtualizada);

        LicitacoesApiImpl licitacaoController = new LicitacoesApiImpl(licitacaoService, licitacaoConverter);

        ResponseEntity<LicitacaoResponse> responseEntity = licitacaoController.atualizaLicitacao(id, licitacaoRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        Mockito.verify(licitacaoConverter, Mockito.times(1)).toDomain(id, licitacaoRequest);
        Mockito.verify(licitacaoService, Mockito.times(1)).atualizaLicitacao(licitacao);
        Mockito.verify(licitacaoConverter, Mockito.times(1)).toResponse(licitacaoAtualizada);
    }
}