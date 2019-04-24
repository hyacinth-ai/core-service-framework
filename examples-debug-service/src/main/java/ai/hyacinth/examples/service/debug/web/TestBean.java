package ai.hyacinth.examples.service.debug.web;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TestBean {
  private String text;

  public TestBean(String text) {
    this.text = text;
  }
}
