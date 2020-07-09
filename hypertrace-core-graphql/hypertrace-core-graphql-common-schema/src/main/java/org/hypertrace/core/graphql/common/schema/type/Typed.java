package org.hypertrace.core.graphql.common.schema.type;

public interface Typed<T> {
  String TYPE_FIELD_NAME = "type";

  /**
   * Must be annotated in extending interface. Annotations lib doesn't handle generics, which
   * requires a redeclaration and overrides these annotations.
   *
   * <pre>
   *     \@GraphQLField
   *     \@GraphQLNonNull
   *     \@GraphQLName(TYPE_FIELD_NAME)
   * </pre>
   */
  T type();
}
