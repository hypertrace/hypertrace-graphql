package org.hypertrace.graphql.label.joiner;

import graphql.schema.DataFetchingFieldSelectionSet;
import io.reactivex.rxjava3.core.Single;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;

public interface EntityAndRuleJoinerBuilder {
  Single<EntityAndRuleJoiner> build(
      GraphQlRequestContext context, DataFetchingFieldSelectionSet selectionSet);
}
