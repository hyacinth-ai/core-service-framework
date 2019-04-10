package ai.hyacinth.core.service.jpa.hibernate.dialect;

import org.hibernate.dialect.MySQL57Dialect;

public class MySQLDialect extends MySQL57Dialect {
  @Override
  public String getTableTypeString() {
    return " ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
  }
}
