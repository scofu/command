package com.scofu.command;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/** A forwarding annotated element. */
public interface ForwardingAnnotatedElement extends AnnotatedElement {

  /** Returns the forwarded annotated element. */
  AnnotatedElement annotatedElement();

  @Override
  default <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
    return annotatedElement().getAnnotation(annotationClass);
  }

  @Override
  default Annotation[] getAnnotations() {
    return annotatedElement().getAnnotations();
  }

  @Override
  default Annotation[] getDeclaredAnnotations() {
    return annotatedElement().getDeclaredAnnotations();
  }
}
