package ai.hyacinth.core.service.jpa.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Converter
@NoArgsConstructor
public abstract class AbstractJsonConverter<T> implements AttributeConverter<T, String> {
  @Autowired
  private ObjectMapper objectMapper;

  @SuppressWarnings("unchecked")
  protected Class<T> targetClass = (Class<T>) Map.class;

  public AbstractJsonConverter(Class<T> targetClass) {
    this.targetClass = targetClass;
  }

  @Override
  public String convertToDatabaseColumn(T data) {
    try {
      return objectMapper.writeValueAsString(data);
    } catch (final JsonProcessingException e) {
      log.error("JSON writing error", e);
      throw new UnsupportedOperationException(e);
    }
  }

  @Override
  public T convertToEntityAttribute(String dataJson) {
    try {
      return objectMapper.readValue(dataJson, targetClass);
    } catch (final IOException e) {
      log.error("JSON reading error", e);
      throw new UnsupportedOperationException(e);
    }
  }
}
