package tech.buildrun.btgpactual.orderms.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
  private static final String ORDER_CREATED_QUEUE = "btg-pactual-order-created";
}
