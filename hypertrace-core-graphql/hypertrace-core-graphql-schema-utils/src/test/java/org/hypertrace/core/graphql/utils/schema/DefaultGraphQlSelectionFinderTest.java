package org.hypertrace.core.graphql.utils.schema;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import graphql.schema.DataFetchingFieldSelectionSet;
import graphql.schema.SelectedField;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultGraphQlSelectionFinderTest {
  @Mock DataFetchingFieldSelectionSet mockSelectionSet;
  @Mock SelectedField mockSelectionFoo;
  @Mock SelectedField mockSelectionBar;

  final DefaultGraphQlSelectionFinder selectionFinder = new DefaultGraphQlSelectionFinder();
  private List<SelectedField> topLevelSelectionSet;

  @BeforeEach
  void beforeEach() {
    this.topLevelSelectionSet = List.of(this.mockSelectionFoo, this.mockSelectionBar);
    when(this.mockSelectionFoo.getName()).thenReturn("foo");
    when(this.mockSelectionBar.getName()).thenReturn("bar");
    when(this.mockSelectionSet.getFields(any(String.class)))
        .thenAnswer(ignored -> topLevelSelectionSet);
  }

  @Test
  void returnsEmptyStreamForNoMatchingTopLevelSelections() {
    assertIterableEquals(
        Collections.emptyList(),
        this.selectionFinder
            .findSelections(
                this.mockSelectionSet,
                SelectionQuery.builder().selectionPath(List.of("no-match")).build())
            .collect(Collectors.toUnmodifiableList()));
  }

  @Test
  void returnsMatchingTopLevelSelections() {
    assertIterableEquals(
        List.of(this.mockSelectionBar),
        this.selectionFinder
            .findSelections(
                mockSelectionSet, SelectionQuery.builder().selectionPath(List.of("bar")).build())
            .collect(Collectors.toUnmodifiableList()));
  }

  @Test
  void returnsEmptyForOneLevelMatch() {
    DataFetchingFieldSelectionSet fooSelectionSet = mock(DataFetchingFieldSelectionSet.class);
    when(fooSelectionSet.getFields(any(String.class))).thenReturn(Collections.emptyList());
    when(this.mockSelectionFoo.getSelectionSet()).thenReturn(fooSelectionSet);

    assertIterableEquals(
        Collections.emptyList(),
        this.selectionFinder
            .findSelections(
                mockSelectionSet,
                SelectionQuery.builder().selectionPath(List.of("foo", "bar")).build())
            .collect(Collectors.toUnmodifiableList()));
  }

  @Test
  void returnsMultiLevelMatch() {
    DataFetchingFieldSelectionSet fooSelectionSet = mock(DataFetchingFieldSelectionSet.class);
    when(fooSelectionSet.getFields(any(String.class))).thenReturn(List.of(this.mockSelectionBar));
    when(this.mockSelectionFoo.getSelectionSet()).thenReturn(fooSelectionSet);

    assertIterableEquals(
        List.of(this.mockSelectionBar),
        this.selectionFinder
            .findSelections(
                mockSelectionSet,
                SelectionQuery.builder().selectionPath(List.of("foo", "bar")).build())
            .collect(Collectors.toUnmodifiableList()));
  }

  @Test
  void supportsMultipleMatchFanOut() {
    DataFetchingFieldSelectionSet fooSelectionSet = mock(DataFetchingFieldSelectionSet.class);
    this.topLevelSelectionSet = List.of(this.mockSelectionFoo, this.mockSelectionFoo);
    when(fooSelectionSet.getFields(any(String.class)))
        .thenReturn(List.of(this.mockSelectionBar, this.mockSelectionBar));
    when(this.mockSelectionFoo.getSelectionSet()).thenReturn(fooSelectionSet);

    assertIterableEquals(
        List.of(
            this.mockSelectionBar,
            this.mockSelectionBar,
            this.mockSelectionBar,
            this.mockSelectionBar),
        this.selectionFinder
            .findSelections(
                mockSelectionSet,
                SelectionQuery.builder().selectionPath(List.of("foo", "bar")).build())
            .collect(Collectors.toUnmodifiableList()));
  }

  @Test
  void supportsWildcardPath() {
    reset(this.mockSelectionFoo);
    reset(this.mockSelectionBar);
    assertIterableEquals(
        List.of(this.mockSelectionFoo, this.mockSelectionBar),
        this.selectionFinder
            .findSelections(
                mockSelectionSet, SelectionQuery.builder().selectionPath(List.of("*")).build())
            .collect(Collectors.toUnmodifiableList()));
  }

  @Test
  void supportsLeafPredicates() {
    DataFetchingFieldSelectionSet fooSelectionSet = mock(DataFetchingFieldSelectionSet.class);
    when(fooSelectionSet.getFields(any(String.class)))
        .thenReturn(List.of(this.mockSelectionBar, this.mockSelectionFoo));
    when(this.mockSelectionFoo.getSelectionSet()).thenReturn(fooSelectionSet);

    assertIterableEquals(
        List.of(this.mockSelectionBar),
        this.selectionFinder
            .findSelections(
                mockSelectionSet,
                SelectionQuery.builder()
                    .selectionPath(List.of("foo", "*"))
                    .matchesPredicate(field -> field.getName().equals("bar"))
                    .build())
            .collect(Collectors.toUnmodifiableList()));
  }
}
