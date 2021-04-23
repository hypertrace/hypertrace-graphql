package org.hypertrace.core.graphql.utils.gateway;

import java.time.Duration;
import java.time.Instant;
import org.hypertrace.core.graphql.attributes.AttributeModel;
import org.hypertrace.gateway.service.v1.common.Value;
import org.hypertrace.gateway.service.v1.common.ValueType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UnwrappedValueConverterTest {

  @Mock AttributeModel attributeModel;

  @Test
  void testTimestampConversion() {
    UnwrappedValueConverter unwrappedValueConverter = new UnwrappedValueConverter();
    long millis = System.currentTimeMillis();
    Value value =
        Value.newBuilder()
            .setValueType(ValueType.TIMESTAMP)
            .setTimestamp(Duration.ofMillis(millis).toNanos())
            .build();
    Mockito.when(attributeModel.units()).thenReturn("ns");

    Instant instant =
        (Instant) unwrappedValueConverter.convert(value, attributeModel).blockingGet();
    Assertions.assertEquals(Instant.ofEpochMilli(millis), instant);
  }
}
