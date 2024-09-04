package tech.buildrun.btgpactual.orderms.services;

import lombok.AllArgsConstructor;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import tech.buildrun.btgpactual.orderms.controllers.dto.OrderResponse;
import tech.buildrun.btgpactual.orderms.entity.OrderEntity;
import tech.buildrun.btgpactual.orderms.entity.OrderItem;
import tech.buildrun.btgpactual.orderms.listener.dto.OrderCreatedEvent;
import tech.buildrun.btgpactual.orderms.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
@AllArgsConstructor
public class OrderService {
  private final OrderRepository repository;
  private final MongoTemplate mongoTemplate;

  public void save(OrderCreatedEvent event) {
    var entity = new OrderEntity();

    entity.setOrderId(event.codigoPedido());
    entity.setCustomerId(event.codigoCliente());
    entity.setItems(getOrderItems(event));
    entity.setTotal(getTotal(event));

    repository.save(entity);
  }

  public Page<OrderResponse> findAllByCustomerId(Long customerId, PageRequest pageRequest) {
    Page<OrderEntity> orders = repository.findAllByCustomerId(customerId, pageRequest);

    return orders.map(OrderResponse::fromEntity);
  }

  public BigDecimal findTotalOnOrdersByCustomerId(Long customerId) {
    var aggregation = newAggregation(
        match(Criteria.where("customerId").is(customerId)),
        group().sum("total").as("total")
    );

    var response = mongoTemplate.aggregate(aggregation, "tb_orders", Document.class);

    return new BigDecimal(Objects.requireNonNull(response.getUniqueMappedResult()).get("total").toString());
  }

  private static List<OrderItem> getOrderItems(OrderCreatedEvent event) {
    return event.itens().stream()
        .map(i -> new OrderItem(i.produto(), i.quantidade(), i.preco()))
        .toList();
  }

  private BigDecimal getTotal(OrderCreatedEvent event) {
    return event.itens()
        .stream()
        .map(i -> i.preco().multiply(BigDecimal.valueOf(i.quantidade())))
        .reduce(BigDecimal::add)
        .orElse(BigDecimal.ZERO);
  }
}
