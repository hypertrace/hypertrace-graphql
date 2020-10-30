package org.hypertrace.core.graphql.attributes;

import io.reactivex.rxjava3.core.Observable;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;

public interface IdMappingLoader {
  Observable<IdMapping> loadMappings(GraphQlRequestContext requestContext);


}
