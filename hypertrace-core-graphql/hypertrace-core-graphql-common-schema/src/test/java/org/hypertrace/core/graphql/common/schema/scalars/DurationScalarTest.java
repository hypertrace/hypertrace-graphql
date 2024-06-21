package org.hypertrace.core.graphql.common.schema.scalars;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import graphql.annotations.processor.ProcessingElementsContainer;
import graphql.language.StringValue;
import graphql.schema.GraphQLScalarType;
import java.lang.reflect.AnnotatedType;
import java.time.Duration;
import java.time.Instant;
import org.hypertrace.core.graphql.common.schema.typefunctions.DurationScalar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DurationScalarTest {

  private static final String TEST_DURATION_STRING = "PT2H30M";
  private static final Duration TEST_DURATION = Duration.parse(TEST_DURATION_STRING);
  private DurationScalar durationScalar;
  private GraphQLScalarType durationType;
  @Mock AnnotatedType mockAnnotatedType;
  // Can't actually mock class, but it's not used so to convey intent using the Mock class.
  private final Class<?> mockAnnotatedClass = Mock.class;
  @Mock ProcessingElementsContainer mockProcessingElementsContainer;

  @BeforeEach
  void beforeEach() {
    this.durationScalar = new DurationScalar();
    // Can't actually mock class, but it's not used so to convey intent using
    this.durationType =
        this.durationScalar.buildType(
            false, mockAnnotatedClass, mockAnnotatedType, mockProcessingElementsContainer);
  }

  @Test
  void canDetermineIfConvertible() {
    assertTrue(this.durationScalar.canBuildType(Duration.class, this.mockAnnotatedType));
    assertFalse(this.durationScalar.canBuildType(Instant.class, this.mockAnnotatedType));
  }

  @Test
  void canConvertFromLiteral() {
    assertEquals(TEST_DURATION, durationType.getCoercing().parseLiteral(TEST_DURATION_STRING));
  }

  @Test
  void canSerialize() {
    assertEquals(TEST_DURATION_STRING, durationType.getCoercing().serialize(TEST_DURATION));
    assertEquals(TEST_DURATION_STRING, durationType.getCoercing().serialize(TEST_DURATION_STRING));
  }

  @Test
  void canConvertFromValue() {
    assertEquals(
        TEST_DURATION,
        durationType
            .getCoercing()
            .parseValue(StringValue.newStringValue().value(TEST_DURATION_STRING).build()));
  }
}
