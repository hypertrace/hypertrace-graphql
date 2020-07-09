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

class FilterConverter
    implements Converter<Collection<AttributeAssociation<FilterArgument>>, Filter> {

  private final ColumnIdentifierExpressionConverter columnIdentifierExpressionConverter;
  private final OperatorConverter operatorConverter;
  private final LiteralConstantExpressionConverter literalConstantExpressionConverter;

  @Inject
  FilterConverter(
      ColumnIdentifierExpressionConverter columnIdentifierExpressionConverter,
      OperatorConverter operatorConverter,
      LiteralConstantExpressionConverter literalConstantExpressionConverter) {
    this.columnIdentifierExpressionConverter = columnIdentifierExpressionConverter;
    this.operatorConverter = operatorConverter;
    this.literalConstantExpressionConverter = literalConstantExpressionConverter;
  }

  @Override
  public Single<Filter> convert(Collection<AttributeAssociation<FilterArgument>> filters) {
    if (filters.isEmpty()) {
      return Single.just(Filter.getDefaultInstance());
    }

    return Observable.fromIterable(filters)
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
        this.columnIdentifierExpressionConverter.convert(filter.attribute()),
        this.operatorConverter.convert(filter.value().operator()),
        this.literalConstantExpressionConverter.convert(filter.value().value()),
        (key, operator, value) ->
            Filter.newBuilder().setLhs(key).setOperator(operator).setRhs(value).build());
  }
}
