package org.hypertrace.graphql.label;

import javax.annotation.Nullable;
import org.hypertrace.core.graphql.spi.schema.GraphQlSchemaFragment;
import org.hypertrace.graphql.label.schema.LabelSchema;
import org.hypertrace.graphql.label.schema.mutation.LabelsMutationSchema;

class LabelSchemaFragment implements GraphQlSchemaFragment {

  @Override
  public String fragmentName() {
    return "Label schema";
  }

  @Override
  public Class<LabelSchema> annotatedQueryClass() {
    return LabelSchema.class;
  }

  @Nullable
  @Override
  public Class<?> annotatedMutationClass() {
    return LabelsMutationSchema.class;
  }
}
