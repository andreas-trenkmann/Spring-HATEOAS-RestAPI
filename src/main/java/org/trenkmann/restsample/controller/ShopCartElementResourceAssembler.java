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
import org.trenkmann.restsample.model.ShopCartElement;

/**
 * @author andreas trenkmann
 */
@Component
public class ShopCartElementResourceAssembler implements
    RepresentationModelAssembler<ShopCartElement, EntityModel<ShopCartElement>> {

  private Long lastId;

  @Override
  public EntityModel<ShopCartElement> toModel(ShopCartElement shopCartElement) {
    lastId = shopCartElement.getShopCart().getId();
    return new EntityModel<>(shopCartElement,
        linkTo(methodOn(ShopcartController.class)
            .getCartElementsByCartId(shopCartElement.getShopCart().getId()))
            .withRel("elementsInCart"),
        linkTo(methodOn(MP3Controller.class).getMP3ById(shopCartElement.getMp3().getId()))
            .withRel("mp3"),
        linkTo(methodOn(ShopcartController.class)
            .getCartElementByCartIdAndElementId(shopCartElement.getShopCart().getId(),
                shopCartElement.getId())).withSelfRel());
  }

  @Override
  public CollectionModel<EntityModel<ShopCartElement>> toCollectionModel(
      Iterable<? extends ShopCartElement> entities) {

    List<EntityModel<ShopCartElement>> list = StreamSupport.stream(entities.spliterator(), false)
        .map(this::toModel)
        .collect(Collectors.toList());

    return new CollectionModel<>(list,
        linkTo(methodOn(ShopcartController.class).getCartElementsByCartId(lastId)).withSelfRel(),
        linkTo(methodOn(ShopcartController.class).getCarts()).withRel("carts"));
  }
}
