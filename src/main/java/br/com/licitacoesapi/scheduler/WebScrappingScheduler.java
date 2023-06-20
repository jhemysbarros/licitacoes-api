package br.com.licitacoesapi.scheduler;

import br.com.licitacoesapi.model.Licitacao;
import br.com.licitacoesapi.service.WebScrappingService;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@EnableScheduling
public class WebScrappingScheduler {

	private final Logger logger = Logger.getLogger(this.getClass());
	private final WebScrappingService webScrappingService;

	private static final String TIME_ZONE = "America/Sao_Paulo";
	@Value("${cron.extrai-dados-da-web.job}")
	private static final String CRON_EXTRAI_DADOS_DA_WEB_JOB = "0 2 * * * *";

	public WebScrappingScheduler(WebScrappingService webScrappingService) {
		this.webScrappingService = webScrappingService;
	}

	@Scheduled(cron = CRON_EXTRAI_DADOS_DA_WEB_JOB, zone = TIME_ZONE)
	private void extraiDadosDaWeb() throws IOException {
		logger.info("Extraindo dados da Web para o banco de dados...");
		List<Licitacao> licitacoesSalvas = webScrappingService.extraiDadosDaWeb();
		logger.infof("%d licitações foram extraídas da Web para o banco de dados.", licitacoesSalvas.size());
	}
}
