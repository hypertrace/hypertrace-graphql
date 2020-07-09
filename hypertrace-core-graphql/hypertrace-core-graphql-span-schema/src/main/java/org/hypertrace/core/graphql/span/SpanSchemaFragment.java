package org.hypertrace.core.graphql.span;

import org.hypertrace.core.graphql.span.schema.SpanSchema;
import org.hypertrace.core.graphql.spi.schema.GraphQlSchemaFragment;

public class SpanSchemaFragment implements GraphQlSchemaFragment {

  @Override
  public String fragmentName() {
    return "Span schema";
  }

  @Override
  public Class<SpanSchema> annotatedQueryClass() {
    return SpanSchema.class;
  }
}
