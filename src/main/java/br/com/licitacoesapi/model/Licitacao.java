package br.com.licitacoesapi.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Getter
@Setter
@Entity
public class Licitacao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(length = 500)
	private String orgao;
	private String codigoUASG;
	private String modalidade;
	@Column(length = 1000)
	private String objeto;
	@Setter(AccessLevel.NONE)
	private LocalDate dataInicioEdital;
	@Setter(AccessLevel.NONE)
	private String dataInicioEditalDetalhada;
	private String endereco;
	private String telefone;
	private String fax;
	@Setter(AccessLevel.NONE)
	private LocalDateTime dataEntregaProposta;
	@Setter(AccessLevel.NONE)
	private String dataEntregaPropostaDetalhada;
	@Enumerated(EnumType.STRING)
	private StatusEnum status;

	public void setDataInicioEdital(String dataInicioEditalDetalhada) {
		if (Objects.nonNull(dataInicioEditalDetalhada) && !dataInicioEditalDetalhada.isBlank()) {
			String dataExtraida = dataInicioEditalDetalhada.substring(0, 10);
			this.dataInicioEdital = LocalDate.parse(dataExtraida, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		}
		this.dataInicioEditalDetalhada = dataInicioEditalDetalhada;
	}

	public void setDataEntregaProposta(String dataEntregaPropostaDetalhada) {
		if (Objects.nonNull(dataEntregaPropostaDetalhada) && !dataEntregaPropostaDetalhada.isBlank()) {
			String dataPreparadaParaFormatacao = dataEntregaPropostaDetalhada
				.replaceAll("[Hs| ]", "")
				.replaceAll("[às]", " ");
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
			this.dataEntregaProposta = LocalDateTime.parse(dataPreparadaParaFormatacao, dateTimeFormatter);
		}
		this.dataEntregaPropostaDetalhada = dataEntregaPropostaDetalhada;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Licitacao)) return false;
		Licitacao licitacao = (Licitacao) o;

		return Objects.equals(orgao, licitacao.getOrgao())
			&& Objects.equals(codigoUASG, licitacao.getCodigoUASG())
			&& Objects.equals(modalidade, licitacao.getModalidade());
	}

	@Override
	public int hashCode() {
		return Objects.hash(
			orgao,
			codigoUASG,
			modalidade
		);
	}
}
