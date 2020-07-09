package org.hypertrace.core.graphql.attributes;

public enum AttributeModelScope {
  TRACE,
  SPAN,
  INTERACTION,
  DOMAIN_EVENT,
  API,
  SERVICE,
  DOMAIN,
  TRANSACTION,
  SESSION,
  API_TRACE,
  BACKEND,
  BACKEND_TRACE,
  ACTOR
}
