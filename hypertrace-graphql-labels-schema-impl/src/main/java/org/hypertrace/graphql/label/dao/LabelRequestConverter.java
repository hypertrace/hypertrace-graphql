package org.hypertrace.graphql.label.dao;

import java.util.Optional;
import org.hypertrace.graphql.label.request.LabelCreateRequest;
import org.hypertrace.graphql.label.request.LabelUpdateRequest;
import org.hypertrace.label.config.service.v1.CreateLabelRequest;
import org.hypertrace.label.config.service.v1.LabelData;
import org.hypertrace.label.config.service.v1.UpdateLabelRequest;

public class LabelRequestConverter {
  CreateLabelRequest convertCreationRequest(LabelCreateRequest creationRequest) {
    return CreateLabelRequest.newBuilder()
        .setData(convertLabelData(creationRequest.label()))
        .build();
  }

  UpdateLabelRequest convertUpdateRequest(LabelUpdateRequest updateRequest) {
    return UpdateLabelRequest.newBuilder()
        .setId(updateRequest.label().id())
        .setData(convertLabelData(updateRequest.label()))
        .build();
  }

  private LabelData convertLabelData(org.hypertrace.graphql.label.schema.LabelData data) {
    LabelData.Builder dataBuilder = LabelData.newBuilder().setKey(data.key());
    Optional.ofNullable(data.color()).ifPresent(dataBuilder::setColor);
    Optional.ofNullable(data.description()).ifPresent(dataBuilder::setDescription);
    return dataBuilder.build();
  }
}
