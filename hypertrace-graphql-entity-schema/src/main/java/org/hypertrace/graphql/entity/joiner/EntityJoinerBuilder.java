package org.hypertrace.graphql.entity.joiner;

import graphql.schema.DataFetchingFieldSelectionSet;
import io.reactivex.rxjava3.core.Single;
import java.util.List;
import org.hypertrace.core.graphql.common.schema.arguments.TimeRangeArgument;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;

public interface EntityJoinerBuilder {

  Single<EntityJoiner> build(
      GraphQlRequestContext context,
      DataFetchingFieldSelectionSet selectionSet,
      List<String> pathToEntityJoinable);

  Single<EntityJoiner> build(
      GraphQlRequestContext context,
      TimeRangeArgument timeRange,
      DataFetchingFieldSelectionSet selectionSet,
      List<String> pathToEntityJoinable);
}
