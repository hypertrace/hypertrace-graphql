package org.hypertrace.core.graphql.common.utils.attributes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.reactivex.rxjava3.core.Single;
import org.hypertrace.core.graphql.attributes.AttributeModelType;
import org.hypertrace.core.graphql.common.schema.attributes.AttributeType;
import org.junit.jupiter.api.Test;

public class AttributeTypeConverterTest {
  @Test
  void testConvert() {
    AttributeTypeConverter attributeTypeConverter = new AttributeTypeConverter();

    assertEquals(AttributeType.STRING,
        attributeTypeConverter.convert(AttributeModelType.STRING).blockingGet());
    assertEquals(AttributeType.BOOLEAN,
        attributeTypeConverter.convert(AttributeModelType.BOOLEAN).blockingGet());
    assertEquals(AttributeType.LONG,
        attributeTypeConverter.convert(AttributeModelType.LONG).blockingGet());
    assertEquals(AttributeType.DOUBLE,
        attributeTypeConverter.convert(AttributeModelType.DOUBLE).blockingGet());
    assertEquals(AttributeType.TIMESTAMP,
        attributeTypeConverter.convert(AttributeModelType.TIMESTAMP).blockingGet());
    assertEquals(AttributeType.STRING_MAP,
        attributeTypeConverter.convert(AttributeModelType.STRING_MAP).blockingGet());
    assertEquals(AttributeType.STRING_ARRAY,
        attributeTypeConverter.convert(AttributeModelType.STRING_ARRAY).blockingGet());
  }
}
