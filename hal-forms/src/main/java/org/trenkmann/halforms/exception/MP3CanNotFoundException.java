package org.trenkmann.halforms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MP3CanNotFoundException extends GeneralRestException {

  public MP3CanNotFoundException(Long id) {
    super("Can not found MP3 with id " + id);
  }

}
