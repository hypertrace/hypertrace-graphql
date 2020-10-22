package org.hypertrace.graphql.entity.dao;

import static io.reactivex.rxjava3.core.Single.zip;
import static org.hypertrace.graphql.entity.dao.GatewayServiceEntityEdgeFetcher.EMPTY_EDGE_RESULT_SET;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import javax.inject.Inject;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.common.request.AttributeRequest;
import org.hypertrace.core.graphql.common.utils.BiConverter;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.gateway.service.v1.common.Value;
import org.hypertrace.gateway.service.v1.entity.EntitiesResponse;
import org.hypertrace.graphql.entity.dao.GatewayServiceEntityEdgeLookupConverter.EdgeLookup;
import org.hypertrace.graphql.entity.request.EntityRequest;
import org.hypertrace.graphql.entity.schema.EdgeResultSet;
import org.hypertrace.graphql.entity.schema.Entity;
import org.hypertrace.graphql.entity.schema.EntityResultSet;
import org.hypertrace.graphql.entity.schema.EntityType;
import org.hypertrace.graphql.metric.request.MetricRequest;
import org.hypertrace.graphql.metric.schema.MetricContainer;

class GatewayServiceEntityConverter {
  private final BiConverter<Collection<AttributeRequest>, Map<String, Value>, Map<String, Object>>
      attributeMapConverter;

  private final BiConverter<
          Collection<MetricRequest>,
          org.hypertrace.gateway.service.v1.entity.Entity,
          Map<String, MetricContainer>>
      metricContainerConverter;
  private final GatewayServiceEntityEdgeLookupConverter edgeLookupConverter;
  private final Converter<String, Optional<EntityType>> entityTypeConverter;

  @Inject
  GatewayServiceEntityConverter(
      BiConverter<Collection<AttributeRequest>, Map<String, Value>, Map<String, Object>>
          attributeMapConverter,
      BiConverter<
              Collection<MetricRequest>,
              org.hypertrace.gateway.service.v1.entity.Entity,
              Map<String, MetricContainer>>
          metricContainerConverter,
      GatewayServiceEntityEdgeLookupConverter edgeLookupConverter,
      Converter<String, Optional<EntityType>> entityTypeConverter) {
    this.attributeMapConverter = attributeMapConverter;
    this.metricContainerConverter = metricContainerConverter;
    this.edgeLookupConverter = edgeLookupConverter;
    this.entityTypeConverter = entityTypeConverter;
  }

  Single<EntityResultSet> convert(EntityRequest request, EntitiesResponse response) {
    return this.edgeLookupConverter
        .convert(request, response)
        .flatMap(edgeLookup -> this.convert(request, response, edgeLookup));
  }

  private Single<EntityResultSet> convert(
      EntityRequest request, EntitiesResponse response, EdgeLookup edgeLookup) {

    return Observable.fromIterable(response.getEntityList())
        .flatMapSingle(
            entity ->
                this.convertEntity(
                    request,
                    entity,
                    edgeLookup.getIncoming().row(entity),
                    edgeLookup.getOutgoing().row(entity)))
        .toList()
        .map(
            entities ->
                new ConvertedEntityResultSet(entities, response.getTotal(), entities.size()));
  }

  private Single<Entity> convertEntity(
      EntityRequest entityRequest,
      org.hypertrace.gateway.service.v1.entity.Entity platformEntity,
      Map<String, EdgeResultSet> incomingEdges,
      Map<String, EdgeResultSet> outgoingEdges) {
    return zip(
        this.entityTypeConverter.convert(entityRequest.entityType()),
        this.attributeMapConverter.convert(
            entityRequest.resultSetRequest().attributes(), platformEntity.getAttributeMap()),
        this.metricContainerConverter.convert(entityRequest.metricRequests(), platformEntity),
        (entityTypeOptional, attrMap, containerMap) ->
            new ConvertedEntity(
                attrMap
                    .get(entityRequest.resultSetRequest().idAttribute().attribute().key())
                    .toString(),
                entityTypeOptional.orElse(null),
                entityRequest.entityType(),
                attrMap,
                containerMap,
                incomingEdges,
                outgoingEdges));
  }

  @lombok.Value
  @Accessors(fluent = true)
  private static class ConvertedEntity implements Entity {
    String id;
    EntityType type;
    String typeString;
    Map<String, Object> attributeValues;
    Map<String, MetricContainer> metricContainers;
    Map<String, EdgeResultSet> incomingEdges;
    Map<String, EdgeResultSet> outgoingEdges;

    @Override
    public Object attribute(String key) {
      return this.attributeValues.get(key);
    }

    @Override
    public MetricContainer metric(String key) {
      return this.metricContainers.get(key);
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
