package org.hypertrace.core.graphql.utils.gateway;

import io.reactivex.rxjava3.core.Single;
import javax.inject.Inject;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.gateway.service.v1.common.Expression;
import org.hypertrace.gateway.service.v1.common.Expression.Builder;

class LiteralConstantExpressionConverter implements Converter<Object, Expression> {

  private final LiteralConstantConverter constantConverter;

  @Inject
  LiteralConstantExpressionConverter(LiteralConstantConverter constantConverter) {
    this.constantConverter = constantConverter;
  }

  @Override
  public Single<Expression> convert(Object from) {
    return this.constantConverter
        .convert(from)
        .map(Expression.newBuilder()::setLiteral)
        .map(Builder::build);
  }
}
