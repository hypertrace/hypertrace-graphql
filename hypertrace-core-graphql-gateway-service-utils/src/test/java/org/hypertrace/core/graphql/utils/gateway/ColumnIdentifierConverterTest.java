package org.hypertrace.core.graphql.utils.gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.hypertrace.core.graphql.attributes.AttributeModel;
import org.hypertrace.gateway.service.v1.common.ColumnIdentifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ColumnIdentifierConverterTest {
  @Mock AttributeModel mockAttribute;

  @Test
  void convertsColumnBasedOnAttributeId() {
    when(mockAttribute.id()).thenReturn("expectedIdentifier");

    assertEquals(
        ColumnIdentifier.newBuilder().setColumnName("expectedIdentifier").build(),
        new ColumnIdentifierConverter().convert(this.mockAttribute).blockingGet());
  }
}
