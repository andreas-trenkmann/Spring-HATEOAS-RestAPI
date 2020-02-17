package org.trenkmann.restsample.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;
import org.trenkmann.restsample.model.ShopCartElement;

/**
 * @author andreas trenkmann
 */
@Component
public class ShopCartElementResourceAssembler implements ResourceAssembler<ShopCartElement, Resource<ShopCartElement>> {

  @Override
  public Resource<ShopCartElement> toResource(ShopCartElement shopCartElement) {
    return new Resource<>(shopCartElement,
        linkTo(methodOn(ShopcartController.class)
            .getCartElementsByCartId(shopCartElement.getShopCart().getId()))
            .withRel("elementsInCart"),
        linkTo(methodOn(MP3Controller.class).getMP3ById(shopCartElement.getMp3().getId()))
            .withRel("mp3"),
        linkTo(methodOn(ShopcartController.class)
            .getCartElementByCartIdAndElementId(shopCartElement.getShopCart().getId(),
                shopCartElement.getId())).withSelfRel());
  }

}
