package org.hypertrace.core.graphql.utils.grpc;

import static java.util.Collections.emptyMap;
import static org.hypertrace.core.grpcutils.context.RequestContextConstants.TENANT_ID_HEADER_KEY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlatformGrpcContextBuilderTest {

  @Mock GraphQlRequestContext mockRequestContext;

  private final PlatformGrpcContextBuilder builder = new PlatformGrpcContextBuilder();

  @Test
  void addsTenantIdToContext() {
    when(this.mockRequestContext.getTenantId()).thenReturn(Optional.of("tenant id"));
    assertEquals(
        Optional.of("tenant id"), this.builder.build(this.mockRequestContext).getTenantId());
  }

  @Test
  void addsTracingHeadersToContext() {
    when(this.mockRequestContext.getTenantId()).thenReturn(Optional.of("tenant id"));
    when(this.mockRequestContext.getTracingContextHeaders())
        .thenReturn(Map.of("traceid", "traceid value"));
    this.builder.build(this.mockRequestContext);
    assertEquals(
        Map.of(TENANT_ID_HEADER_KEY, "tenant id", "traceid", "traceid value"),
        this.builder.build(this.mockRequestContext).getRequestHeaders());
  }

  @Test
  void passesAuthHeaderToPlatformContextIfPresent() {
    assertEquals(emptyMap(), this.builder.build(this.mockRequestContext).getRequestHeaders());

    when(this.mockRequestContext.getAuthorizationHeader()).thenReturn(Optional.of("auth header"));
    assertEquals(
        Map.of("authorization", "auth header"),
        this.builder.build(this.mockRequestContext).getRequestHeaders());
  }
}
