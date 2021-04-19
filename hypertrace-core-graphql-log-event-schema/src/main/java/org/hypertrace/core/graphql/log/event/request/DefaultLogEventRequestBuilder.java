package org.hypertrace.core.graphql.log.event.request;

import io.reactivex.rxjava3.core.Single;
import java.util.Map;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;

public class DefaultLogEventRequestBuilder implements LogEventRequestBuilder {

  @Override
  public Single<LogEventRequest> build(
      GraphQlRequestContext context, Map<String, Object> arguments) {
    return Single.just(new DefaultLogEventRequest(context));
  }

  @Value
  @Accessors(fluent = true)
  private static class DefaultLogEventRequest implements LogEventRequest {
    GraphQlRequestContext requestContext;
  }
}
