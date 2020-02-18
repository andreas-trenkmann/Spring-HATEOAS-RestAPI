package org.trenkmann.restsample.exception;

import static org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class GeneralRestException extends RuntimeException {

  public GeneralRestException() {
  }

  public GeneralRestException(String message) {
    super(message);
  }

  public GeneralRestException(String message, Throwable cause) {
    super(message, cause);
  }

  public GeneralRestException(Throwable cause) {
    super(cause);
  }

  public GeneralRestException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public HttpStatus getResponseStatus() {
    ResponseStatus responseStatus = findMergedAnnotation(getClass(), ResponseStatus.class);
    return responseStatus == null ? HttpStatus.INTERNAL_SERVER_ERROR : responseStatus.value();
  }

}
