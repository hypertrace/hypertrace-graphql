package org.hypertrace.core.graphql.log.event.request;

import java.util.Collection;
import java.util.List;
import org.hypertrace.core.graphql.common.request.AttributeAssociation;
import org.hypertrace.core.graphql.common.request.AttributeRequest;
import org.hypertrace.core.graphql.common.request.ContextualRequest;
import org.hypertrace.core.graphql.common.schema.arguments.TimeRangeArgument;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterArgument;
import org.hypertrace.core.graphql.common.schema.results.arguments.order.OrderArgument;

public interface LogEventRequest extends ContextualRequest {
  // All attributes to fetch
  Collection<AttributeRequest> attributes();

  TimeRangeArgument timeRange();

  int limit();

  int offset();

  List<AttributeAssociation<OrderArgument>> orderArguments();

  Collection<AttributeAssociation<FilterArgument>> filterArguments();
}
