package org.hypertrace.graphql.spaces.dao;

import io.reactivex.rxjava3.core.Single;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.graphql.spaces.request.SpaceConfigRuleCreationRequest;
import org.hypertrace.graphql.spaces.schema.query.SpaceConfigRuleResultSet;
import org.hypertrace.graphql.spaces.schema.query.SpaceResultSet;
import org.hypertrace.graphql.spaces.schema.shared.SpaceConfigRule;

public interface SpacesDao {
  Single<SpaceResultSet> getAllSpaces(GraphQlRequestContext context);
}
