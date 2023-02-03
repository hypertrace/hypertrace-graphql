package org.hypertrace.core.graphql.common.schema.scalars;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import graphql.annotations.processor.ProcessingElementsContainer;
import graphql.language.ArrayValue;
import graphql.language.BooleanValue;
import graphql.language.FloatValue;
import graphql.language.IntValue;
import graphql.language.StringValue;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.GraphQLScalarType;
import java.lang.reflect.AnnotatedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.List;
import org.hypertrace.core.graphql.common.schema.typefunctions.UnknownScalar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UnknownScalarTest {

  private UnknownScalar unknownScalarFunction;
  private GraphQLScalarType unknownScalarType;
  @Mock AnnotatedType mockAnnotatedType;
  // Can't actually mock class, but it's not used so to convey intent using the Mock class.
  private final Class<?> mockAnnotatedClass = Mock.class;
  @Mock ProcessingElementsContainer mockProcessingElementsContainer;

  @BeforeEach
  void beforeEach() {
    this.unknownScalarFunction = new UnknownScalar();
    // Can't actually mock class, but it's not used so to convey intent using
    this.unknownScalarType =
        this.unknownScalarFunction.buildType(
            false, mockAnnotatedClass, mockAnnotatedType, mockProcessingElementsContainer);
  }

  @Test
  void canDetermineIfConvertible() {
    // We should only use unknown if we can't type narrower than object in java
    assertTrue(this.unknownScalarFunction.canBuildType(Object.class, this.mockAnnotatedType));
    assertFalse(this.unknownScalarFunction.canBuildType(String.class, this.mockAnnotatedType));
    assertFalse(this.unknownScalarFunction.canBuildType(Integer.class, this.mockAnnotatedType));
    assertFalse(this.unknownScalarFunction.canBuildType(Long.class, this.mockAnnotatedType));
    assertFalse(this.unknownScalarFunction.canBuildType(Instant.class, this.mockAnnotatedType));
    assertFalse(this.unknownScalarFunction.canBuildType(Float.class, this.mockAnnotatedType));
    assertFalse(this.unknownScalarFunction.canBuildType(Double.class, this.mockAnnotatedType));
    assertFalse(this.unknownScalarFunction.canBuildType(Boolean.class, this.mockAnnotatedType));
    assertFalse(this.unknownScalarFunction.canBuildType(List.class, this.mockAnnotatedType));
    assertFalse(this.unknownScalarFunction.canBuildType(Enum.class, this.mockAnnotatedType));
  }

  @Test
  void canSerialize() {
    assertEquals("five", unknownScalarType.getCoercing().serialize("five"));
    assertEquals(5, unknownScalarType.getCoercing().serialize(5));
    assertEquals(5.0d, unknownScalarType.getCoercing().serialize(5.0d));
    assertEquals(true, unknownScalarType.getCoercing().serialize(true));
    assertEquals(List.of(5), unknownScalarType.getCoercing().serialize(List.of(5)));
  }

  @Test
  void canConvertFromLiteral() {
    assertEquals(
        "five",
        unknownScalarType.getCoercing().parseLiteral(StringValue.newStringValue("five").build()));
    assertEquals(
        BigInteger.valueOf(5),
        unknownScalarType
            .getCoercing()
            .parseLiteral(IntValue.newIntValue(BigInteger.valueOf(5)).build()));
    assertEquals(
        BigDecimal.valueOf(5.0d),
        unknownScalarType
            .getCoercing()
            .parseLiteral(FloatValue.newFloatValue(BigDecimal.valueOf(5.0d)).build()));
    assertEquals(
        true,
        unknownScalarType.getCoercing().parseLiteral(BooleanValue.newBooleanValue(true).build()));
    assertEquals(
        List.of("five"),
        unknownScalarType
            .getCoercing()
            .parseLiteral(
                ArrayValue.newArrayValue()
                    .value(StringValue.newStringValue("five").build())
                    .build()));

    assertThrows(
        CoercingParseLiteralException.class,
        () -> unknownScalarType.getCoercing().parseLiteral("bad value"));
  }

  @Test
  void canConvertFromValue() {
    // A dumb bug requires a dumb test
    assertEquals(true, unknownScalarType.getCoercing().parseValue(true));

    assertEquals("value", unknownScalarType.getCoercing().parseValue("value"));

    assertEquals(10, unknownScalarType.getCoercing().parseValue(10));

    assertEquals(10.5, unknownScalarType.getCoercing().parseValue(10.5));
  }
}
