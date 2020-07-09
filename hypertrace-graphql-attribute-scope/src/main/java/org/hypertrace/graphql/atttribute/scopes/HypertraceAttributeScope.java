package org.hypertrace.graphql.atttribute.scopes;

import org.hypertrace.core.graphql.common.schema.attributes.AttributeScope;

enum HypertraceAttributeScope implements AttributeScope {
  API,
  API_TRACE,
  BACKEND,
  BACKEND_TRACE,
  INTERACTION,
  SERVICE,
  SPAN,
  TRACE
}
