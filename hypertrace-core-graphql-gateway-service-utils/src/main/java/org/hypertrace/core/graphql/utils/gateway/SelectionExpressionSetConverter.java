package org.hypertrace.core.graphql.utils.gateway;

import com.google.common.collect.ImmutableSet;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.Collection;
import java.util.Set;
import javax.inject.Inject;
import org.hypertrace.core.graphql.common.request.AttributeRequest;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.gateway.service.v1.common.Expression;
import org.hypertrace.gateway.service.v1.common.Expression.Builder;

class SelectionExpressionSetConverter
    implements Converter<Collection<AttributeRequest>, Set<Expression>> {

  private final AttributeExpressionConverter attributeExpressionConverter;

  @Inject
  SelectionExpressionSetConverter(AttributeExpressionConverter attributeExpressionConverter) {
    this.attributeExpressionConverter = attributeExpressionConverter;
  }

  @Override
  public Single<Set<Expression>> convert(Collection<AttributeRequest> attributeRequests) {
    return Observable.fromIterable(attributeRequests)
        .flatMapSingle(this::buildAliasedSelectionExpression)
        .collectInto(ImmutableSet.<Expression>builder(), ImmutableSet.Builder::add)
        .map(ImmutableSet.Builder::build);
  }

  private Single<Expression> buildAliasedSelectionExpression(AttributeRequest attributeRequest) {
    return this.attributeExpressionConverter
        .convert(attributeRequest.attributeExpression())
        .map(Expression::toBuilder)
        .map(Builder::build);
  }
}
