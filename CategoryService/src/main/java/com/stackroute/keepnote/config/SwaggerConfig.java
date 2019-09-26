package com.stackroute.keepnote.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import com.google.common.base.Predicate;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import static springfox.documentation.builders.PathSelectors.regex;
import static com.google.common.base.Predicates.or;


/*As in this class we are implementing Swagger So annotate the class with @Configuration and 
 * @EnableSwagger2
 * 
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

	/*
	 * Annotate this method with @Bean . This method will return an Object of Docket.
	 * This method will implement logic for swagger
	 */
	@Bean
	public Docket postsApi() {
		return new Docket(DocumentationType.SWAGGER_2).groupName("public-api")
				.apiInfo(apiInfo()).select().paths(postPaths()).build();
	}

	private Predicate<String> postPaths() {
		return or(regex("/api/posts.*"), regex("/api/v1.*"));
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("Step5 Assignment")
				.description("Swagger Example for Step5 Assignment")
				.termsOfServiceUrl("http://stackroute.in")
				.contact("santhosh.devadasan@in.ibm.com").license("Internal License")
				.licenseUrl("santhosh.devadasan@in.ibm.com").version("1.0").build();
	}


	
}
