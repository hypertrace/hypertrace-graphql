package org.hypertrace.core.graphql.common.schema.scalars;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import graphql.language.StringValue;
import graphql.schema.GraphQLScalarType;
import java.time.LocalTime;
import org.hypertrace.core.graphql.common.schema.typefunctions.TimeScalar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TimeScalarTest {

  private static final String TIME_STRING = "08:30";
  private static final LocalTime LOCAL_TIME = LocalTime.parse(TIME_STRING);
  private TimeScalar timeScalar;
  private GraphQLScalarType timeType;

  @BeforeEach
  void beforeEach() {
    this.timeScalar = new TimeScalar();
    this.timeType =
        this.timeScalar.buildType(
            false, null, null, null);
  }

  @Test
  void test_canBuildType_compatibleTypes_shouldReturnTrue() {
    assertTrue(this.timeScalar.canBuildType(LocalTime.class, null));
  }

  @Test
  void test_canConvertFromLiteral() {
    assertEquals(
        LOCAL_TIME, timeType.getCoercing().parseLiteral(TIME_STRING));
  }

  @Test
  void test_canSerialize() {
    assertEquals(
        TIME_STRING, timeType.getCoercing().serialize(LOCAL_TIME));
    assertEquals(
        TIME_STRING, timeType.getCoercing().serialize(TIME_STRING));
  }

  @Test
  void test_canConvertFromValue() {
    assertEquals(
        LOCAL_TIME,
        timeType
            .getCoercing()
            .parseValue(StringValue.newStringValue().value(TIME_STRING).build()));
  }
}
