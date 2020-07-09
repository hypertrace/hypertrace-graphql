package org.hypertrace.core.graphql.atttributes.scopes;

import org.hypertrace.core.graphql.common.schema.attributes.AttributeScope;

enum HypertraceCoreAttributeScope implements AttributeScope {
  TRACE,
  SPAN;
}
