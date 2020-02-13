package org.trenkmann.restsample.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MP3CanNotDeleteException extends GeneralRestException {

  public MP3CanNotDeleteException(Long id) {
    super("Can not delete MP3 with id " + id);
  }

}
