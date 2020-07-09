package org.hypertrace.core.graphql.metadata.fetcher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import graphql.schema.DataFetchingEnvironment;
import io.reactivex.rxjava3.core.Single;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.hypertrace.core.graphql.attributes.AttributeModel;
import org.hypertrace.core.graphql.attributes.AttributeStore;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.metadata.fetcher.MetadataFetcher.MetadataFetcherImpl;
import org.hypertrace.core.graphql.metadata.response.MetadataResponseBuilder;
import org.hypertrace.core.graphql.metadata.schema.AttributeMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MetadataFetcherTest {

  @Mock MetadataResponseBuilder mockResponseBuilder;
  @Mock AttributeStore mockAttributeStore;
  @Mock DataFetchingEnvironment mockDataFetchingEnvironment;
  @Mock AttributeModel mockModel;
  @Mock AttributeMetadata mockMetadata;
  @Mock GraphQlRequestContext mockContext;
  private MetadataFetcherImpl fetcher;

  @BeforeEach
  void beforeEach() {
    List<AttributeModel> mockModelResult = List.of(mockModel);
    List<AttributeMetadata> mockMetadataResult = List.of(mockMetadata);
    when(this.mockDataFetchingEnvironment.getContext()).thenReturn(this.mockContext);
    when(this.mockAttributeStore.getAll(eq(this.mockContext)))
        .thenReturn(Single.just(mockModelResult));
    when(this.mockResponseBuilder.build(eq(mockModelResult)))
        .thenReturn(Single.just(mockMetadataResult));

    this.fetcher = new MetadataFetcherImpl(this.mockResponseBuilder, this.mockAttributeStore);
  }

  @Test
  void returnsResponseFuture() throws ExecutionException, InterruptedException {
    assertEquals(List.of(mockMetadata), this.fetcher.get(this.mockDataFetchingEnvironment).get());
  }
}
