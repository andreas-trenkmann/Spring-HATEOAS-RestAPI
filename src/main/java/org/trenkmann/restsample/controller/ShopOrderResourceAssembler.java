package org.trenkmann.restsample.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.hateoas.Resource;
import org.springframework.stereotype.Component;
import org.trenkmann.restsample.model.ShopOrder;
import org.trenkmann.restsample.model.ShopOrderStatus;

/**
 * @author andreas trenkmann
 */
@Component
public class ShopOrderResourceAssembler {

  public Resource<ShopOrder> toResource(ShopOrder shopOrder) {

    Resource<ShopOrder> resource =
        new Resource<>(
            shopOrder,
            linkTo(methodOn(ShopOrderController.class).getOrderById(shopOrder.getId()))
                .withSelfRel(),
            linkTo(methodOn(ShopcartController.class).getCartById(shopOrder.getShopCart().getId()))
                .withRel("cart"),
            linkTo(methodOn(ShopOrderController.class).getAllOrders()).withRel("orders"));

    if (shopOrder.getStatus() == ShopOrderStatus.IN_PROGRESS) {
      resource.add(
          linkTo(methodOn(ShopOrderController.class).cancelOrder(shopOrder.getId()))
              .withRel("cancel"));
      resource.add(
          linkTo(methodOn(ShopOrderController.class).completeOrder(shopOrder.getId()))
              .withRel("complete"));
    }
    return resource;
  }
}
