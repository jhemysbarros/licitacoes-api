package br.com.licitacoesapi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
@EnableWebMvc
public class SwaggerConfig extends WebMvcConfigurationSupport {

	private static final String TITULO = "Licitações API";
	private static final String DESCRICAO = "Uma API para visualização e gerenciamento de licitações.";
	private static final String VERSAO = "v1";
	private static final String NOME_CONTATO = "Jhemys Barros";
	private static final String EMAIL_CONTATO = "jhemysbarros@gmail.com";
	private static final String PACOTE_BASE = "br.com.licitacoesapi";

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
			.select()
			.apis(RequestHandlerSelectors.basePackage(PACOTE_BASE))
			.paths(PathSelectors.any())
			.build()
			.apiInfo(apiInfo());
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
			.title(TITULO)
			.description(DESCRICAO)
			.version(VERSAO)
			.contact(new Contact(NOME_CONTATO, null, EMAIL_CONTATO))
			.build();
	}
}