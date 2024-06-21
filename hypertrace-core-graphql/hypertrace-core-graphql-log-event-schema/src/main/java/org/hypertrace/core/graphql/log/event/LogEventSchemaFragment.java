package org.hypertrace.core.graphql.log.event;

import org.hypertrace.core.graphql.log.event.schema.LogEventSchema;
import org.hypertrace.core.graphql.spi.schema.GraphQlSchemaFragment;

public class LogEventSchemaFragment implements GraphQlSchemaFragment {

  @Override
  public String fragmentName() {
    return "LogEvent schema";
  }

  @Override
  public Class<?> annotatedQueryClass() {
    return LogEventSchema.class;
  }
}
