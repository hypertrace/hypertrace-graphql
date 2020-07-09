package org.hypertrace.core.graphql.utils.grpc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import java.util.concurrent.CompletableFuture;
import org.hypertrace.core.graphql.spi.lifecycle.GraphQlServiceLifecycle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultGrpcChannelRegistryTest {

  @Mock GraphQlServiceLifecycle mockLifecycle;
  CompletableFuture<Void> shutdown;

  DefaultGrpcChannelRegistry channelRegistry;

  @BeforeEach
  void beforeEach() {
    this.shutdown = new CompletableFuture<>();
    when(this.mockLifecycle.shutdownCompletion()).thenReturn(this.shutdown);
    this.channelRegistry = new DefaultGrpcChannelRegistry(this.mockLifecycle);
  }

  @Test
  void createsNewChannelsAsRequested() {
    assertNotNull(this.channelRegistry.forAddress("foo", 1000));
  }

  @Test
  void reusesChannelsForDuplicateRequests() {
    Channel firstChannel = this.channelRegistry.forAddress("foo", 1000);
    assertSame(firstChannel, this.channelRegistry.forAddress("foo", 1000));
    assertNotSame(firstChannel, this.channelRegistry.forAddress("foo", 1001));
    assertNotSame(firstChannel, this.channelRegistry.forAddress("bar", 1000));
  }

  @Test
  void shutdownAllChannelsOnLifecycleShutdown() {
    ManagedChannel firstChannel = this.channelRegistry.forAddress("foo", 1000);
    ManagedChannel secondChannel = this.channelRegistry.forAddress("foo", 1002);
    assertFalse(firstChannel.isShutdown());
    assertFalse(secondChannel.isShutdown());
    this.shutdown.complete(null);
    assertTrue(firstChannel.isShutdown());
    assertTrue(secondChannel.isShutdown());
  }

  @Test
  void throwsIfNewChannelRequestedAfterLifecycleShutdown() {
    this.shutdown.complete(null);
    assertThrows(AssertionError.class, () -> this.channelRegistry.forAddress("foo", 1000));
  }
}
