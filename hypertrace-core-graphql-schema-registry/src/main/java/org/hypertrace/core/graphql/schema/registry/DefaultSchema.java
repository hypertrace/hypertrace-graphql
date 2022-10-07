package org.hypertrace.core.graphql.schema.registry;

import graphql.annotations.annotationTypes.GraphQLDescription;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import org.hypertrace.core.graphql.spi.schema.GraphQlSchemaFragment;

class DefaultSchema implements GraphQlSchemaFragment {
  // Placeholder description is used to identify and remove placeholder fields before building the
  // schema. Placeholders are used while the schema is under construction so it is always valid
  // (i.e. has at least one value)
  static final String PLACEHOLDER_DESCRIPTION = "::placeholder::";
  static final String ROOT_QUERY_NAME = "Query";
  static final String ROOT_MUTATION_NAME = "Mutation";

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

  @GraphQLName(ROOT_QUERY_NAME)
  private interface QuerySchema {
    @GraphQLField
    @GraphQLDescription(PLACEHOLDER_DESCRIPTION)
    String placeholder();
  }

  @GraphQLName(ROOT_MUTATION_NAME)
  private interface MutationSchema {
    @GraphQLField
    @GraphQLDescription(PLACEHOLDER_DESCRIPTION)
    String placeholder();
  }
}
