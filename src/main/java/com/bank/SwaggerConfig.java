/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bank;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
            .apiInfo(apiInfo())
            .useDefaultResponseMessages(false)
            .select()          
            .build();
    }

     private ApiInfo apiInfo() {
         return new ApiInfoBuilder()
                 .title("Transfer Money API")
                 .description("Transfer Money API reference for developers")
                 .termsOfServiceUrl("http://dummy.com")
                 .version("1.0")
                 .build();
     }

 }
