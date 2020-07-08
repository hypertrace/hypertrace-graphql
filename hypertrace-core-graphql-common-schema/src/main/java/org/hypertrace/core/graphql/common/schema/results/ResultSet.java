package org.hypertrace.core.graphql.common.schema.results;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import java.util.List;

public interface ResultSet<T> {
  String RESULT_SET_RESULTS_NAME = "results";
  String RESULT_SET_COUNT_NAME = "count";
  String RESULT_SET_TOTAL_NAME = "total";

  /**
   * Must be annotated in extending interface. Annotations lib doesn't handle generics, which
   * requires a redeclaration and overrides these annotations.
   *
   * <pre>
   *     \@GraphQLField
   *     \@GraphQLNonNull
   *     \@GraphQLName(RESULT_SET_RESULTS_NAME)
   * </pre>
   */
  List<T> results();

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(RESULT_SET_COUNT_NAME)
  long count();

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(RESULT_SET_TOTAL_NAME)
  long total();
}
