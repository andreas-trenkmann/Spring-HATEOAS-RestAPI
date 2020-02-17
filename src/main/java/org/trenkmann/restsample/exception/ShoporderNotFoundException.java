package org.trenkmann.restsample.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author andreas trenkmann
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ShoporderNotFoundException extends GeneralRestException {

  public ShoporderNotFoundException(Long orderId) {
    super("Order not found with Id: " + orderId);
  }
}
