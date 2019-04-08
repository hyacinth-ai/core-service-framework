package ai.hyacinth.core.service.jpa.converter;

import java.util.Map;
import javax.persistence.Converter;

@Converter
public class Map2JsonConverter extends AbstractJsonConverter<Map> {
  public Map2JsonConverter() {
    super(Map.class);
  }
}
