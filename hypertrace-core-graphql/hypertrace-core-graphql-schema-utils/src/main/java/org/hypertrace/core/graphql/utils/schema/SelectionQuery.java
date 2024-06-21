package org.hypertrace.core.graphql.utils.schema;

import graphql.schema.SelectedField;
import java.util.List;
import java.util.function.Predicate;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class SelectionQuery {

  List<String> selectionPath;
  Predicate<SelectedField> matchesPredicate;

  public static final String ANY = "*";

  public static SelectionQuery namedChild(String childName) {
    return SelectionQuery.builder().selectionPath(List.of(childName)).build();
  }
}
