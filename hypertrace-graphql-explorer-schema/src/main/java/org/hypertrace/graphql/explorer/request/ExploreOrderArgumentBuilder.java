package org.hypertrace.graphql.explorer.request;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.attributes.AttributeModel;
import org.hypertrace.core.graphql.attributes.AttributeStore;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.graphql.explorer.schema.ExploreResult;
import org.hypertrace.graphql.metric.schema.argument.AggregatableOrderArgument;

class ExploreOrderArgumentBuilder {

  private final AttributeStore attributeStore;

  @Inject
  ExploreOrderArgumentBuilder(AttributeStore attributeStore) {
    this.attributeStore = attributeStore;
  }

  Single<List<ExploreOrderArgument>> buildList(
      GraphQlRequestContext context,
      String requestScope,
      List<AggregatableOrderArgument> providedOrders) {

    return Observable.fromIterable(providedOrders)
        .concatMapSingle(
            argument -> this.buildExploreOrderArgument(context, requestScope, argument))
        .collect(Collectors.toUnmodifiableList());
  }

  private Single<ExploreOrderArgument> buildExploreOrderArgument(
      GraphQlRequestContext requestContext, String scope, AggregatableOrderArgument argument) {

    if (ExploreResult.EXPLORE_RESULT_INTERVAL_START_KEY.equals(argument.key())) {
      return this.buildIntervalStartExploreOrderArgument(argument);
    }
    return this.buildAttributeExploreOrderArgument(requestContext, scope, argument);
  }

  private Single<ExploreOrderArgument> buildIntervalStartExploreOrderArgument(
      AggregatableOrderArgument argument) {
    return Single.just(new IntervalStartExploreOrderArgument(argument));
  }

  private Single<ExploreOrderArgument> buildAttributeExploreOrderArgument(
      GraphQlRequestContext requestContext, String scope, AggregatableOrderArgument argument) {
    return this.attributeStore
        .get(requestContext, scope, argument.key())
        .map(model -> new AttributeExploreOrderArgument(argument, Optional.of(model)));
  }

  @Value
  @Accessors(fluent = true)
  private static class IntervalStartExploreOrderArgument implements ExploreOrderArgument {
    ExploreOrderArgumentType type = ExploreOrderArgumentType.INTERVAL_START;
    AggregatableOrderArgument argument;
    Optional<AttributeModel> attribute = Optional.empty();
  }

  @Value
  @Accessors(fluent = true)
  private static class AttributeExploreOrderArgument implements ExploreOrderArgument {
    ExploreOrderArgumentType type = ExploreOrderArgumentType.ATTRIBUTE;
    AggregatableOrderArgument argument;
    Optional<AttributeModel> attribute;
  }
}
