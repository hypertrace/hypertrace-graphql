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
import org.hypertrace.core.graphql.utils.grpc.GraphQlGrpcContextBuilder;
import org.hypertrace.core.graphql.utils.grpc.GrpcChannelRegistry;

@Singleton
class AttributeClient {
  private static final int DEFAULT_DEADLINE_SEC = 10;
  private final AttributeServiceStub attributeServiceClient;
  private final GraphQlGrpcContextBuilder grpcContextBuilder;
  private final AttributeModelTranslator translator;

  @Inject
  AttributeClient(
      GraphQlServiceConfig serviceConfig,
      GraphQlGrpcContextBuilder grpcContextBuilder,
      GrpcChannelRegistry grpcChannelRegistry,
      CallCredentials credentials,
      AttributeModelTranslator translator) {
    this.grpcContextBuilder = grpcContextBuilder;
    this.translator = translator;

    this.attributeServiceClient =
        newStub(
                grpcChannelRegistry.forAddress(
                    serviceConfig.getAttributeServiceHost(),
                    serviceConfig.getAttributeServicePort()))
            .withCallCredentials(credentials);
  }

  public Observable<AttributeModel> queryAll(GraphQlRequestContext requestContext) {
    return this.grpcContextBuilder
        .build(requestContext)
        .<AttributeMetadata>streamInContext(
            streamObserver ->
                this.attributeServiceClient
                    .withDeadlineAfter(DEFAULT_DEADLINE_SEC, TimeUnit.SECONDS)
                    .findAll(Empty.getDefaultInstance(), streamObserver))
        .mapOptional(this.translator::translate);
  }
}
