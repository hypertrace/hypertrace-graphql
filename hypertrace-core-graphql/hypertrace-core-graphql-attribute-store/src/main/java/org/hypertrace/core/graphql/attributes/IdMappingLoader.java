package org.hypertrace.core.graphql.attributes;

import io.reactivex.rxjava3.core.Observable;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;

public interface IdMappingLoader {
  Observable<IdMapping> loadMappings(GraphQlRequestContext requestContext);
}
