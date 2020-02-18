package org.trenkmann.restsample.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.SneakyThrows;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import org.trenkmann.restsample.model.ShopCart;
import org.trenkmann.restsample.model.dto.ShopOrderDTO;

/**
 * @author andreas trenkmann
 */
@Component
public class ShopCartResourceAssembler implements
    RepresentationModelAssembler<ShopCart, EntityModel<ShopCart>> {

  @SneakyThrows
  @Override
  public EntityModel<ShopCart> toModel(ShopCart cart) {
    return new EntityModel<>(cart,
        linkTo(methodOn(ShopcartController.class).getCartById(cart.getId())).withSelfRel(),
        linkTo(methodOn(ShopcartController.class).getCartElementsByCartId(cart.getId()))
            .withRel("elementInCart"),
        linkTo(methodOn(ShopOrderController.class).newShopOrder(new ShopOrderDTO()))
            .withRel("order"),
        linkTo(methodOn(ShopcartController.class).getCarts()).withRel("carts"));
  }

  @Override
  public CollectionModel<EntityModel<ShopCart>> toCollectionModel(
      Iterable<? extends ShopCart> entities) {
    List<EntityModel<ShopCart>> list = StreamSupport.stream(entities.spliterator(), false)
        .map(this::toModel)
        .collect(Collectors.toList());

    return new CollectionModel<>(list,
        linkTo(methodOn(ShopcartController.class).getCarts()).withSelfRel());
  }

}
