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
import java.time.OffsetTime;
import java.time.temporal.TemporalAccessor;
import java.util.function.Function;

public class OffsetTimeScalar implements TypeFunction {

  private static final GraphQLScalarType OFFSET_TIME_SCALAR =
      GraphQLScalarType.newScalar()
          .name("OffsetTime")
          .description("An ISO-8601 formatted OffsetTime Scalar")
          .coercing(
              new Coercing<OffsetTime, String>() {
                @Override
                public String serialize(Object fetcherResult) throws CoercingSerializeException {
                  return toOffsetTime(fetcherResult, CoercingSerializeException::new).toString();
                }

                @Override
                public OffsetTime parseValue(Object input) throws CoercingParseValueException {
                  return toOffsetTime(input, CoercingParseValueException::new);
                }

                @Override
                public OffsetTime parseLiteral(Object input) throws CoercingParseLiteralException {
                  return toOffsetTime(input, CoercingParseLiteralException::new);
                }

                private <E extends GraphqlErrorException> OffsetTime toOffsetTime(
                    Object offsetInput, Function<Exception, E> errorWrapper) throws E {
                  try {
                    if (offsetInput instanceof TemporalAccessor) {
                      return OffsetTime.from((TemporalAccessor) offsetInput);
                    }
                    if (offsetInput instanceof CharSequence) {
                      return OffsetTime.parse((CharSequence) offsetInput);
                    }
                    if (offsetInput instanceof StringValue) {
                      return OffsetTime.parse(((StringValue) offsetInput).getValue());
                    }
                  } catch (DateTimeException exception) {
                    throw errorWrapper.apply(exception);
                  }
                  throw errorWrapper.apply(
                      new DateTimeException(
                          String.format(
                              "Cannot convert provided format '%s' to OffsetTime",
                              offsetInput.getClass().getCanonicalName())));
                }
              })
          .build();

  @Override
  public boolean canBuildType(Class<?> aClass, AnnotatedType annotatedType) {
    return OffsetTime.class.isAssignableFrom(aClass);
  }

  @Override
  public GraphQLScalarType buildType(
      boolean input,
      Class<?> aClass,
      AnnotatedType annotatedType,
      ProcessingElementsContainer container) {
    return OFFSET_TIME_SCALAR;
  }
}
