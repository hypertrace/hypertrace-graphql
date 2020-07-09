package org.hypertrace.core.graphql.utils.schema;

import graphql.schema.DataFetchingFieldSelectionSet;
import graphql.schema.SelectedField;
import java.util.stream.Stream;

public interface GraphQlSelectionFinder {

  Stream<SelectedField> findSelections(
      DataFetchingFieldSelectionSet selectionSet, SelectionQuery query);
}
