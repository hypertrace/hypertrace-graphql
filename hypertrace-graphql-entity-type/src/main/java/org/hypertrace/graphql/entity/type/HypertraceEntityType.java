package org.hypertrace.graphql.entity.type;

import org.hypertrace.graphql.entity.schema.EntityType;

enum HypertraceEntityType implements EntityType {
  API,
  SERVICE,
  BACKEND;
}
