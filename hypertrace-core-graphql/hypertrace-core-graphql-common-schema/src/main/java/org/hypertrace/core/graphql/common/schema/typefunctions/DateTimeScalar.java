package org.hypertrace.core.graphql.common.schema.typefunctions;

import graphql.GraphqlErrorException;
import graphql.annotations.processor.ProcessingElementsContainer;
import graphql.annotations.processor.typeFunctions.TypeFunction;
import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;
import java.lang.reflect.AnnotatedType;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.function.Function;

public class DateTimeScalar implements TypeFunction {

  private static final GraphQLScalarType DATE_TIME_SCALAR =
      GraphQLScalarType.newScalar()
          .name("DateTime")
          .description("An ISO-8601 formatted DateTime Scalar")
          .coercing(
              new Coercing<Instant, String>() {
                @Override
                public String serialize(Object fetcherResult) throws CoercingSerializeException {
                  return toInstant(fetcherResult, CoercingSerializeException::new).toString();
                }

                @Override
                public Instant parseValue(Object input) throws CoercingParseValueException {
                  return toInstant(input, CoercingParseValueException::new);
                }

                @Override
                public Instant parseLiteral(Object input) throws CoercingParseLiteralException {
                  return toInstant(input, CoercingParseLiteralException::new);
                }

                private <E extends GraphqlErrorException> Instant toInstant(
                    Object instantInput, Function<Exception, E> errorWrapper) throws E {
                  try {
                    if (instantInput instanceof TemporalAccessor) {
                      return Instant.from((TemporalAccessor) instantInput);
                    }
                    if (instantInput instanceof CharSequence) {
                      return parse((CharSequence) instantInput);
                    }
                    if (instantInput instanceof StringValue) {
                      return parse(((StringValue) instantInput).getValue());
                    }
                  } catch (DateTimeException exception) {
                    throw errorWrapper.apply(exception);
                  }
                  throw errorWrapper.apply(
                      new DateTimeException(
                          String.format(
                              "Cannot convert provided format '%s' to Instant",
                              instantInput.getClass().getCanonicalName())));
                }
              })
          .build();

  @Override
  public boolean canBuildType(Class<?> aClass, AnnotatedType annotatedType) {
    return Instant.class.isAssignableFrom(aClass);
  }

  @Override
  public GraphQLScalarType buildType(
      boolean input,
      Class<?> aClass,
      AnnotatedType annotatedType,
      ProcessingElementsContainer container) {
    return DATE_TIME_SCALAR;
  }

  private static Instant parse(CharSequence charSequence) {
    return OffsetDateTime.parse(charSequence).toInstant();
  }
}
