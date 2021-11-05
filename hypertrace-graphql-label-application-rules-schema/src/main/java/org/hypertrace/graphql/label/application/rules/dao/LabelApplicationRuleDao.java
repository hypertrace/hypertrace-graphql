package org.hypertrace.graphql.label.application.rules.dao;

import io.reactivex.rxjava3.core.Single;
import org.hypertrace.core.graphql.common.request.ContextualRequest;
import org.hypertrace.graphql.label.application.rules.request.LabelApplicationRuleCreateRequest;
import org.hypertrace.graphql.label.application.rules.request.LabelApplicationRuleDeleteRequest;
import org.hypertrace.graphql.label.application.rules.request.LabelApplicationRuleUpdateRequest;
import org.hypertrace.graphql.label.application.rules.schema.query.LabelApplicationRuleResultSet;
import org.hypertrace.graphql.label.application.rules.schema.shared.LabelApplicationRule;

public interface LabelApplicationRuleDao {
  Single<LabelApplicationRule> createLabelApplicationRule(
      LabelApplicationRuleCreateRequest request);

  Single<LabelApplicationRuleResultSet> getLabelApplicationRules(ContextualRequest request);

  Single<LabelApplicationRule> updateLabelApplicationRule(
      LabelApplicationRuleUpdateRequest request);

  Single<Boolean> deleteLabelApplicationRule(LabelApplicationRuleDeleteRequest request);
}
