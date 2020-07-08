package org.hypertrace.core.graphql.utils.grpc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import org.junit.jupiter.api.Test;

class PlatformRequestContextBuilderTest {

  @Test
  void addsEachHeaderToRequestContext() {
    var provided = Map.of("k1", "v1", "k2", "v2");
    var context = new PlatformRequestContextBuilder().build(provided);
    assertEquals(provided, context.getAll());
    assertEquals(provided, context.getRequestHeaders());
  }
}
