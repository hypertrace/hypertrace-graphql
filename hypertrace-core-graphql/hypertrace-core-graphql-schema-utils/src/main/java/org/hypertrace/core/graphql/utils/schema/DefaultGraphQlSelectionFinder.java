package org.hypertrace.core.graphql.utils.schema;

import static java.util.function.Predicate.not;

import graphql.schema.DataFetchingFieldSelectionSet;
import graphql.schema.SelectedField;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

class DefaultGraphQlSelectionFinder implements GraphQlSelectionFinder {
  private static final List<String> ANY_DIRECT_DESCENDANT = List.of(SelectionQuery.ANY);

  @Override
  public Stream<SelectedField> findSelections(
      DataFetchingFieldSelectionSet selectionSet, SelectionQuery query) {

    return this.doSelectionSearch(selectionSet, this.applyQueryDefaults(query));
  }

  private Stream<SelectedField> doSelectionSearch(
      DataFetchingFieldSelectionSet selectionSet, SelectionQuery query) {
    if (query.getSelectionPath().isEmpty()) {
      return Stream.empty();
    }

    String expectedName = query.getSelectionPath().get(0);
    SelectionQuery childQuery = this.buildQueryForChildSelection(query);
    Stream<SelectedField> descendantFields =
        selectionSet.getFields(SelectionQuery.ANY).stream()
            .filter(
                field ->
                    expectedName.equals(SelectionQuery.ANY)
                        || expectedName.equals(field.getName()));
    if (childQuery.getSelectionPath().isEmpty()) {
      return descendantFields.filter(query.getMatchesPredicate());
    }

    return descendantFields
        .map(SelectedField::getSelectionSet)
        .flatMap(childSelectionSet -> this.findSelections(childSelectionSet, childQuery));
  }

  private SelectionQuery applyQueryDefaults(SelectionQuery selectionQuery) {
    List<String> pathOrDefault =
        Optional.ofNullable(selectionQuery.getSelectionPath())
            .filter(not(List::isEmpty))
            .orElse(ANY_DIRECT_DESCENDANT);
    Predicate<SelectedField> predicateOrDefault =
        Optional.ofNullable(selectionQuery.getMatchesPredicate()).orElse(unused -> true);

    return SelectionQuery.builder()
        .selectionPath(pathOrDefault)
        .matchesPredicate(predicateOrDefault)
        .build();
  }

  private SelectionQuery buildQueryForChildSelection(SelectionQuery selectionQuery) {
    return selectionQuery.toBuilder()
        .selectionPath(
            selectionQuery.getSelectionPath().subList(1, selectionQuery.getSelectionPath().size()))
        .build();
  }
}
