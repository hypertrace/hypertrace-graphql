package org.hypertrace.core.graphql.common.schema.scalars;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import graphql.annotations.processor.ProcessingElementsContainer;
import graphql.language.StringValue;
import graphql.schema.GraphQLScalarType;
import java.lang.reflect.AnnotatedType;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDateTime;
import org.hypertrace.core.graphql.common.schema.typefunctions.DateTimeScalar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DateTimeScalarTest {

  private static final String TEST_DATE_TIME_STRING = "2019-10-29T21:30:12.871Z";
  private static final Instant TEST_DATE_TIME_INSTANT = Instant.parse(TEST_DATE_TIME_STRING);
  private DateTimeScalar dateTimeFunction;
  private GraphQLScalarType dateTimeType;
  @Mock AnnotatedType mockAnnotatedType;
  // Can't actually mock class, but it's not used so to convey intent using the Mock class.
  private final Class<?> mockAnnotatedClass = Mock.class;
  @Mock ProcessingElementsContainer mockProcessingElementsContainer;

  @BeforeEach
  void beforeEach() {
    this.dateTimeFunction = new DateTimeScalar();
    // Can't actually mock class, but it's not used so to convey intent using
    this.dateTimeType =
        this.dateTimeFunction.buildType(
            false, mockAnnotatedClass, mockAnnotatedType, mockProcessingElementsContainer);
  }

  @Test
  void canDetermineIfConvertible() {
    assertTrue(this.dateTimeFunction.canBuildType(Instant.class, this.mockAnnotatedType));
    assertTrue(this.dateTimeFunction.canBuildType(OffsetDateTime.class, this.mockAnnotatedType));
    assertTrue(this.dateTimeFunction.canBuildType(LocalDateTime.class, this.mockAnnotatedType));
    assertTrue(
        this.dateTimeFunction.canBuildType(ChronoLocalDateTime.class, this.mockAnnotatedType));
    assertTrue(this.dateTimeFunction.canBuildType(ZonedDateTime.class, this.mockAnnotatedType));
  }

  @Test
  void canConvertFromLiteral() {
    assertEquals(
        TEST_DATE_TIME_INSTANT, dateTimeType.getCoercing().parseLiteral(TEST_DATE_TIME_STRING));
  }

  @Test
  void canSerialize() {
    assertEquals(
        TEST_DATE_TIME_STRING, dateTimeType.getCoercing().serialize(TEST_DATE_TIME_INSTANT));
    assertEquals(
        TEST_DATE_TIME_STRING, dateTimeType.getCoercing().serialize(TEST_DATE_TIME_STRING));

    assertEquals(
        TEST_DATE_TIME_STRING,
        dateTimeType
            .getCoercing()
            .serialize(TEST_DATE_TIME_INSTANT.atOffset(ZoneOffset.ofHoursMinutes(12, 30))));
  }

  @Test
  void canConvertFromValue() {
    assertEquals(
        TEST_DATE_TIME_INSTANT,
        dateTimeType
            .getCoercing()
            .parseValue(StringValue.newStringValue().value(TEST_DATE_TIME_STRING).build()));
  }
}
