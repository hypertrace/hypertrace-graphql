package org.hypertrace.core.graphql.spi.schema;

import graphql.annotations.processor.typeFunctions.TypeFunction;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface GraphQlSchemaFragment {

  String fragmentName();

  @Nullable
  default Class<?> annotatedQueryClass() {
    return null;
  }

  @Nullable
  default Class<?> annotatedMutationClass() {
    return null;
  }

  @Nonnull
  default List<TypeFunction> typeFunctions() {
    return List.of();
  }
}
