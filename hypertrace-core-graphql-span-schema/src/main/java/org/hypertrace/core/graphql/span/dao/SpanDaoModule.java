package org.hypertrace.core.graphql.span.dao;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import io.grpc.CallCredentials;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hypertrace.core.graphql.attributes.AttributeStore;
import org.hypertrace.core.graphql.common.request.AttributeAssociation;
import org.hypertrace.core.graphql.common.request.AttributeRequest;
import org.hypertrace.core.graphql.common.request.FilterRequestBuilder;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterArgument;
import org.hypertrace.core.graphql.common.schema.results.arguments.order.OrderArgument;
import org.hypertrace.core.graphql.common.utils.BiConverter;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializer;
import org.hypertrace.core.graphql.spi.config.GraphQlServiceConfig;
import org.hypertrace.core.graphql.utils.grpc.GrpcChannelRegistry;
import org.hypertrace.core.graphql.utils.grpc.GrpcContextBuilder;
import org.hypertrace.gateway.service.GatewayServiceGrpc.GatewayServiceFutureStub;
import org.hypertrace.gateway.service.v1.common.Expression;
import org.hypertrace.gateway.service.v1.common.Filter;
import org.hypertrace.gateway.service.v1.common.OrderByExpression;
import org.hypertrace.gateway.service.v1.common.Value;

public class SpanDaoModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(SpanDao.class).to(GatewayServiceSpanDao.class);
    requireBinding(GatewayServiceFutureStub.class);
    requireBinding(CallCredentials.class);
    requireBinding(GraphQlServiceConfig.class);
    requireBinding(GrpcContextBuilder.class);
    requireBinding(GrpcChannelRegistry.class);
    requireBinding(FilterRequestBuilder.class);
    requireBinding(ArgumentDeserializer.class);
    requireBinding(AttributeStore.class);

    requireBinding(
        Key.get(new TypeLiteral<Converter<Collection<AttributeRequest>, Set<Expression>>>() {}));

    requireBinding(
        Key.get(
            new TypeLiteral<
                Converter<
                    List<AttributeAssociation<OrderArgument>>, List<OrderByExpression>>>() {}));

    requireBinding(
        Key.get(
            new TypeLiteral<
                Converter<Collection<AttributeAssociation<FilterArgument>>, Filter>>() {}));

    requireBinding(
        Key.get(
            new TypeLiteral<
                BiConverter<
                    Collection<AttributeRequest>, Map<String, Value>, Map<String, Object>>>() {}));
  }
}
