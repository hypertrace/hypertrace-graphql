package org.hypertrace.core.graphql.common.request;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.attributes.AttributeStore;
import org.hypertrace.core.graphql.common.schema.attributes.AttributeScope;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterArgument;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterOperatorType;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterType;
import org.hypertrace.core.graphql.common.utils.attributes.AttributeAssociator;
import org.hypertrace.core.graphql.common.utils.attributes.AttributeScopeStringTranslator;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;

public class DefaultFilterRequestBuilder implements FilterRequestBuilder {

  private final AttributeAssociator attributeAssociator;
  private final AttributeStore attributeStore;
  private final AttributeScopeStringTranslator scopeTranslator;

  @Inject
  DefaultFilterRequestBuilder(
      AttributeAssociator attributeAssociator,
      AttributeStore attributeStore,
      AttributeScopeStringTranslator scopeTranslator) {

    this.attributeAssociator = attributeAssociator;
    this.attributeStore = attributeStore;
    this.scopeTranslator = scopeTranslator;
  }

  @Override
  public Single<List<AttributeAssociation<FilterArgument>>> build(
      GraphQlRequestContext requestContext,
      String scope,
      Collection<FilterArgument> filterArguments) {
    return Observable.fromIterable(filterArguments)
        .flatMapSingle(filter -> this.normalize(requestContext, scope, filter))
        .collect(Collectors.toUnmodifiableList());
  }

  private Single<AttributeAssociation<FilterArgument>> normalize(
      GraphQlRequestContext requestContext, String scope, FilterArgument filterArgument) {
    switch (filterArgument.type()) {
      case STRING:
      case NUMERIC:
      case ATTRIBUTE:
        return this.attributeAssociator.associateAttribute(
            requestContext,
            scope,
            new NormalizedFilter(
                filterArgument.key(), filterArgument.operator(), filterArgument.value()),
            FilterArgument::key);
      case ID:
        return Maybe.fromOptional(Optional.ofNullable(filterArgument.idType()))
            .map(AttributeScope::getScopeString)
            .switchIfEmpty(Maybe.fromOptional(Optional.ofNullable(filterArgument.idScope())))
            .switchIfEmpty(
                Single.error(new UnsupportedOperationException("ID filter must specify idScope")))
            .map(this.scopeTranslator::fromExternal)
            .flatMap(
                foreignScope ->
                    this.attributeStore.getForeignIdAttribute(requestContext, scope, foreignScope))
            .map(
                foreignIdAttribute ->
                    AttributeAssociation.of(
                        foreignIdAttribute,
                        new NormalizedFilter(
                            foreignIdAttribute.key(),
                            filterArgument.operator(),
                            filterArgument.value())));
      default:
        return Single.error(
            new UnsupportedOperationException(
                "Unrecognized filter type: " + filterArgument.type()));
    }
  }

  @Value
  @Accessors(fluent = true)
  private static class NormalizedFilter implements FilterArgument {
    FilterType type = FilterType.ATTRIBUTE;
    String key;
    FilterOperatorType operator;
    Object value;
    String idScope = null;
    AttributeScope idType = null;
  }
}
