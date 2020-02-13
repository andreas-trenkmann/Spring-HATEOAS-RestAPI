package org.trenkmann.restsample.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ShopCartNoElementsFoundException extends GeneralRestException {

  public ShopCartNoElementsFoundException(Long id) {
    super("No Elements in Shopping Cart found with id "+id);
  }

  public ShopCartNoElementsFoundException() {
    super("no Element found in Shopping Cart");
  }
}
