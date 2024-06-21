package org.hypertrace.core.graphql.common.schema.typefunctions;

import graphql.annotations.processor.ProcessingElementsContainer;
import graphql.annotations.processor.retrievers.GraphQLObjectInfoRetriever;
import graphql.annotations.processor.typeBuilders.EnumBuilder;
import graphql.annotations.processor.typeFunctions.TypeFunction;
import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLType;
import java.lang.reflect.AnnotatedType;
import javax.inject.Inject;
import org.hypertrace.core.graphql.common.schema.attributes.AttributeScope;

public class AttributeScopeDynamicEnum implements TypeFunction {

  private final GraphQLEnumType enumType;

  @Inject
  AttributeScopeDynamicEnum(Class<? extends AttributeScope> attributeScopeImplementationClass) {
    this.enumType =
        new EnumBuilder(new GraphQLObjectInfoRetriever())
            .getEnumBuilder(attributeScopeImplementationClass)
            .name(AttributeScope.TYPE_NAME)
            .build();
  }

  @Override
  public boolean canBuildType(Class<?> aClass, AnnotatedType annotatedType) {
    return aClass == AttributeScope.class;
  }

  @Override
  public GraphQLType buildType(
      boolean input,
      Class<?> aClass,
      AnnotatedType annotatedType,
      ProcessingElementsContainer container) {
    return this.enumType;
  }
}
