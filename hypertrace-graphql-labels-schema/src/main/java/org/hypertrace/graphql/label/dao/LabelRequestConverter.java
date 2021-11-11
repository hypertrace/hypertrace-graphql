package org.hypertrace.graphql.label.dao;

import org.hypertrace.graphql.label.request.LabelCreateRequest;
import org.hypertrace.graphql.label.request.LabelUpdateRequest;
import org.hypertrace.label.config.service.v1.CreateLabelRequest;
import org.hypertrace.label.config.service.v1.LabelData;
import org.hypertrace.label.config.service.v1.UpdateLabelRequest;

public class LabelRequestConverter {
  CreateLabelRequest convertCreationRequest(LabelCreateRequest creationRequest) {
    return CreateLabelRequest.newBuilder()
        .setData(LabelData.newBuilder().setKey(creationRequest.label().key()).build())
        .build();
  }

  UpdateLabelRequest convertUpdateRequest(LabelUpdateRequest updateRequest) {
    return UpdateLabelRequest.newBuilder()
        .setId(updateRequest.label().id())
        .setData(LabelData.newBuilder().setKey(updateRequest.label().key()).build())
        .build();
  }
}
