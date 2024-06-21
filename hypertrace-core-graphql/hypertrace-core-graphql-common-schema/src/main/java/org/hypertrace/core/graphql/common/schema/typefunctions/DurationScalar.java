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
import java.time.Duration;
import java.util.function.Function;

public class DurationScalar implements TypeFunction {

  private static final GraphQLScalarType DURATION_SCALAR =
      GraphQLScalarType.newScalar()
          .name("Duration")
          .description("An ISO-8601 formatted Duration Scalar")
          .coercing(
              new Coercing<Duration, String>() {
                @Override
                public String serialize(Object fetcherResult) throws CoercingSerializeException {
                  return toDuration(fetcherResult, CoercingSerializeException::new).toString();
                }

                @Override
                public Duration parseValue(Object input) throws CoercingParseValueException {
                  return toDuration(input, CoercingParseValueException::new);
                }

                @Override
                public Duration parseLiteral(Object input) throws CoercingParseLiteralException {
                  return toDuration(input, CoercingParseLiteralException::new);
                }

                private <E extends GraphqlErrorException> Duration toDuration(
                    Object durationInput, Function<Exception, E> errorWrapper) throws E {
                  try {
                    if (durationInput instanceof Duration) {
                      return Duration.from((Duration) durationInput);
                    }
                    if (durationInput instanceof CharSequence) {
                      return Duration.parse((CharSequence) durationInput);
                    }
                    if (durationInput instanceof StringValue) {
                      return Duration.parse(((StringValue) durationInput).getValue());
                    }
                  } catch (Exception exception) {
                    throw errorWrapper.apply(exception);
                  }
                  throw errorWrapper.apply(
                      new DateTimeException(
                          String.format(
                              "Cannot convert provided format '%s' to Duration",
                              durationInput.getClass().getCanonicalName())));
                }
              })
          .build();

  @Override
  public boolean canBuildType(Class<?> aClass, AnnotatedType annotatedType) {
    return Duration.class.isAssignableFrom(aClass);
  }

  @Override
  public GraphQLScalarType buildType(
      boolean input,
      Class<?> aClass,
      AnnotatedType annotatedType,
      ProcessingElementsContainer container) {
    return DURATION_SCALAR;
  }
}
