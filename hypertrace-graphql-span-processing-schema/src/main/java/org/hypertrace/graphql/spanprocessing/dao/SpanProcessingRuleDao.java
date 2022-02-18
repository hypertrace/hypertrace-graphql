package org.hypertrace.graphql.spanprocessing.dao;

import io.reactivex.rxjava3.core.Single;
import org.hypertrace.core.graphql.common.request.ContextualRequest;
import org.hypertrace.graphql.spanprocessing.request.mutation.ExcludeSpanCreateRuleRequest;
import org.hypertrace.graphql.spanprocessing.request.mutation.ExcludeSpanDeleteRuleRequest;
import org.hypertrace.graphql.spanprocessing.request.mutation.ExcludeSpanUpdateRuleRequest;
import org.hypertrace.graphql.spanprocessing.schema.mutation.DeleteSpanProcessingRuleResponse;
import org.hypertrace.graphql.spanprocessing.schema.query.ExcludeSpanRuleResultSet;
import org.hypertrace.graphql.spanprocessing.schema.rule.ExcludeSpanRule;

public interface SpanProcessingRuleDao {
  Single<ExcludeSpanRuleResultSet> getRules(ContextualRequest request);

  Single<ExcludeSpanRule> createRule(ExcludeSpanCreateRuleRequest request);

  Single<ExcludeSpanRule> updateRule(ExcludeSpanUpdateRuleRequest request);

  Single<DeleteSpanProcessingRuleResponse> deleteRule(ExcludeSpanDeleteRuleRequest request);
}
