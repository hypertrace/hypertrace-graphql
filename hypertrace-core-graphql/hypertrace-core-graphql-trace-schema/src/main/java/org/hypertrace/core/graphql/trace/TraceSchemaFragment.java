package org.hypertrace.core.graphql.trace;

import org.hypertrace.core.graphql.spi.schema.GraphQlSchemaFragment;
import org.hypertrace.core.graphql.trace.schema.TraceSchema;

public class TraceSchemaFragment implements GraphQlSchemaFragment {

  @Override
  public String fragmentName() {
    return "Trace schema";
  }

  @Override
  public Class<TraceSchema> annotatedQueryClass() {
    return TraceSchema.class;
  }
}
