package org.hypertrace.graphql.label.dao;

import io.reactivex.rxjava3.core.Single;
import org.hypertrace.core.graphql.common.request.ContextualRequest;
import org.hypertrace.graphql.label.request.LabelApplicationRuleCreateRequest;
import org.hypertrace.graphql.label.request.LabelApplicationRuleDeleteRequest;
import org.hypertrace.graphql.label.request.LabelApplicationRuleUpdateRequest;
import org.hypertrace.graphql.label.schema.LabelApplicationRule;
import org.hypertrace.graphql.label.schema.LabelApplicationRuleResultSet;

public interface LabelApplicationRuleDao {
  Single<LabelApplicationRule> createLabelApplicationRule(
      LabelApplicationRuleCreateRequest request);

  Single<LabelApplicationRuleResultSet> getLabelApplicationRules(ContextualRequest request);

  Single<LabelApplicationRule> updateLabelApplicationRule(
      LabelApplicationRuleUpdateRequest request);

  Single<Boolean> deleteLabelApplicationRule(LabelApplicationRuleDeleteRequest request);
}
