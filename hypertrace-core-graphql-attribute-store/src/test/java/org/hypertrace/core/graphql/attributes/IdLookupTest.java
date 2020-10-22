package org.hypertrace.core.graphql.attributes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;

class IdLookupTest {

  @Test
  void canLookupIds() {
    IdLookup idLookup =
        new IdLookup(
            Set.of(
                IdMapping.forId("SPAN", "s-id"),
                IdMapping.forId("TRACE", "t-id"),
                IdMapping.forForeignId("SPAN", "TRACE", "s-t-id")));

    assertEquals(Optional.of("s-id"), idLookup.idKey("SPAN"));
    assertEquals(Optional.of("t-id"), idLookup.idKey("TRACE"));
    assertEquals(Optional.of("s-t-id"), idLookup.foreignIdKey("SPAN", "TRACE"));
    assertEquals(Optional.empty(), idLookup.foreignIdKey("TRACE", "SPAN"));
  }
}
