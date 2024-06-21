package org.hypertrace.core.graphql.common.schema.scalars;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import graphql.annotations.processor.ProcessingElementsContainer;
import graphql.language.StringValue;
import graphql.schema.GraphQLScalarType;
import java.lang.reflect.AnnotatedType;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import org.hypertrace.core.graphql.common.schema.typefunctions.OffsetTimeScalar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OffsetTimeScalarTest {

  private static final String TEST_SCALAR_TIME_STRING = "21:30:12.748+12:30";
  private static final OffsetTime TEST_OFFSET_TIME = OffsetTime.parse(TEST_SCALAR_TIME_STRING);
  private OffsetTimeScalar offsetTimeFunction;
  private GraphQLScalarType offsetTimeType;
  @Mock AnnotatedType mockAnnotatedType;
  // Can't actually mock class, but it's not used so to convey intent using the Mock class.
  private final Class<?> mockAnnotatedClass = Mock.class;
  @Mock ProcessingElementsContainer mockProcessingElementsContainer;

  @BeforeEach
  void beforeEach() {
    this.offsetTimeFunction = new OffsetTimeScalar();
    // Can't actually mock class, but it's not used so to convey intent using
    this.offsetTimeType =
        this.offsetTimeFunction.buildType(
            false, mockAnnotatedClass, mockAnnotatedType, mockProcessingElementsContainer);
  }

  @Test
  void canDetermineIfConvertible() {
    assertTrue(this.offsetTimeFunction.canBuildType(OffsetTime.class, this.mockAnnotatedType));
  }

  @Test
  void canConvertFromLiteral() {
    assertEquals(
        TEST_OFFSET_TIME, offsetTimeType.getCoercing().parseLiteral(TEST_SCALAR_TIME_STRING));
  }

  @Test
  void canSerialize() {
    assertEquals(TEST_SCALAR_TIME_STRING, offsetTimeType.getCoercing().serialize(TEST_OFFSET_TIME));
    assertEquals(
        TEST_SCALAR_TIME_STRING, offsetTimeType.getCoercing().serialize(TEST_SCALAR_TIME_STRING));

    assertEquals(
        TEST_SCALAR_TIME_STRING,
        offsetTimeType
            .getCoercing()
            .serialize(TEST_OFFSET_TIME.withOffsetSameLocal(ZoneOffset.ofHoursMinutes(12, 30))));
  }

  @Test
  void canConvertFromValue() {
    assertEquals(
        TEST_OFFSET_TIME,
        offsetTimeType
            .getCoercing()
            .parseValue(StringValue.newStringValue().value(TEST_SCALAR_TIME_STRING).build()));
  }
}
