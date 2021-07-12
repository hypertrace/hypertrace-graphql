package org.hypertrace.core.graphql.attributes;

import static org.hypertrace.core.attribute.service.v1.AttributeServiceGrpc.AttributeServiceStub;
import static org.hypertrace.core.attribute.service.v1.AttributeServiceGrpc.newStub;

import io.grpc.CallCredentials;
import io.reactivex.rxjava3.core.Observable;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.hypertrace.core.attribute.service.v1.AttributeMetadata;
import org.hypertrace.core.attribute.service.v1.Empty;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.spi.config.GraphQlServiceConfig;
import org.hypertrace.core.graphql.utils.grpc.GrpcChannelRegistry;
import org.hypertrace.core.graphql.utils.grpc.GrpcContextBuilder;
import org.hypertrace.core.grpcutils.client.rx.GrpcRxExecutionContext;

@Singleton
class AttributeClient {

  private final AttributeServiceStub attributeServiceClient;
  private final GrpcContextBuilder grpcContextBuilder;
  private final AttributeModelTranslator translator;
  private final GraphQlServiceConfig serviceConfig;

  @Inject
  AttributeClient(
      GraphQlServiceConfig serviceConfig,
      GrpcContextBuilder grpcContextBuilder,
      GrpcChannelRegistry grpcChannelRegistry,
      CallCredentials credentials,
      AttributeModelTranslator translator) {
    this.grpcContextBuilder = grpcContextBuilder;
    this.translator = translator;
    this.serviceConfig = serviceConfig;

    this.attributeServiceClient =
        newStub(
                grpcChannelRegistry.forAddress(
                    serviceConfig.getAttributeServiceHost(),
                    serviceConfig.getAttributeServicePort()))
            .withCallCredentials(credentials);
  }

  public Observable<AttributeModel> queryAll(GraphQlRequestContext requestContext) {
    return GrpcRxExecutionContext.forContext(this.grpcContextBuilder.build(requestContext))
        .<AttributeMetadata>stream(
            streamObserver ->
                this.attributeServiceClient
                    .withDeadlineAfter(
                        serviceConfig.getAttributeServiceTimeout().toMillis(),
                        TimeUnit.MILLISECONDS)
                    .findAll(Empty.getDefaultInstance(), streamObserver))
        .mapOptional(this.translator::translate);
  }
}
