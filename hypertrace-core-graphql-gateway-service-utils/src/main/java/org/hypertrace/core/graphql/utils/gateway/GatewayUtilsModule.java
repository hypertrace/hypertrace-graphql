package org.hypertrace.core.graphql.utils.gateway;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hypertrace.core.graphql.attributes.AttributeModel;
import org.hypertrace.core.graphql.common.request.AttributeAssociation;
import org.hypertrace.core.graphql.common.request.AttributeRequest;
import org.hypertrace.core.graphql.common.schema.attributes.arguments.AttributeExpression;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterArgument;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterOperatorType;
import org.hypertrace.core.graphql.common.schema.results.arguments.order.OrderArgument;
import org.hypertrace.core.graphql.common.schema.results.arguments.order.OrderDirection;
import org.hypertrace.core.graphql.common.utils.BiConverter;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.gateway.service.GatewayServiceGrpc.GatewayServiceFutureStub;
import org.hypertrace.gateway.service.v1.common.ColumnIdentifier;
import org.hypertrace.gateway.service.v1.common.Expression;
import org.hypertrace.gateway.service.v1.common.Filter;
import org.hypertrace.gateway.service.v1.common.LiteralConstant;
import org.hypertrace.gateway.service.v1.common.Operator;
import org.hypertrace.gateway.service.v1.common.OrderByExpression;
import org.hypertrace.gateway.service.v1.common.SortOrder;
import org.hypertrace.gateway.service.v1.common.Value;

public class GatewayUtilsModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(Key.get(
            new TypeLiteral<
                BiConverter<
                    Collection<AttributeRequest>,
                    Map<String, Value>,
                    Map<AttributeExpression, Object>>>() {}))
        .to(AttributeMapConverter.class);

    bind(Key.get(new TypeLiteral<Converter<Value, Object>>() {})).to(UnwrappedValueConverter.class);
    bind(Key.get(new TypeLiteral<BiConverter<Value, AttributeModel, Object>>() {}))
        .to(UnwrappedValueConverter.class);
    bind(Key.get(new TypeLiteral<Converter<Collection<AttributeRequest>, Set<Expression>>>() {}))
        .to(SelectionExpressionSetConverter.class);

    bind(Key.get(
            new TypeLiteral<
                Converter<
                    List<AttributeAssociation<OrderArgument>>, List<OrderByExpression>>>() {}))
        .to(OrderByExpressionListConverter.class);

    bind(Key.get(new TypeLiteral<Converter<FilterOperatorType, Operator>>() {}))
        .to(OperatorConverter.class);
    bind(Key.get(
            new TypeLiteral<
                Converter<Collection<AttributeAssociation<FilterArgument>>, Filter>>() {}))
        .to(FilterConverter.class);
    bind(Key.get(
            new TypeLiteral<
                Converter<
                    Collection<Collection<AttributeAssociation<FilterArgument>>>, Filter>>() {}))
        .to(MultiFilterConverter.class);

    bind(Key.get(new TypeLiteral<Converter<AttributeModel, ColumnIdentifier>>() {}))
        .to(ColumnIdentifierConverter.class);
    bind(Key.get(new TypeLiteral<Converter<AttributeModel, Expression>>() {}))
        .to(ColumnIdentifierExpressionConverter.class);
    bind(Key.get(
            new TypeLiteral<Converter<AttributeAssociation<AttributeExpression>, Expression>>() {}))
        .to(AttributeExpressionConverter.class);

    bind(Key.get(new TypeLiteral<Converter<Object, LiteralConstant>>() {}))
        .to(LiteralConstantConverter.class);
    bind(Key.get(new TypeLiteral<Converter<Object, Expression>>() {}))
        .to(LiteralConstantExpressionConverter.class);

    bind(Key.get(new TypeLiteral<Converter<OrderDirection, SortOrder>>() {}))
        .to(SortOrderConverter.class);

    bind(GatewayServiceFutureStub.class)
        .toProvider(GatewayServiceFutureStubProvider.class)
        .in(Singleton.class);
  }
}
