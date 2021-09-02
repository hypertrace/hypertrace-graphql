package org.hypertrace.graphql.label;

import org.hypertrace.core.graphql.spi.schema.GraphQlSchemaFragment;
import org.hypertrace.graphql.label.schema.LabelSchema;
import org.hypertrace.graphql.label.schema.mutation.LabelMutationSchema;

class LabelSchemaFragment implements GraphQlSchemaFragment {

  @Override
  public String fragmentName() {
    return "Label schema";
  }

  @Override
  public Class<LabelSchema> annotatedQueryClass() {
    return LabelSchema.class;
  }

  @Override
  public Class<?> annotatedMutationClass() {
    return LabelMutationSchema.class;
  }
}
