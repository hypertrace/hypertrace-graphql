package org.hypertrace.graphql.label.dao;

import io.reactivex.rxjava3.core.Single;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.common.utils.CollectorUtils;
import org.hypertrace.graphql.entity.schema.Entity;
import org.hypertrace.graphql.entity.schema.EntityResultSet;
import org.hypertrace.graphql.label.schema.Label;
import org.hypertrace.graphql.label.schema.LabelResultSet;
import org.hypertrace.graphql.label.schema.LabeledEntity;
import org.hypertrace.graphql.label.schema.LabeledEntityResultSet;
import org.hypertrace.label.config.service.v1.CreateLabelResponse;
import org.hypertrace.label.config.service.v1.GetLabelsResponse;
import org.hypertrace.label.config.service.v1.UpdateLabelResponse;

public class LabelResponseConverter {

  public Single<LabelResultSet> convert(List<Label> labels) {
    return Single.just(new DefaultLabelResultSet(labels, labels.size(), labels.size()));
  }

  Single<LabelResultSet> convert(GetLabelsResponse response) {
    return convertToLabelList(response)
        .map(labelList -> new DefaultLabelResultSet(labelList, labelList.size(), labelList.size()));
  }

  Single<List<Label>> convertToLabelList(GetLabelsResponse response) {
    return Single.just(
        response.getLabelsList().stream()
            .map(this::convertLabel)
            .collect(Collectors.toUnmodifiableList()));
  }

  Single<Label> convertLabel(CreateLabelResponse response) {
    return Single.just(convertLabel(response.getLabel()));
  }

  Single<Label> convertUpdateLabel(UpdateLabelResponse response) {
    return Single.just(convertLabel(response.getLabel()));
  }

  private Label convertLabel(org.hypertrace.label.config.service.v1.Label label) {
    return new DefaultLabel(
        label.getId(),
        label.getData().getKey(),
        label.getData().hasColor() ? label.getData().getColor() : null,
        label.getData().hasDescription() ? label.getData().getDescription() : null,
        Collections.emptyMap());
  }

  public Single<Label> convertLabel(Label label, Map<String, EntityResultSet> entityResultMap) {
    Label convertedLabel =
        new DefaultLabel(
            label.id(),
            label.key(),
            label.color(),
            label.description(),
            getLabeledEntitiesMap(entityResultMap));
    return Single.just(convertedLabel);
  }

  private Map<String, List<LabeledEntity>> getLabeledEntitiesMap(
      Map<String, EntityResultSet> entityResultMap) {
    return entityResultMap.entrySet().stream()
        .map(entry -> Map.entry(entry.getKey(), convertEntities(entry.getValue())))
        .collect(CollectorUtils.immutableMapEntryCollector());
  }

  private List<LabeledEntity> convertEntities(EntityResultSet entityResultSet) {
    return entityResultSet.results().stream()
        .map(DefaultLabeledEntity::new)
        .collect(Collectors.toList());
  }

  @Value
  @Accessors(fluent = true)
  private static class DefaultLabelResultSet implements LabelResultSet {
    List<Label> results;
    long count;
    long total;
  }

  @Value
  @Accessors(fluent = true)
  private static class DefaultLabel implements Label {
    String id;
    String key;
    String color;
    String description;
    Map<String, List<LabeledEntity>> labeledEntitiesMap;

    @Override
    public LabeledEntityResultSet labeledEntities(String entityType, int limit) {
      List<LabeledEntity> labeledEntities =
          Optional.ofNullable(labeledEntitiesMap.get(entityType)).orElse(Collections.emptyList());
      List<LabeledEntity> labeledEntitiesWithLimit =
          labeledEntities.size() > limit ? labeledEntities.subList(0, limit) : labeledEntities;
      return new DefaultLabeledEntityResultSet(
          labeledEntitiesWithLimit, labeledEntitiesWithLimit.size(), labeledEntities.size());
    }
  }

  @Value
  @Accessors(fluent = true)
  private static class DefaultLabeledEntityResultSet implements LabeledEntityResultSet {
    List<LabeledEntity> results;
    long count;
    long total;
  }

  @Value
  @Accessors(fluent = true)
  private static class DefaultLabeledEntity implements LabeledEntity {
    Entity entity;

    @Override
    public String id() {
      return entity.id();
    }

    @Override
    public Object attribute(String key) {
      return this.entity.attribute(key);
    }
  }
}
