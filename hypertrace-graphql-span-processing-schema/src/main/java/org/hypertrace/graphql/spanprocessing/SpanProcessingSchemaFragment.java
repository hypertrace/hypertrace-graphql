package org.hypertrace.graphql.spanprocessing;

import javax.annotation.Nullable;
import org.hypertrace.core.graphql.spi.schema.GraphQlSchemaFragment;
import org.hypertrace.graphql.spanprocessing.schema.mutation.SpanProcessingMutationSchema;
import org.hypertrace.graphql.spanprocessing.schema.query.SpanProcessingQuerySchema;

public class SpanProcessingSchemaFragment implements GraphQlSchemaFragment {

  @Override
  public String fragmentName() {
    return "Span Processing schema";
  }

  @Override
  public Class<SpanProcessingQuerySchema> annotatedQueryClass() {
    return SpanProcessingQuerySchema.class;
  }

  @Nullable
  @Override
  public Class<?> annotatedMutationClass() {
    return SpanProcessingMutationSchema.class;
  }
}
