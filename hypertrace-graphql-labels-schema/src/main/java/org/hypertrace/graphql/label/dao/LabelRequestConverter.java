package org.hypertrace.graphql.label.dao;

import java.util.Objects;
import org.hypertrace.graphql.label.request.LabelCreateRequest;
import org.hypertrace.graphql.label.request.LabelUpdateRequest;
import org.hypertrace.label.config.service.v1.CreateLabelRequest;
import org.hypertrace.label.config.service.v1.LabelData;
import org.hypertrace.label.config.service.v1.UpdateLabelRequest;

public class LabelRequestConverter {
  CreateLabelRequest convertCreationRequest(LabelCreateRequest creationRequest) {
    LabelData.Builder dataBuilder = LabelData.newBuilder().setKey(creationRequest.label().key());
    if (Objects.nonNull(creationRequest.label().color())) {
      dataBuilder.setColor(Objects.requireNonNull(creationRequest.label().color()));
    }
    if (Objects.nonNull(creationRequest.label().description())) {
      dataBuilder.setDescription(Objects.requireNonNull(creationRequest.label().description()));
    }
    return CreateLabelRequest.newBuilder().setData(dataBuilder.build()).build();
  }

  UpdateLabelRequest convertUpdateRequest(LabelUpdateRequest updateRequest) {
    LabelData.Builder dataBuilder = LabelData.newBuilder().setKey(updateRequest.label().key());
    if (Objects.nonNull(updateRequest.label().color())) {
      dataBuilder.setColor(Objects.requireNonNull(updateRequest.label().color()));
    }
    if (Objects.nonNull(updateRequest.label().description())) {
      dataBuilder.setDescription(Objects.requireNonNull(updateRequest.label().description()));
    }
    return UpdateLabelRequest.newBuilder()
        .setId(updateRequest.label().id())
        .setData(dataBuilder.build())
        .build();
  }
}
