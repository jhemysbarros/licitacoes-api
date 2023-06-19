package br.com.licitacoesapi.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Licitacao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String orgao;
	private String codigoUASG;
	private String modalidade;
	private String objeto;
	private LocalDateTime dataInicioEdital;
	private String endereco;
	private String telefone;
	private String fax;
	private LocalDateTime dataEntregaProposta;
	@Enumerated(EnumType.STRING)
	private StatusEnum status;
}
