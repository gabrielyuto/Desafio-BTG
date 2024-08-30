package tech.buildrun.btgpactual.orderms.entity;

import java.math.BigDecimal;

import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderItem {
  private String product;
  private Integer quantity;

  @Field(targetType = FieldType.DECIMAL128)
  private BigDecimal price;
}
