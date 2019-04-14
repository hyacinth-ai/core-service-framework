package ai.hyacinth.core.service.gateway.server.support;

import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalConverter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

//@Component
//@ConfigurationPropertiesBinding
//@Slf4j
public class DataURIStringConverter implements Converter<String, byte[]>, ConditionalConverter {
  public DataURIStringConverter() {}

  @Override
  public byte[] convert(String source) {
    if (source.startsWith("data:")) {
      int comma = source.indexOf(',');
      String base64 = source.substring(comma + 1);
      return Base64.getDecoder().decode(base64);
    } else {
      return source.getBytes();
    }
  }

  @Override
  public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
    return true;
  }
}
