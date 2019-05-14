package ai.hyacinth.core.service.bus.support.config;

import ai.hyacinth.core.service.bus.support.event.BusEvent;
import ai.hyacinth.core.service.bus.support.service.BusService;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.cloud.bus.jackson.RemoteApplicationEventScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@RemoteApplicationEventScan(basePackageClasses = BusEvent.class)
@ComponentScan(basePackageClasses = BusService.class)
public class BusConfig {
//  @Bean
//  public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
//    return new RabbitAdmin(connectionFactory) {
//      @Override
//      public void initialize() {
//        while (true) { // might want to give up after some number of tries
//          try {
//            super.initialize();
//            break;
//          }
//          catch (Exception e) {
//            System.out.println("Failed to declare elements: " + e.getCause().getCause().getMessage());
//            try {
//              Thread.sleep(1_000);
//            }
//            catch (InterruptedException e1) {
//              Thread.currentThread().interrupt();
//            }
//          }
//        }
//      }
//    };
//  }
}
