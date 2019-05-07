package ai.hyacinth.core.service.module.loader;

import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringUtils;

@Slf4j
@Order
public class ConfigLoaderPostProcessor implements EnvironmentPostProcessor {

  private static final String DOCUMENT_PROFILES_PROPERTY = "spring.profiles";

  private String configLocation;

  public ConfigLoaderPostProcessor() {
    detectConfigLocation();
  }

  public ConfigLoaderPostProcessor(String configLocation) {
    this.configLocation = configLocation;
  }

  @Override
  public void postProcessEnvironment(
      ConfigurableEnvironment environment, SpringApplication application) {
    ResourceLoader resourceLoader = new DefaultResourceLoader();
    Resource resource = resourceLoader.getResource(configLocation);
    YamlPropertySourceLoader sourceLoader = new YamlPropertySourceLoader();
    final String name = String.format("ServiceModuleConfiguration [%s]", configLocation);
    try {
      List<PropertySource<?>> yalPropertiesList = sourceLoader.load(name, resource);
      if (yalPropertiesList != null && yalPropertiesList.size() > 0) {
        Collections.reverse(yalPropertiesList);
        for (PropertySource propertySource : yalPropertiesList) {
          if (matchActiveProfile(
              propertySource.getProperty(DOCUMENT_PROFILES_PROPERTY),
              environment.getActiveProfiles(),
              environment.getDefaultProfiles())) {
            if (!environment.getPropertySources().contains(propertySource.getName())) {
              environment.getPropertySources().addLast(propertySource);
            }
          } else {
            log.info("profile not match. skip loading {}", propertySource);
          }
        }
      }
      log.info("load from {} successfully", configLocation);
    } catch (Exception e) {
      log.error("error to load config file:  " + configLocation);
    }
  }

  private boolean matchActiveProfile(
      Object property, String[] activeProfiles, String defaultProfiles[]) {
    if (activeProfiles.length == 0) {
      return false;
    }
    if (property == null) {
      return true; // document has no restriction
    } else {
      String docProfile = property.toString();
      for (String activeProfile : activeProfiles) {
        if (docProfile.equals(activeProfile)) {
          return true;
        }
      }
    }
    return false;
  }

  private void detectConfigLocation() {
    if (StringUtils.isEmpty(configLocation)) {
      String pkPath = this.getClass().getPackage().getName().replace(".", "/");
      configLocation = "classpath:" + pkPath + "/config.yml";
    }
  }
}
