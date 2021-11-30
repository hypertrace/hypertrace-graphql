package org.hypertrace.graphql.entity.dao;

import static io.reactivex.rxjava3.core.Single.zip;
import static org.hypertrace.graphql.entity.dao.GatewayServiceEntityEdgeFetcher.EMPTY_EDGE_RESULT_SET;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.inject.Inject;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.common.request.AttributeRequest;
import org.hypertrace.core.graphql.common.schema.attributes.arguments.AttributeExpression;
import org.hypertrace.core.graphql.common.utils.BiConverter;
import org.hypertrace.core.graphql.common.utils.TriConverter;
import org.hypertrace.gateway.service.v1.baseline.BaselineEntitiesResponse;
import org.hypertrace.gateway.service.v1.baseline.BaselineEntity;
import org.hypertrace.gateway.service.v1.common.Value;
import org.hypertrace.gateway.service.v1.entity.EntitiesResponse;
import org.hypertrace.graphql.entity.dao.GatewayServiceEntityEdgeLookupConverter.EdgeLookup;
import org.hypertrace.graphql.entity.request.EntityRequest;
import org.hypertrace.graphql.entity.schema.EdgeResultSet;
import org.hypertrace.graphql.entity.schema.Entity;
import org.hypertrace.graphql.entity.schema.EntityResultSet;
import org.hypertrace.graphql.entity.schema.EntityType;
import org.hypertrace.graphql.label.schema.LabelResultSet;
import org.hypertrace.graphql.metric.request.MetricRequest;
import org.hypertrace.graphql.metric.schema.MetricContainer;

class GatewayServiceEntityConverter {
  private final BiConverter<
          Collection<AttributeRequest>, Map<String, Value>, Map<AttributeExpression, Object>>
      attributeMapConverter;

  private final TriConverter<
          Collection<MetricRequest>,
          org.hypertrace.gateway.service.v1.entity.Entity,
          BaselineEntity,
          Map<AttributeExpression, MetricContainer>>
      metricContainerConverter;
  private final GatewayServiceEntityEdgeLookupConverter edgeLookupConverter;

  @Inject
  GatewayServiceEntityConverter(
      BiConverter<
              Collection<AttributeRequest>, Map<String, Value>, Map<AttributeExpression, Object>>
          attributeMapConverter,
      TriConverter<
              Collection<MetricRequest>,
              org.hypertrace.gateway.service.v1.entity.Entity,
              BaselineEntity,
              Map<AttributeExpression, MetricContainer>>
          metricContainerConverter,
      GatewayServiceEntityEdgeLookupConverter edgeLookupConverter) {
    this.attributeMapConverter = attributeMapConverter;
    this.metricContainerConverter = metricContainerConverter;
    this.edgeLookupConverter = edgeLookupConverter;
  }

  Single<EntityResultSet> convert(
      EntityRequest request,
      EntitiesResponse response,
      BaselineEntitiesResponse baselineResponse,
      Map<org.hypertrace.gateway.service.v1.entity.Entity, LabelResultSet> labelResultSetMap) {
    return this.edgeLookupConverter
        .convert(request, response)
        .flatMap(
            edgeLookup ->
                this.convert(request, response, baselineResponse, edgeLookup, labelResultSetMap));
  }

  private Single<EntityResultSet> convert(
      EntityRequest request,
      EntitiesResponse response,
      BaselineEntitiesResponse baselineResponse,
      EdgeLookup edgeLookup,
      Map<org.hypertrace.gateway.service.v1.entity.Entity, LabelResultSet> labelResultSetMap) {
    Map<String, BaselineEntity> baselineEntityMap = getBaselineEntityMap(baselineResponse);
    return Observable.fromIterable(response.getEntityList())
        .flatMapSingle(
            entity ->
                this.convertEntity(
                    request,
                    entity,
                    getBaselineEntity(baselineEntityMap, entity.getId()),
                    edgeLookup.getIncoming().row(entity),
                    edgeLookup.getOutgoing().row(entity),
                    labelResultSetMap.get(entity)))
        .toList()
        .map(
            entities ->
                new ConvertedEntityResultSet(entities, response.getTotal(), entities.size()));
  }

  private BaselineEntity getBaselineEntity(
      Map<String, BaselineEntity> baselineEntityMap, String entityId) {
    return baselineEntityMap.containsKey(entityId)
        ? baselineEntityMap.get(entityId)
        : BaselineEntity.getDefaultInstance();
  }

  private Map<String, BaselineEntity> getBaselineEntityMap(
      BaselineEntitiesResponse baselineResponse) {
    return baselineResponse.getBaselineEntityList().stream()
        .collect(Collectors.toMap(BaselineEntity::getId, Function.identity()));
  }

  private Single<Entity> convertEntity(
      EntityRequest entityRequest,
      org.hypertrace.gateway.service.v1.entity.Entity platformEntity,
      BaselineEntity baselineEntity,
      Map<String, EdgeResultSet> incomingEdges,
      Map<String, EdgeResultSet> outgoingEdges,
      LabelResultSet labelResultSet) {
    return zip(
        this.attributeMapConverter.convert(
            entityRequest.resultSetRequest().attributes(), platformEntity.getAttributeMap()),
        this.metricContainerConverter.convert(
            entityRequest.metricRequests(), platformEntity, baselineEntity),
        (attrMap, containerMap) ->
            new ConvertedEntity(
                attrMap
                    .get(
                        entityRequest
                            .resultSetRequest()
                            .idAttribute()
                            .attributeExpression()
                            .value())
                    .toString(),
                entityRequest.entityType(),
                attrMap,
                containerMap,
                incomingEdges,
                outgoingEdges,
                labelResultSet));
  }

  @lombok.Value
  @Accessors(fluent = true)
  private static class ConvertedEntity implements Entity {
    String id;
    String type;
    Map<AttributeExpression, Object> attributeValues;
    Map<AttributeExpression, MetricContainer> metricContainers;
    Map<String, EdgeResultSet> incomingEdges;
    Map<String, EdgeResultSet> outgoingEdges;
    LabelResultSet labels;

    public Object attribute(AttributeExpression attributeExpression) {
      return this.attributeValues.get(attributeExpression);
    }

    @Override
    public MetricContainer metric(AttributeExpression attributeExpression) {
      return this.metricContainers.get(attributeExpression);
    }

    @Override
    public EdgeResultSet incomingEdges(EntityType neighborType, String neighborScope) {
      return this.incomingEdges.getOrDefault(
          this.resolveEntityScope(neighborType, neighborScope), EMPTY_EDGE_RESULT_SET);
    }

    @Override
    public EdgeResultSet outgoingEdges(EntityType neighborType, String neighborScope) {

      return this.outgoingEdges.getOrDefault(
          this.resolveEntityScope(neighborType, neighborScope), EMPTY_EDGE_RESULT_SET);
    }

    private String resolveEntityScope(
        @Nullable EntityType entityType, @Nullable String entityScope) {
      return Optional.ofNullable(entityType)
          .map(EntityType::getScopeString)
          .or(() -> Optional.ofNullable(entityScope))
          .orElseThrow(
              () ->
                  new UnsupportedOperationException(
                      "Either neighborType or neighborScope must be provided"));
    }
  }

  @lombok.Value
  @Accessors(fluent = true)
  private static class ConvertedEntityResultSet implements EntityResultSet {
    List<Entity> results;
    long total;
    long count;
  }
}
