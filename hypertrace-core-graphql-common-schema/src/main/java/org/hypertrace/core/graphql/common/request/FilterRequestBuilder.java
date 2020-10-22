package org.hypertrace.core.graphql.common.request;

import io.reactivex.rxjava3.core.Single;
import java.util.Collection;
import java.util.List;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterArgument;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;

public interface FilterRequestBuilder {
  Single<List<AttributeAssociation<FilterArgument>>> build(
      GraphQlRequestContext requestContext,
      String scope,
      Collection<FilterArgument> filterArguments);
}
