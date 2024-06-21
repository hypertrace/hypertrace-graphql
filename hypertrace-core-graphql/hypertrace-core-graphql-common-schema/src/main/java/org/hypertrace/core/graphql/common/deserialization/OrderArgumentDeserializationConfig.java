package org.hypertrace.core.graphql.common.deserialization;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.util.List;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.common.schema.attributes.arguments.AttributeExpression;
import org.hypertrace.core.graphql.common.schema.results.arguments.order.OrderArgument;
import org.hypertrace.core.graphql.common.schema.results.arguments.order.OrderDirection;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializationConfig;

class OrderArgumentDeserializationConfig implements ArgumentDeserializationConfig {

  @Override
  public String getArgumentKey() {
    return OrderArgument.ARGUMENT_NAME;
  }

  @Override
  public Class<OrderArgument> getArgumentSchema() {
    return OrderArgument.class;
  }

  @Override
  public List<Module> jacksonModules() {
    return List.of(
        new SimpleModule().addAbstractTypeMapping(OrderArgument.class, DefaultOrderArgument.class));
  }

  @Value
  @Accessors(fluent = true)
  @NoArgsConstructor(force = true)
  private static class DefaultOrderArgument implements OrderArgument {
    @JsonProperty(ORDER_DIRECTION_NAME)
    OrderDirection direction = OrderDirection.DESC;

    @JsonProperty(ORDER_KEY_NAME)
    String key;

    @JsonProperty(ORDER_KEY_EXPRESSION_NAME)
    AttributeExpression keyExpression;
  }
}
