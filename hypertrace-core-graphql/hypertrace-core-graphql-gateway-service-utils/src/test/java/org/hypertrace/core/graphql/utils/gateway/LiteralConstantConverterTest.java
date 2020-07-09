package org.hypertrace.core.graphql.utils.gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.List;
import org.hypertrace.gateway.service.v1.common.LiteralConstant;
import org.hypertrace.gateway.service.v1.common.Value;
import org.hypertrace.gateway.service.v1.common.ValueType;
import org.junit.jupiter.api.Test;

class LiteralConstantConverterTest {

  @Test
  void convertsValuesOfVariousTypes() {
    LiteralConstantConverter converter = new LiteralConstantConverter();

    assertEquals(
        LiteralConstant.newBuilder()
            .setValue(Value.newBuilder().setValueType(ValueType.STRING).setString("foo"))
            .build(),
        converter.convert("foo").blockingGet());

    assertEquals(
        LiteralConstant.newBuilder()
            .setValue(
                Value.newBuilder()
                    .setValueType(ValueType.STRING_ARRAY)
                    .addAllStringArray(List.of("foo", "bar")))
            .build(),
        converter.convert(List.of("foo", "bar")).blockingGet());

    assertEquals(
        LiteralConstant.newBuilder()
            .setValue(Value.newBuilder().setValueType(ValueType.LONG).setLong(100))
            .build(),
        converter.convert(100L).blockingGet());
    assertEquals(
        LiteralConstant.newBuilder()
            .setValue(Value.newBuilder().setValueType(ValueType.LONG).setLong(100))
            .build(),
        converter.convert(100).blockingGet());
    assertEquals(
        LiteralConstant.newBuilder()
            .setValue(Value.newBuilder().setValueType(ValueType.LONG).setLong(100))
            .build(),
        converter.convert(BigInteger.valueOf(100)).blockingGet());

    assertEquals(
        LiteralConstant.newBuilder()
            .setValue(
                Value.newBuilder()
                    .setValueType(ValueType.DOUBLE)
                    .setDouble(
                        BigDecimal.valueOf(53.4f).doubleValue())) // careful of double epsilon
            .build(),
        converter.convert(BigDecimal.valueOf(53.4f)).blockingGet());
    assertEquals(
        LiteralConstant.newBuilder()
            .setValue(Value.newBuilder().setValueType(ValueType.DOUBLE).setDouble(53.4f))
            .build(),
        converter.convert(53.4f).blockingGet());
    assertEquals(
        LiteralConstant.newBuilder()
            .setValue(Value.newBuilder().setValueType(ValueType.DOUBLE).setDouble(53.4))
            .build(),
        converter.convert(53.4d).blockingGet());

    assertEquals(
        LiteralConstant.newBuilder()
            .setValue(Value.newBuilder().setValueType(ValueType.BOOL).setBoolean(true))
            .build(),
        converter.convert(true).blockingGet());

    long timestamp = System.currentTimeMillis();

    assertEquals(
        LiteralConstant.newBuilder()
            .setValue(Value.newBuilder().setValueType(ValueType.TIMESTAMP).setTimestamp(timestamp))
            .build(),
        converter.convert(Instant.ofEpochMilli(timestamp)).blockingGet());
  }

  @Test
  void convertsNullValue() {
    LiteralConstantConverter converter = new LiteralConstantConverter();
    assertEquals(
        LiteralConstant.newBuilder().setValue(Value.getDefaultInstance()).build(),
        converter.convert(null).blockingGet());
  }
}
