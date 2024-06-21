package org.hypertrace.core.graphql.utils.gateway;

import io.reactivex.rxjava3.core.Single;
import org.hypertrace.core.graphql.common.request.AttributeAssociation;
import org.hypertrace.core.graphql.common.schema.attributes.arguments.AttributeExpression;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.gateway.service.v1.common.AttributeExpression.Builder;
import org.hypertrace.gateway.service.v1.common.Expression;

class AttributeExpressionConverter
    implements Converter<AttributeAssociation<AttributeExpression>, Expression> {

  @Override
  public Single<Expression> convert(
      AttributeAssociation<AttributeExpression> attributeExpressionAssociation) {
    Builder builder = org.hypertrace.gateway.service.v1.common.AttributeExpression.newBuilder();
    builder.setAttributeId(attributeExpressionAssociation.attribute().id());
    attributeExpressionAssociation.value().subpath().ifPresent(builder::setSubpath);
    builder.setAlias(attributeExpressionAssociation.value().asAlias());
    return Single.just(Expression.newBuilder().setAttributeExpression(builder).build());
  }
}
