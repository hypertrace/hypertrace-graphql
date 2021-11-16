package org.hypertrace.graphql.label.dao;

import io.reactivex.rxjava3.core.Single;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.graphql.label.schema.Label;
import org.hypertrace.graphql.label.schema.LabelResultSet;
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
            .map(
                label ->
                    new DefaultLabel(
                        label.getId(),
                        label.getData().getKey(),
                        label.getData().hasColor() ? label.getData().getColor() : null,
                        label.getData().hasDescription() ? label.getData().getDescription() : null))
            .collect(Collectors.toUnmodifiableList()));
  }

  Single<Label> convertLabel(CreateLabelResponse response) {
    return Single.just(
        new DefaultLabel(
            response.getLabel().getId(),
            response.getLabel().getData().getKey(),
            response.getLabel().getData().hasColor()
                ? response.getLabel().getData().getColor()
                : null,
            response.getLabel().getData().hasDescription()
                ? response.getLabel().getData().getDescription()
                : null));
  }

  Single<Label> convertUpdateLabel(UpdateLabelResponse response) {
    return Single.just(
        new DefaultLabel(
            response.getLabel().getId(),
            response.getLabel().getData().getKey(),
            response.getLabel().getData().hasColor()
                ? response.getLabel().getData().getColor()
                : null,
            response.getLabel().getData().hasDescription()
                ? response.getLabel().getData().getDescription()
                : null));
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
  }
}
