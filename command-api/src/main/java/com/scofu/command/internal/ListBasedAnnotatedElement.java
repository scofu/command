package com.scofu.command.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A list based annotated element.
 */
public class ListBasedAnnotatedElement implements AnnotatedElement {

  private final Map<Class<?>, Annotation> annotations;

  public ListBasedAnnotatedElement(List<Annotation> annotations) {
    this.annotations = annotations.stream()
        .collect(Collectors.toMap(Annotation::annotationType, Function.identity()));
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
    final var annotation = annotations.get(annotationClass);
    if (annotation == null) {
      return null;
    }
    return (T) annotation;
  }

  @Override
  public Annotation[] getAnnotations() {
    return annotations.values().toArray(new Annotation[]{});
  }

  @Override
  public Annotation[] getDeclaredAnnotations() {
    return getAnnotations();
  }
}
