package ai.hyacinth.core.service.module.factory;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.IOException;
import java.util.Properties;

public class YamlPropertySourceFactory implements PropertySourceFactory {
  @Override
  public PropertySource<?> createPropertySource(String name, EncodedResource resource)
      throws IOException {
    YamlPropertiesFactoryBean factoryBean = new YamlPropertiesFactoryBean();
    factoryBean.setResources(resource.getResource());
    factoryBean.getObject();
    factoryBean.afterPropertiesSet();
    Properties propertiesFromYaml = factoryBean.getObject();

    String resourceFullName = resource.getResource().getURI().toString();
    return new PropertiesPropertySource(resourceFullName, propertiesFromYaml);
  }
}
