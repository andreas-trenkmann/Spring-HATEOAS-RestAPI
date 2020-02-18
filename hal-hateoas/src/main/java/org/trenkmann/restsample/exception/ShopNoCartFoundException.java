package org.trenkmann.restsample.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author andreas trenkmann
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ShopNoCartFoundException extends GeneralRestException {

  public ShopNoCartFoundException(Long elementId) {
    super("can not find ShopCart with ID " + elementId);
  }
}
