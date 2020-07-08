package org.hypertrace.core.graphql.schema.registry;

import graphql.annotations.annotationTypes.GraphQLName;
import org.hypertrace.core.graphql.spi.schema.GraphQlSchemaFragment;

class DefaultSchema implements GraphQlSchemaFragment {

  @Override
  public String fragmentName() {
    return "Default Schema";
  }

  @Override
  public Class<?> annotatedQueryClass() {
    return QuerySchema.class;
  }

  @Override
  public Class<?> annotatedMutationClass() {
    return MutationSchema.class;
  }

  @GraphQLName("Query")
  private interface QuerySchema {}

  @GraphQLName("Mutation")
  private interface MutationSchema {}
}
