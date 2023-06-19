package br.com.licitacoesapi.scheduler;

import br.com.licitacoesapi.service.WebScrappingService;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class WebScrappingScheduler {

	private final Logger logger = Logger.getLogger(this.getClass());
	private final WebScrappingService webScrappingService;

	private static final String TIME_ZONE = "America/Sao_Paulo";
	@Value("${cron.extrai-dados-da-web.job}")
	private static final String CRON_EXTRAI_DADOS_DA_WEB_JOB = "0 5 * * * *";

	public WebScrappingScheduler(WebScrappingService webScrappingService) {
		this.webScrappingService = webScrappingService;
	}

	@Scheduled(cron = CRON_EXTRAI_DADOS_DA_WEB_JOB, zone = TIME_ZONE)
	private void extraiDadosDaWeb() {
		logger.info("Extraindo dados da Web...");
		webScrappingService.extraiDadosDaWeb();
		logger.infof("Dados da Web extraídos com succeso.");
	}
}
