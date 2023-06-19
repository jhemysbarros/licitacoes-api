package br.com.licitacoesapi.repository;

import br.com.licitacoesapi.model.Licitacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LicitacaoRepository extends JpaRepository<Licitacao, Long> {
}
