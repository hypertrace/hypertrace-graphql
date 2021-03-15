package org.hypertrace.graphql.explorer.request;

import java.util.Optional;
import org.hypertrace.core.graphql.attributes.AttributeModel;
import org.hypertrace.graphql.metric.schema.argument.AggregatableOrderArgument;

public interface ExploreOrderArgument {

  ExploreOrderArgumentType type();

  AggregatableOrderArgument argument();

  Optional<AttributeModel> attribute();

  enum ExploreOrderArgumentType {
    ATTRIBUTE,
    INTERVAL_START
  }
}
