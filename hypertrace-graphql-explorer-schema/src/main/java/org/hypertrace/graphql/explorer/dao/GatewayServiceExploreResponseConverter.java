package org.hypertrace.graphql.explorer.dao;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.attributes.AttributeModel;
import org.hypertrace.core.graphql.common.request.AttributeRequest;
import org.hypertrace.gateway.service.v1.common.Row;
import org.hypertrace.gateway.service.v1.explore.ExploreResponse;
import org.hypertrace.graphql.explorer.fetcher.ExploreResultMapKey;
import org.hypertrace.graphql.explorer.request.ExploreRequest;
import org.hypertrace.graphql.explorer.schema.ExploreResult;
import org.hypertrace.graphql.explorer.schema.ExploreResultSet;
import org.hypertrace.graphql.explorer.schema.Selection;

class GatewayServiceExploreResponseConverter {

  private final GatewayServiceSelectionMapConverter selectionMapConverter;

  @Inject
  GatewayServiceExploreResponseConverter(
      GatewayServiceSelectionMapConverter selectionMapConverter) {
    this.selectionMapConverter = selectionMapConverter;
  }

  Single<ExploreResultSet> convert(ExploreRequest request, ExploreResponse response) {
    return Observable.fromIterable(response.getRowList())
        .flatMapSingle(row -> this.convertRow(request, row))
        .toList()
        .map(results -> new ConvertedExploreResultSet(results, results.size()));
  }

  private Single<ExploreResult> convertRow(ExploreRequest request, Row row) {
    return this.selectionMapConverter
        .convert(request, row)
        .map(selectionMap -> this.buildResult(request, selectionMap));
  }

  private ExploreResult buildResult(
      ExploreRequest request, Map<ExploreResultMapKey, Selection> selectionMap) {
    // TODO remove once first class group name removed, til then just give the first group name
    Optional<String> groupName =
        request.groupByAttributeRequests().stream()
            .findFirst()
            .map(AttributeRequest::attribute)
            .map(AttributeModel::key)
            .map(ExploreResultMapKey::basicAttribute)
            .map(selectionMap::get)
            .map(Selection::value)
            .map(String.class::cast);

    Optional<Instant> intervalStart =
        Optional.ofNullable(selectionMap.get(ExploreResultMapKey.intervalStart()))
            .map(Selection::value)
            .map(Instant.class::cast);

    return new ConvertedExploreResult(
        selectionMap, groupName.orElse(null), intervalStart.orElse(null));
  }

  @Value
  @Accessors(fluent = true)
  private static class ConvertedExploreResult implements ExploreResult {
    Map<ExploreResultMapKey, Selection> selectionMap;
    String groupName;
    Instant intervalStart;
  }

  @Value
  @Accessors(fluent = true)
  private static class ConvertedExploreResultSet implements ExploreResultSet {
    List<ExploreResult> results;
    long count;

    @Override
    public long total() {
      //  Total not currently exposed by gateway
      return this.results.size();
    }
  }
}
