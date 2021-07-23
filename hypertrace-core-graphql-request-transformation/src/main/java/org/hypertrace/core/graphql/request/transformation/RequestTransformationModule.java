package org.hypertrace.core.graphql.request.transformation;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

public class RequestTransformationModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(RequestTransformer.class).to(RequestTransformerImpl.class);
    Multibinder.newSetBinder(binder(), RequestTransformation.class);
  }
}
