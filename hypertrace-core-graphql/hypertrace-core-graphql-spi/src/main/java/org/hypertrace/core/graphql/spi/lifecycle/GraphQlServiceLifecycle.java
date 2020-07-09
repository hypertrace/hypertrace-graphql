package org.hypertrace.core.graphql.spi.lifecycle;

import java.util.concurrent.CompletionStage;

public interface GraphQlServiceLifecycle {

  CompletionStage<Void> shutdownCompletion();
}
