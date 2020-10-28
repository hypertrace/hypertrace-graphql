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
import java.time.LocalTime;
import java.time.temporal.TemporalAccessor;
import java.util.function.Function;

/**
 * Graphql Scalar for Time without date or timezone
 */
public class TimeScalar implements TypeFunction {

  private static final GraphQLScalarType TIME_SCALAR =
      GraphQLScalarType.newScalar()
          .name("Time")
          .description("An ISO-8601 formatted Time Scalar")
          .coercing(
              new Coercing<LocalTime, String>() {
                @Override
                public String serialize(Object fetcherResult) throws CoercingSerializeException {
                  return toLocalTime(fetcherResult, CoercingSerializeException::new).toString();
                }

                @Override
                public LocalTime parseValue(Object input) throws CoercingParseValueException {
                  return toLocalTime(input, CoercingParseValueException::new);
                }

                @Override
                public LocalTime parseLiteral(Object input) throws CoercingParseLiteralException {
                  return toLocalTime(input, CoercingParseLiteralException::new);
                }

                private <E extends GraphqlErrorException> LocalTime toLocalTime(
                    Object timeInput, Function<Exception, E> errorWrapper) throws E {
                  try {
                    if (timeInput instanceof TemporalAccessor) {
                      return LocalTime.from((TemporalAccessor) timeInput);
                    }
                    if (timeInput instanceof CharSequence) {
                      return LocalTime.parse((CharSequence) timeInput);
                    }
                    if (timeInput instanceof StringValue) {
                      return LocalTime.parse(((StringValue) timeInput).getValue());
                    }
                  } catch (DateTimeException exception) {
                    throw errorWrapper.apply(exception);
                  }
                  throw errorWrapper.apply(
                      new DateTimeException(
                          String.format(
                              "Cannot convert provided format '%s' to LocalTime",
                              timeInput.getClass().getCanonicalName())));
                }
              })
          .build();

  @Override
  public boolean canBuildType(Class<?> aClass, AnnotatedType annotatedType) {
    return TemporalAccessor.class.isAssignableFrom(aClass);
  }

  @Override
  public GraphQLScalarType buildType(
      boolean input,
      Class<?> aClass,
      AnnotatedType annotatedType,
      ProcessingElementsContainer container) {
    return TIME_SCALAR;
  }
}
