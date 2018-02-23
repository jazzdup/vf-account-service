package com.vodafone.charging.accountservice;

import com.vodafone.charging.accountservice.domain.ChargingId;
import com.vodafone.charging.accountservice.domain.model.Account;
import com.vodafone.charging.accountservice.repository.AccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.*;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.LocalDate;
import java.util.Date;
import java.util.Random;

import static com.google.common.collect.Lists.newArrayList;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * Spring Boot Application class
 */
@SpringBootApplication
@Configuration
//@ComponentScan(basePackages = "com.vodafone.charging")
@EnableSwagger2
public class AccountServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountServiceApplication.class, args);
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.vodafone.charging.accountservice"))
                .paths(PathSelectors.any())
                .build()
                .directModelSubstitute(LocalDate.class,
                        String.class)
                .apiInfo(apiInfo())
                .useDefaultResponseMessages(false)
                .globalResponseMessage(RequestMethod.POST, newArrayList(new ResponseMessageBuilder().code(INTERNAL_SERVER_ERROR.value())
                        .message(INTERNAL_SERVER_ERROR.getReasonPhrase())
                        .responseModel(new ModelRef("AccountServiceError")).build()));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Vodafone Account Service")
                .contact(new Contact("Ravi Aghera", "www.vodafone.com", "ravi.aghera@vodafone.com"))
                .description("REST Api to enrich Vodafone Account Information via interaction with OPCO and Partner Services")
                .build();

    }

    @Bean
    CommandLineRunner init(AccountRepository accountRepository) {
        return (args) ->
                newArrayList("al-pacino-" + new Random().nextInt(),
                        "robert-deniro-" + new Random().nextInt(),
                        "joe-pesci" + new Random().nextInt()).forEach(id ->
                        accountRepository.save(Account.builder()
                                .id(id)
                                .chargingId(new ChargingId.Builder().type(ChargingId.Type.MSISDN)
                                        .value(String.valueOf(new Random().nextInt())).build())
                                .customerType("PRE")
                                .lastValidate(new Date())
                                .build()));
    }

    @Bean
    public UiConfiguration uiConfig() {
        return UiConfigurationBuilder.builder()
                .deepLinking(true)
                .displayOperationId(false)
                .defaultModelsExpandDepth(10)
                .defaultModelExpandDepth(10)
                .defaultModelRendering(ModelRendering.MODEL)
                .displayRequestDuration(false)
                .docExpansion(DocExpansion.LIST)
                .filter(false)
                .maxDisplayedTags(null)
                .operationsSorter(OperationsSorter.ALPHA)
                .showExtensions(false)
                .tagsSorter(TagsSorter.ALPHA)
                .validatorUrl(null)
                .build();
    }

}

