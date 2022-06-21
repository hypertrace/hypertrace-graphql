package org.hypertrace.graphql.spanprocessing.dao;

import io.reactivex.rxjava3.core.Single;
import org.hypertrace.core.graphql.common.request.ContextualRequest;
import org.hypertrace.graphql.spanprocessing.request.mutation.ApiNamingCreateRuleRequest;
import org.hypertrace.graphql.spanprocessing.request.mutation.ApiNamingDeleteRuleRequest;
import org.hypertrace.graphql.spanprocessing.request.mutation.ApiNamingUpdateRuleRequest;
import org.hypertrace.graphql.spanprocessing.request.mutation.ExcludeSpanCreateRuleRequest;
import org.hypertrace.graphql.spanprocessing.request.mutation.ExcludeSpanDeleteRuleRequest;
import org.hypertrace.graphql.spanprocessing.request.mutation.ExcludeSpanUpdateRuleRequest;
import org.hypertrace.graphql.spanprocessing.request.mutation.IncludeSpanCreateRuleRequest;
import org.hypertrace.graphql.spanprocessing.request.mutation.IncludeSpanDeleteRuleRequest;
import org.hypertrace.graphql.spanprocessing.request.mutation.IncludeSpanUpdateRuleRequest;
import org.hypertrace.graphql.spanprocessing.schema.mutation.DeleteSpanProcessingRuleResponse;
import org.hypertrace.graphql.spanprocessing.schema.query.ApiNamingRuleResultSet;
import org.hypertrace.graphql.spanprocessing.schema.query.ExcludeSpanRuleResultSet;
import org.hypertrace.graphql.spanprocessing.schema.query.IncludeSpanRuleResultSet;
import org.hypertrace.graphql.spanprocessing.schema.rule.ApiNamingRule;
import org.hypertrace.graphql.spanprocessing.schema.rule.ExcludeSpanRule;
import org.hypertrace.graphql.spanprocessing.schema.rule.IncludeSpanRule;

public interface SpanProcessingRuleDao {
  Single<ExcludeSpanRuleResultSet> getExcludeSpanRules(ContextualRequest request);

  Single<ExcludeSpanRule> createExcludeSpanRule(ExcludeSpanCreateRuleRequest request);

  Single<ExcludeSpanRule> updateExcludeSpanRule(ExcludeSpanUpdateRuleRequest request);

  Single<DeleteSpanProcessingRuleResponse> deleteExcludeSpanRule(
      ExcludeSpanDeleteRuleRequest request);

  Single<IncludeSpanRuleResultSet> getIncludeSpanRules(ContextualRequest request);

  Single<IncludeSpanRule> createIncludeSpanRule(IncludeSpanCreateRuleRequest request);

  Single<IncludeSpanRule> updateIncludeSpanRule(IncludeSpanUpdateRuleRequest request);

  Single<DeleteSpanProcessingRuleResponse> deleteIncludeSpanRule(
      IncludeSpanDeleteRuleRequest request);

  Single<ApiNamingRuleResultSet> getApiNamingRules(ContextualRequest request);

  Single<ApiNamingRule> createApiNamingRule(ApiNamingCreateRuleRequest request);

  Single<ApiNamingRule> updateApiNamingRule(ApiNamingUpdateRuleRequest request);

  Single<DeleteSpanProcessingRuleResponse> deleteApiNamingRule(ApiNamingDeleteRuleRequest request);
}
