package org.hypertrace.graphql.label.dao;

import java.util.Objects;
import org.hypertrace.graphql.label.request.LabelCreateRequest;
import org.hypertrace.graphql.label.request.LabelUpdateRequest;
import org.hypertrace.label.config.service.v1.CreateLabelRequest;
import org.hypertrace.label.config.service.v1.LabelData;
import org.hypertrace.label.config.service.v1.UpdateLabelRequest;

public class LabelRequestConverter {
  CreateLabelRequest convertCreationRequest(LabelCreateRequest creationRequest) {
    return CreateLabelRequest.newBuilder()
        .setData(convertLabelData(creationRequest.label().data()))
        .build();
  }

  UpdateLabelRequest convertUpdateRequest(LabelUpdateRequest updateRequest) {
    return UpdateLabelRequest.newBuilder()
        .setId(updateRequest.label().id())
        .setData(convertLabelData(updateRequest.label().data()))
        .build();
  }

  LabelData convertLabelData(org.hypertrace.graphql.label.schema.shared.LabelData data) {
    LabelData.Builder convertedDataBuilder = LabelData.newBuilder().setKey(data.key());
    if (Objects.nonNull(data.color())) {
      convertedDataBuilder.setColor(Objects.requireNonNull(data.color()));
    }
    if (Objects.nonNull(data.description())) {
      convertedDataBuilder.setDescription(Objects.requireNonNull(data.description()));
    }
    return convertedDataBuilder.build();
  }
}
