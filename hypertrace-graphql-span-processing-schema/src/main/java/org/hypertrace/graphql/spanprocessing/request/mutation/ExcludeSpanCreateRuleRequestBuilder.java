package org.hypertrace.graphql.spanprocessing.request.mutation;

import io.reactivex.rxjava3.core.Single;
import java.util.Map;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;

public interface ExcludeSpanCreateRuleRequestBuilder {
  Single<ExcludeSpanCreateRuleRequest> build(
      GraphQlRequestContext context, Map<String, Object> arguments);
}
