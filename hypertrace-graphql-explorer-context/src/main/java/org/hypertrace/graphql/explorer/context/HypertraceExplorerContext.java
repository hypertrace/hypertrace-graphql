package org.hypertrace.graphql.explorer.context;

import org.hypertrace.graphql.explorer.schema.argument.ExplorerContext;

enum HypertraceExplorerContext implements ExplorerContext {
  TRACE,
  API_TRACE,
  SPAN
}
