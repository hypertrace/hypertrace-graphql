package org.hypertrace.graphql.spaces.dao;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;

import io.reactivex.rxjava3.core.Single;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.common.request.AttributeAssociation;
import org.hypertrace.core.graphql.common.request.AttributeRequest;
import org.hypertrace.core.graphql.common.request.AttributeRequestBuilder;
import org.hypertrace.core.graphql.common.schema.arguments.TimeRangeArgument;
import org.hypertrace.core.graphql.common.schema.attributes.MetricAggregationType;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterArgument;
import org.hypertrace.core.graphql.common.schema.results.arguments.order.OrderDirection;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.graphql.explorer.dao.ExplorerDao;
import org.hypertrace.graphql.explorer.fetcher.ExploreResultMapKey;
import org.hypertrace.graphql.explorer.request.ExploreRequest;
import org.hypertrace.graphql.explorer.schema.ExploreResultSet;
import org.hypertrace.graphql.explorer.schema.Selection;
import org.hypertrace.graphql.explorer.schema.argument.IntervalArgument;
import org.hypertrace.graphql.metric.request.MetricAggregationRequest;
import org.hypertrace.graphql.metric.schema.argument.AggregatableOrderArgument;
import org.hypertrace.graphql.spaces.schema.query.Space;
import org.hypertrace.graphql.spaces.schema.query.SpaceResultSet;

class ExplorerBackedSpacesDao implements SpacesDao {

  private final ExplorerDao explorerDao;
  private final AttributeRequestBuilder attributeRequestBuilder;

  @Inject
  ExplorerBackedSpacesDao(
      ExplorerDao explorerDao, AttributeRequestBuilder attributeRequestBuilder) {

    this.explorerDao = explorerDao;
    this.attributeRequestBuilder = attributeRequestBuilder;
  }

  @Override
  public Single<SpaceResultSet> getAllSpaces(GraphQlRequestContext context) {
    return this.buildRequest(context).flatMap(this.explorerDao::explore).map(this::extractResult);
  }

  private Single<ExploreRequest> buildRequest(GraphQlRequestContext context) {
    return this.attributeRequestBuilder
        .buildForKey(
            context,
            ActiveSpaceExploreRequest.SPACE_IDS_SCOPE,
            ActiveSpaceExploreRequest.SPACE_IDS_KEY)
        .map(attributeRequest -> new ActiveSpaceExploreRequest(context, attributeRequest));
  }

  private SpaceResultSet extractResult(ExploreResultSet exploreResultSet) {

    List<Space> spaces =
        exploreResultSet.results().stream()
            .map(
                exploreResult ->
                    exploreResult
                        .selectionMap()
                        .get(
                            ExploreResultMapKey.basicAttribute(
                                ActiveSpaceExploreRequest.SPACE_IDS_KEY)))
            .map(Selection::value)
            .filter(Objects::nonNull)
            .map(Object::toString)
            .map(ExtractedSpace::new)
            .collect(Collectors.toList());

    return new ExtractedSpaceResultSet(spaces, spaces.size(), spaces.size());
  }

  @Value
  @Accessors(fluent = true)
  private static class ExtractedSpaceResultSet implements SpaceResultSet {
    List<Space> results;
    long count;
    long total;
  }

  @Value
  @Accessors(fluent = true)
  private static class ExtractedSpace implements Space {
    String name;
  }

  private static class FixedTimeRange implements TimeRangeArgument {
    private final Instant FIXED_TIME_RANGE_START = Instant.parse("2021-01-01T00:00:00.00Z");

    @Override
    public Instant startTime() {
      return FIXED_TIME_RANGE_START;
    }

    @Override
    public Instant endTime() {
      return Instant.now();
    }
  }

  @Value
  @Accessors(fluent = true)
  private static class ActiveSpaceExploreRequest implements ExploreRequest {
    private static final String SPACE_IDS_KEY = "spaceIds";
    private static final String SPACE_IDS_SCOPE = "EVENT";

    GraphQlRequestContext requestContext;
    String scope;
    TimeRangeArgument timeRange;
    int limit;
    int offset;
    Set<AttributeRequest> attributeRequests;
    Set<MetricAggregationRequest> aggregationRequests;
    List<AttributeAssociation<AggregatableOrderArgument>> orderArguments;
    List<AttributeAssociation<FilterArgument>> filterArguments;
    Set<AttributeRequest> groupByAttributeRequests;
    Optional<IntervalArgument> timeInterval;
    boolean includeRest;
    Optional<String> spaceId;

    ActiveSpaceExploreRequest(GraphQlRequestContext context, AttributeRequest spaceIdRequest) {
      this.requestContext = context;
      this.scope = SPACE_IDS_SCOPE;
      this.limit = 100;
      this.offset = 0;
      this.timeRange = new FixedTimeRange();
      this.attributeRequests = emptySet();
      this.aggregationRequests = emptySet();
      this.orderArguments =
          List.of(AttributeAssociation.of(spaceIdRequest.attribute(), new SpaceOrderArgument()));
      this.filterArguments = emptyList();
      this.groupByAttributeRequests = Set.of(spaceIdRequest);
      this.timeInterval = Optional.empty();
      this.includeRest = false;
      this.spaceId = Optional.empty();
    }
  }

  @Value
  @Accessors(fluent = true)
  private static class SpaceOrderArgument implements AggregatableOrderArgument {
    OrderDirection direction = OrderDirection.DESC;
    String key = ActiveSpaceExploreRequest.SPACE_IDS_KEY;
    MetricAggregationType aggregation = null;
    Integer size = null;
  }
}
