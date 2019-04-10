package ai.hyacinth.core.service.gateway.server.config;

import ai.hyacinth.core.service.gateway.server.configprops.ResponsePostProcessingType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalConverter;
import org.springframework.core.convert.converter.Converter;

//@Component
//@ConfigurationPropertiesBinding
//@Order
@Slf4j
public class ResponsePostProcessingTypeConverter
    implements Converter<String, ResponsePostProcessingType>, ConditionalConverter {
  public ResponsePostProcessingTypeConverter() {
    log.info("start create converter");
  }
  @Override
  public ResponsePostProcessingType convert(String source) {
    return ResponsePostProcessingType.valueOf(source.toUpperCase());
  }

  @Override
  public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
    boolean matched = targetType.getObjectType().equals(ResponsePostProcessingType.class);
    if (matched) {
      log.info("find match");
    }
    return matched;
  }
}
