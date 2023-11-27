package org.hypertrace.core.graphql.utils.gateway;

import static io.reactivex.rxjava3.core.Single.zip;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.Collection;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.hypertrace.core.graphql.common.request.AttributeAssociation;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterArgument;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.gateway.service.v1.common.Filter;
import org.hypertrace.gateway.service.v1.common.Operator;

public class MultiFilterConverter
    implements Converter<Collection<Collection<AttributeAssociation<FilterArgument>>>, Filter> {

  private final AttributeExpressionConverter attributeExpressionConverter;
  private final OperatorConverter operatorConverter;
  private final LiteralConstantExpressionConverter literalConstantExpressionConverter;

  @Inject
  MultiFilterConverter(
      AttributeExpressionConverter attributeExpressionConverter,
      OperatorConverter operatorConverter,
      LiteralConstantExpressionConverter literalConstantExpressionConverter) {
    this.attributeExpressionConverter = attributeExpressionConverter;
    this.operatorConverter = operatorConverter;
    this.literalConstantExpressionConverter = literalConstantExpressionConverter;
  }

  @Override
  public Single<Filter> convert(
      Collection<Collection<AttributeAssociation<FilterArgument>>> filters) {
    if (filters.isEmpty()) {
      return Single.just(Filter.getDefaultInstance());
    }

    return Observable.fromIterable(filters)
        .flatMapSingle(this::buildAndFilterOperations)
        .collect(Collectors.toUnmodifiableList())
        .map(
            filterList ->
                Filter.newBuilder().setOperator(Operator.OR).addAllChildFilter(filterList).build());
  }

  private Single<Filter> buildAndFilterOperations(
      Collection<AttributeAssociation<FilterArgument>> andFilters) {
    return Observable.fromIterable(andFilters)
        .flatMapSingle(this::buildFilter)
        .collect(Collectors.toUnmodifiableList())
        .map(
            filterList ->
                Filter.newBuilder()
                    .setOperator(Operator.AND)
                    .addAllChildFilter(filterList)
                    .build());
  }

  private Single<Filter> buildFilter(AttributeAssociation<FilterArgument> filter) {
    return zip(
        this.attributeExpressionConverter.convert(
            AttributeAssociation.of(filter.attribute(), filter.value().keyExpression())),
        this.operatorConverter.convert(filter.value().operator()),
        this.literalConstantExpressionConverter.convert(filter.value().value()),
        (key, operator, value) ->
            Filter.newBuilder().setLhs(key).setOperator(operator).setRhs(value).build());
  }
}
