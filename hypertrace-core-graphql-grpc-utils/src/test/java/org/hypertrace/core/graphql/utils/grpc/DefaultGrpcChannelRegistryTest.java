package org.hypertrace.core.graphql.utils.grpc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.grpc.Channel;
import org.hypertrace.core.grpcutils.client.GrpcChannelRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultGrpcChannelRegistryTest {
  DefaultGrpcChannelRegistry channelRegistry;

  @BeforeEach
  void beforeEach() {
    this.channelRegistry = new DefaultGrpcChannelRegistry(new GrpcChannelRegistry());
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
}
