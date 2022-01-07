package org.hypertrace.graphql.label.joiner;

import graphql.schema.DataFetchingFieldSelectionSet;

public interface LabeledEntitiesRequest {
  String entityType();

  int limit();

  DataFetchingFieldSelectionSet selectionSet();
}
