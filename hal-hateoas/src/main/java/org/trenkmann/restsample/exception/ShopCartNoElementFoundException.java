package org.trenkmann.restsample.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author andreas trenkmann
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ShopCartNoElementFoundException extends GeneralRestException {

  public ShopCartNoElementFoundException(Long elementId) {
    super("can not find ShopCart Element with ID " + elementId);
  }
}
