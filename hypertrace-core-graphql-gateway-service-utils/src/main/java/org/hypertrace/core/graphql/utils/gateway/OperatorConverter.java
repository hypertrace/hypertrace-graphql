package org.hypertrace.core.graphql.utils.gateway;

import io.reactivex.rxjava3.core.Single;
import java.util.UnknownFormatConversionException;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterOperatorType;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.gateway.service.v1.common.Operator;

class OperatorConverter implements Converter<FilterOperatorType, Operator> {

  @Override
  public Single<Operator> convert(FilterOperatorType filterOperatorType) {
    switch (filterOperatorType) {
      case LESS_THAN:
        return Single.just(Operator.LT);
      case LESS_THAN_OR_EQUAL_TO:
        return Single.just(Operator.LE);
      case GREATER_THAN:
        return Single.just(Operator.GT);
      case GREATER_THAN_OR_EQUAL_TO:
        return Single.just(Operator.GE);
      case EQUALS:
        return Single.just(Operator.EQ);
      case NOT_EQUALS:
        return Single.just(Operator.NEQ);
      case IN:
        return Single.just(Operator.IN);
      case NOT_IN:
        return Single.just(Operator.NOT_IN);
      case LIKE:
        return Single.just(Operator.LIKE);
      case CONTAINS_KEY:
        return Single.just(Operator.CONTAINS_KEY);
      case CONTAINS_KEY_VALUE:
        return Single.just(Operator.CONTAINS_KEYVALUE);
      case CONTAINS_KEY_LIKE:
        return Single.just(Operator.CONTAINS_KEY_LIKE);
      default:
        return Single.error(
            new UnknownFormatConversionException(
                String.format(
                    "Unable to convert unknown operator '%s'", filterOperatorType.name())));
    }
  }
}
