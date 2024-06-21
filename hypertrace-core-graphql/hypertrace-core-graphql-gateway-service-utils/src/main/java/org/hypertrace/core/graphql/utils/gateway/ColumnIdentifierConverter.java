package org.hypertrace.core.graphql.utils.gateway;

import io.reactivex.rxjava3.core.Single;
import org.hypertrace.core.graphql.attributes.AttributeModel;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.gateway.service.v1.common.ColumnIdentifier;
import org.hypertrace.gateway.service.v1.common.ColumnIdentifier.Builder;

class ColumnIdentifierConverter implements Converter<AttributeModel, ColumnIdentifier> {

  @Override
  public Single<ColumnIdentifier> convert(AttributeModel attribute) {
    return Single.just(attribute.id())
        .map(ColumnIdentifier.newBuilder()::setColumnName)
        .map(Builder::build);
  }
}
