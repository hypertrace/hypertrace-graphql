package org.hypertrace.graphql.spaces.dao;

import io.reactivex.rxjava3.core.Single;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.graphql.spaces.request.SpaceConfigRuleCreationRequest;
import org.hypertrace.graphql.spaces.schema.query.SpaceConfigRuleResultSet;
import org.hypertrace.graphql.spaces.schema.shared.SpaceConfigRule;

public interface SpacesConfigDao {
  Single<SpaceConfigRuleResultSet> getAllRules(GraphQlRequestContext requestContext);

  Single<SpaceConfigRule> createRule(SpaceConfigRuleCreationRequest request);
}
