package org.trenkmann.restsample.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import org.trenkmann.restsample.model.ShopOrder;
import org.trenkmann.restsample.model.ShopOrderStatus;

/**
 * @author andreas trenkmann
 */
@Component
public class ShopOrderResourceAssembler implements
    RepresentationModelAssembler<ShopOrder, EntityModel<ShopOrder>> {


  @Override
  public EntityModel<ShopOrder> toModel(ShopOrder shopOrder) {
    EntityModel<ShopOrder> resource =
        new EntityModel<>(
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

  @Override
  public CollectionModel<EntityModel<ShopOrder>> toCollectionModel(
      Iterable<? extends ShopOrder> entities) {
    List<EntityModel<ShopOrder>> listOfOrders = StreamSupport.stream(entities.spliterator(), false)
        .map(this::toModel).collect(
            Collectors.toList());

    return new CollectionModel<>(listOfOrders,
        linkTo(methodOn(ShopOrderController.class).getAllOrders()).withSelfRel());
  }
}
