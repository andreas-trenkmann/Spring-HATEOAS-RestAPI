package org.trenkmann.restsample.model;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "entireCartContent", types = {ShopCart.class})
public interface EntireCartContentProjection {

  @Value("#{target.id}")
  long getId();

  int getElementCounter();

  List<ShopCartElement> getCartElementSet();
}
