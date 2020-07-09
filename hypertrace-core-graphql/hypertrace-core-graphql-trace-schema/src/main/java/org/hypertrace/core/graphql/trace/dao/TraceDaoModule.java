package org.hypertrace.core.graphql.trace.dao;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import io.grpc.CallCredentials;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hypertrace.core.graphql.common.request.AttributeAssociation;
import org.hypertrace.core.graphql.common.request.AttributeRequest;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterArgument;
import org.hypertrace.core.graphql.common.schema.results.arguments.order.OrderArgument;
import org.hypertrace.core.graphql.common.utils.BiConverter;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.core.graphql.spi.config.GraphQlServiceConfig;
import org.hypertrace.core.graphql.utils.grpc.GraphQlGrpcContextBuilder;
import org.hypertrace.core.graphql.utils.grpc.GrpcChannelRegistry;
import org.hypertrace.gateway.service.v1.common.Expression;
import org.hypertrace.gateway.service.v1.common.Filter;
import org.hypertrace.gateway.service.v1.common.OrderByExpression;
import org.hypertrace.gateway.service.v1.common.Value;

public class TraceDaoModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(TraceDao.class).to(GatewayServiceTraceDao.class);
    requireBinding(CallCredentials.class);
    requireBinding(GraphQlServiceConfig.class);
    requireBinding(GraphQlGrpcContextBuilder.class);
    requireBinding(GrpcChannelRegistry.class);

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
