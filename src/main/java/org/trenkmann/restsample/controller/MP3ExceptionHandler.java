package org.trenkmann.restsample.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.trenkmann.restsample.exception.GeneralRestException;

@Slf4j
@ControllerAdvice
class MP3ExceptionHandler extends ResponseEntityExceptionHandler {

  @ResponseBody
  @ExceptionHandler(GeneralRestException.class)
  public ResponseEntity<String> mp3NotFoundHandler(GeneralRestException ex){
    log.error("MP3Exception caught", ex);
    return  ResponseEntity.status(ex.getResponseStatus()).body(ex.getLocalizedMessage());
  }
}
