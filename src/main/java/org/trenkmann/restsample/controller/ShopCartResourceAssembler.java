package org.trenkmann.restsample.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;
import org.trenkmann.restsample.model.ShopCart;

/**
 * @author andreas trenkmann
 */
@Component
public class ShopCartResourceAssembler implements ResourceAssembler<ShopCart, Resource<ShopCart>> {

  @Override
  public Resource<ShopCart> toResource(ShopCart cart) {
      return new Resource<>( cart,
          linkTo(methodOn(ShopOrderController.class).getCartById(cart.getId())).withSelfRel(),
          linkTo(methodOn(ShopOrderController.class).getCartElementsByCartId(cart.getId())).withRel("elementInCart"),
          linkTo(methodOn(ShopOrderController.class).getCarts()).withRel("carts"));
    }

}
