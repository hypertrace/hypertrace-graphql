package org.hypertrace.core.graphql.context;

import static java.util.Collections.emptyMap;
import static org.hypertrace.core.graphql.context.DefaultGraphQlRequestContextBuilder.AUTHORIZATION_HEADER_KEY;
import static org.hypertrace.core.graphql.context.DefaultGraphQlRequestContextBuilder.TENANT_ID_HEADER_KEY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import graphql.schema.DataFetcher;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hypertrace.core.graphql.spi.config.GraphQlServiceConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultGraphQlRequestContextBuilderTest {

  @Mock AsyncDataFetcherFactory mockDataFetcherFactory;
  @Mock HttpServletRequest mockRequest;
  @Mock HttpServletResponse mockResponse;
  @Mock GraphQlServiceConfig mockServiceConfig;

  GraphQlRequestContextBuilder contextBuilder;
  GraphQlRequestContext requestContext;

  @BeforeEach
  void beforeEach() {
    this.contextBuilder =
        new DefaultGraphQlRequestContextBuilder(
            this.mockDataFetcherFactory, this.mockServiceConfig);
    this.requestContext = this.contextBuilder.build(this.mockRequest, this.mockResponse);
  }

  @Test
  void returnsAuthorizationHeaderIfPresent() {
    when(this.mockRequest.getHeader(eq(AUTHORIZATION_HEADER_KEY))).thenReturn("Bearer ABC");
    when(this.mockRequest.getHeader(eq(AUTHORIZATION_HEADER_KEY.toLowerCase())))
        .thenReturn("Bearer abc");
    assertEquals(Optional.of("Bearer ABC"), this.requestContext.getAuthorizationHeader());

    when(this.mockRequest.getHeader(eq(AUTHORIZATION_HEADER_KEY))).thenReturn(null);
    assertEquals(Optional.of("Bearer abc"), this.requestContext.getAuthorizationHeader());
  }

  @Test
  void returnsEmptyOptionalIfNoAuthorizationHeaderPresent() {
    when(this.mockRequest.getHeader(any())).thenReturn(null);
    assertEquals(Optional.empty(), this.requestContext.getAuthorizationHeader());
  }

  @Test
  void delegatesDataLoaderRegistry() {
    assertNotNull(this.requestContext.getDataLoaderRegistry());
  }

  @Test
  void canDelegateDataFetcherConstruction() {
    this.requestContext.constructDataFetcher(TestDataFetcher.class);
    verify(this.mockDataFetcherFactory).buildDataFetcher(TestDataFetcher.class);
  }

  @Test
  void returnsTenantIdIfTenantIdHeaderPresent() {
    when(this.mockRequest.getHeader(TENANT_ID_HEADER_KEY)).thenReturn("test tenant id");
    assertEquals(Optional.of("test tenant id"), this.requestContext.getTenantId());
  }

  @Test
  void returnsDefaultTenantIdOnlyIfNoHeaderPresent() {
    when(this.mockRequest.getHeader(TENANT_ID_HEADER_KEY)).thenReturn("test tenant id");
    when(this.mockServiceConfig.getDefaultTenantId()).thenReturn(Optional.of("default tenant id"));
    assertEquals(Optional.of("test tenant id"), this.requestContext.getTenantId());
    reset(this.mockRequest);
    assertEquals(Optional.of("default tenant id"), this.requestContext.getTenantId());
  }

  @Test
  void returnsCachingKeyForNoAuth() {
    assertNotNull(this.requestContext.getCachingKey());
  }

  @Test
  void returnsCachingKeysEqualForSameTenant() {
    when(this.mockRequest.getHeader(TENANT_ID_HEADER_KEY)).thenReturn("first tenant id");
    var firstKey = this.contextBuilder.build(this.mockRequest, this.mockResponse).getCachingKey();
    var secondKey = this.contextBuilder.build(this.mockRequest, this.mockResponse).getCachingKey();
    assertEquals(firstKey, secondKey);
    assertNotSame(firstKey, secondKey);

    when(this.mockRequest.getHeader(TENANT_ID_HEADER_KEY)).thenReturn("second tenant id");
    var thirdKey = this.contextBuilder.build(this.mockRequest, this.mockResponse).getCachingKey();
    assertNotEquals(firstKey, thirdKey);
  }

  @Test
  void returnsEmptyMapIfNoTracingHeadersPresent() {
    when(this.mockRequest.getHeaderNames()).thenReturn(Collections.enumeration(List.of("foo")));
    assertEquals(emptyMap(), this.requestContext.getTracingContextHeaders());
  }

  @Test
  void returnsLowerCasedTracingHeadersIfAnyMatches() {
    when(this.mockRequest.getHeaderNames())
        .thenReturn(
            Collections.enumeration(
                List.of(
                    "traceSTATE", "traceparent", "other", "X-B3-traceid", "x-b3-parent-trace-id")));
    when(this.mockRequest.getHeader(any(String.class)))
        .thenAnswer(invocation -> invocation.getArgument(0) + " value");
    assertEquals(
        Map.of(
            "tracestate",
            "traceSTATE value",
            "traceparent",
            "traceparent value",
            "x-b3-traceid",
            "X-B3-traceid value",
            "x-b3-parent-trace-id",
            "x-b3-parent-trace-id value"),
        this.requestContext.getTracingContextHeaders());
  }

  private interface TestDataFetcher extends DataFetcher<CompletableFuture<String>> {}
}
