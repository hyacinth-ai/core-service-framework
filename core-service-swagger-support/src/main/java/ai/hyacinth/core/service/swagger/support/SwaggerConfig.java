package ai.hyacinth.core.service.swagger.support;

import com.google.common.base.Predicates;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableSwagger2
public class SwaggerConfig {

  @Value("${spring.application.name}")
  private String applicationName;

  @Value("${spring.application.version:}")
  private String applicationVersion;

  @Value("${spring.application.description:}")
  private String applicationDescription;

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .apiInfo(createApiInfo())
        .select()
        .apis(
            //                RequestHandlerSelectors.basePackage("com.appsdeveloperblog.app.ws")
            Predicates.and(
                Predicates.not(RequestHandlerSelectors.basePackage("org.springframework.boot")),
                Predicates.not(RequestHandlerSelectors.basePackage("org.springframework.cloud"))))
        .paths(PathSelectors.any())
        .build();
  }

  private ApiInfo createApiInfo() {
    return new ApiInfoBuilder()
        .title(applicationName)
        .description(applicationDescription)
        .termsOfServiceUrl("")
        .license("")
        .licenseUrl("")
        .version(applicationVersion)
        .build();
  }
}
