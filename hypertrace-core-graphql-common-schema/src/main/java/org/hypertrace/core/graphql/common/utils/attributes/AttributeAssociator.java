package org.hypertrace.core.graphql.common.utils.attributes;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.Collection;
import java.util.function.Function;
import org.hypertrace.core.graphql.common.request.AttributeAssociation;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;

public interface AttributeAssociator {

  <T> Observable<AttributeAssociation<T>> associateAttributes(
      GraphQlRequestContext context,
      String requestScope,
      Collection<T> inputs,
      Function<T, String> attributeKeyMapper);

  <T> Single<AttributeAssociation<T>> associateAttribute(
      GraphQlRequestContext context, String requestScope, T input, String attributeKey);
}
