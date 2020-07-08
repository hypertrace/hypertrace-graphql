package org.hypertrace.core.graphql.utils.gateway;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.hypertrace.core.graphql.common.request.AttributeRequest;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.gateway.service.v1.common.Expression;
import org.hypertrace.gateway.service.v1.common.Expression.Builder;

class SelectionExpressionSetConverter
    implements Converter<Collection<AttributeRequest>, Set<Expression>> {

  private final ColumnIdentifierExpressionConverter columnExpressionConverter;

  @Inject
  SelectionExpressionSetConverter(ColumnIdentifierExpressionConverter columnExpressionConverter) {
    this.columnExpressionConverter = columnExpressionConverter;
  }

  @Override
  public Single<Set<Expression>> convert(Collection<AttributeRequest> attributeRequests) {
    return Observable.fromIterable(attributeRequests)
        .flatMapSingle(this::buildAliasedSelectionExpression)
        .collect(Collectors.toUnmodifiableSet());
  }

  private Single<Expression> buildAliasedSelectionExpression(AttributeRequest attributeRequest) {
    return this.columnExpressionConverter
        .convert(attributeRequest.attribute())
        .map(Expression::toBuilder)
        .doOnSuccess(
            builder -> builder.getColumnIdentifierBuilder().setAlias(attributeRequest.alias()))
        .map(Builder::build);
  }
}
