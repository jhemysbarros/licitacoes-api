package br.com.licitacoesapi.repository;

import br.com.licitacoesapi.model.Licitacao;
import br.com.licitacoesapi.model.StatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface LicitacaoRepository extends JpaRepository<Licitacao, Long> {
	@Query("SELECT l FROM Licitacao l WHERE " +
		"(:status IS NULL OR l.status = :status) AND " +
		"(:dataInicio IS NULL OR l.dataInicioEdital = :dataInicio) AND " +
		"(:dataProposta IS NULL OR l.dataEntregaProposta = :dataProposta)")
	Page<Licitacao> findAllByStatusAndDataInicioEditalAndDataEntregaPropostaOOrCodigoUASG(StatusEnum status, LocalDate dataInicio, LocalDateTime dataProposta, Pageable pageable);
}
