package org.hypertrace.core.graphql.common.request;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.hypertrace.core.graphql.common.schema.arguments.TimeRangeArgument;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterArgument;
import org.hypertrace.core.graphql.common.schema.results.arguments.order.OrderArgument;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;

public interface ResultSetRequest<O extends OrderArgument> {
  GraphQlRequestContext context();

  // All attributes to fetch, including ID
  Collection<AttributeRequest> attributes();

  TimeRangeArgument timeRange();

  AttributeRequest idAttribute();

  int limit();

  int offset();

  List<AttributeAssociation<O>> orderArguments();

  Collection<AttributeAssociation<FilterArgument>> filterArguments();

  Optional<String> spaceId();
}
