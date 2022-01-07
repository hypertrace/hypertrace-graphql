package org.hypertrace.core.graphql.utils.gateway;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import javax.inject.Inject;
import org.hypertrace.core.graphql.attributes.AttributeModel;
import org.hypertrace.core.graphql.common.request.AttributeRequest;
import org.hypertrace.core.graphql.common.schema.attributes.arguments.AttributeExpression;
import org.hypertrace.core.graphql.common.utils.BiConverter;
import org.hypertrace.core.graphql.common.utils.CollectorUtils;
import org.hypertrace.gateway.service.v1.common.Value;

class AttributeMapConverter
    implements BiConverter<
        Collection<AttributeRequest>, Map<String, Value>, Map<AttributeExpression, Object>> {

  private final BiConverter<Value, AttributeModel, Object> valueConverter;

  @Inject
  AttributeMapConverter(BiConverter<Value, AttributeModel, Object> valueConverter) {
    this.valueConverter = valueConverter;
  }

  @Override
  public Single<Map<AttributeExpression, Object>> convert(
      Collection<AttributeRequest> attributes, Map<String, Value> response) {
    return Observable.fromIterable(attributes)
        .filter(attribute -> !Value.getDefaultInstance().equals(response.get(attribute.asMapKey())))
        .flatMapSingle(attribute -> this.buildAttributeMapEntry(attribute, response))
        .distinct()
        .collect(CollectorUtils.immutableMapEntryCollector());
  }

  private Single<Entry<AttributeExpression, Object>> buildAttributeMapEntry(
      AttributeRequest attributeRequest, Map<String, Value> response) {
    // Uses SimpleImmutableEntry to support null values
    return this.valueConverter
        .convert(
            response.get(attributeRequest.asMapKey()),
            attributeRequest.attributeExpression().attribute())
        .map(
            value ->
                new SimpleImmutableEntry<>(attributeRequest.attributeExpression().value(), value));
  }
}
