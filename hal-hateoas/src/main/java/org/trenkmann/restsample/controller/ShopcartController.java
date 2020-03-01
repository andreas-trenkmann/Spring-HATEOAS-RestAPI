package org.trenkmann.restsample.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.trenkmann.restsample.data.MP3Repository;
import org.trenkmann.restsample.data.ShopCartElementRepository;
import org.trenkmann.restsample.data.ShopCartRepository;
import org.trenkmann.restsample.exception.MP3CanNotFoundException;
import org.trenkmann.restsample.exception.ShopCartNoElementFoundException;
import org.trenkmann.restsample.exception.ShopNoCartFoundException;
import org.trenkmann.restsample.model.MP3;
import org.trenkmann.restsample.model.ShopCart;
import org.trenkmann.restsample.model.ShopCartElement;
import org.trenkmann.restsample.model.dto.CartOrderElementDTO;
import org.trenkmann.restsample.service.ShopCartService;

/**
 * @author andreas trenkmann
 */
@RestController
public class ShopcartController {

  private final ShopCartRepository shopCartRepository;
  private final ShopCartService shopCartService;
  private final ShopCartElementRepository shopCartElementRepository;
  private final MP3Repository mp3Repository;
  private final ShopCartResourceAssembler shopCartResourceAssembler;
  private final ShopCartElementResourceAssembler shopCartElementResourceAssembler;

  public ShopcartController(ShopCartService shopCartService,
      ShopCartRepository shopCartRepository, ShopCartElementRepository shopCartElementRepository,
      MP3Repository mp3Repository, ShopCartResourceAssembler shopCartResourceAssembler,
      ShopCartElementResourceAssembler shopCartElementResourceAssembler) {
    this.shopCartElementRepository = shopCartElementRepository;
    this.shopCartRepository = shopCartRepository;
    this.shopCartService = shopCartService;
    this.mp3Repository = mp3Repository;
    this.shopCartResourceAssembler = shopCartResourceAssembler;
    this.shopCartElementResourceAssembler = shopCartElementResourceAssembler;
  }

  @Operation(summary = "collects all existing carts from storage", security = @SecurityRequirement(name = "basicScheme"))
  @GetMapping(path = "/carts")
  public CollectionModel<EntityModel<ShopCart>> getCarts() {
    return shopCartResourceAssembler.toCollectionModel(shopCartRepository.findAll());
  }

  @Operation(summary = "get a single cart out of the storage", security = @SecurityRequirement(name = "basicScheme"))
  @GetMapping(path = "/cart/{cartId}")
  public EntityModel<ShopCart> getCartById(@PathVariable Long cartId) {
    return shopCartResourceAssembler.toModel(
        Optional.ofNullable(shopCartRepository.findOneById(cartId)).orElseThrow(()
            -> new ShopNoCartFoundException(cartId)));
  }

  @Operation(summary = "delete a single cart out of the storage", security = @SecurityRequirement(name = "basicScheme"))
  @DeleteMapping(path = "/cart/{cartId}")
  public ResponseEntity<RepresentationModel> deleteCartById(@PathVariable Long cartId) {
    ShopCart shopCart = Optional.ofNullable(shopCartRepository.findOneById(cartId)).orElseThrow(()
        -> new ShopNoCartFoundException(cartId));
    shopCartRepository.delete(shopCart);

    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "get all elements from a certain cart from storage", security = @SecurityRequirement(name = "basicScheme"))
  @GetMapping(path = "/cart/{cartId}/elements")
  public CollectionModel<EntityModel<ShopCartElement>> getCartElementsByCartId(
      @PathVariable Long cartId) {
    ShopCart shopCart = Optional.ofNullable(shopCartRepository.findOneById(cartId)).orElseThrow(()
        -> new ShopNoCartFoundException(cartId));
    return shopCartElementResourceAssembler.toCollectionModel(shopCart.getCartElementSet());
  }

  @Operation(summary = "get a single element from a certain cart from storage", security = @SecurityRequirement(name = "basicScheme"))
  @GetMapping(path = "/cart/{cartId}/element/{elementId}")
  public EntityModel<ShopCartElement> getCartElementByCartIdAndElementId(@PathVariable Long cartId,
      @PathVariable Long elementId) {
    ShopCart shopCart = Optional.ofNullable(shopCartRepository.findOneById(cartId)).orElseThrow(()
        -> new ShopNoCartFoundException(cartId));

    ShopCartElement shopCartElement = shopCart.getCartElementSet().stream()
        .filter(element -> element.getId().equals(elementId)).findFirst()
        .orElseThrow(() -> new ShopCartNoElementFoundException(elementId));

    return shopCartElementResourceAssembler.toModel(shopCartElement);
  }

  @Operation(summary = "delete a single element from a certain cart from storage", security = @SecurityRequirement(name = "basicScheme"))
  @DeleteMapping(path = "/cart/{cartId}/element/{elementId}")
  public ResponseEntity<RepresentationModel> deleteCartElementByCartIdAndElementId(
      @PathVariable Long cartId, @PathVariable Long elementId) {
    ShopCart shopCart = Optional.ofNullable(shopCartRepository.findOneById(cartId)).orElseThrow(()
        -> new ShopNoCartFoundException(cartId));

    ShopCartElement shopCartElement = shopCart.getCartElementSet().stream()
        .filter(element -> element.getId().equals(elementId)).findFirst()
        .orElseThrow(() -> new ShopCartNoElementFoundException(elementId));

    shopCartElementRepository.delete(shopCartElement);
    return ResponseEntity.noContent().build();
  }

  /**
   * @see <a href="http://restcookbook.com/HTTP%20Methods/put-vs-post/" >PUT vs POST</a>
   */
  @Operation(summary = "create a single element from a certain cart to storage", security = @SecurityRequirement(name = "basicScheme"))
  @PostMapping(path = "/cart/{id}/elements")
  public ResponseEntity<EntityModel<ShopCartElement>> addElementToCart(@PathVariable Long id,
      @RequestBody CartOrderElementDTO orderElementDTO)
      throws URISyntaxException {

    ShopCart shopCart = Optional.ofNullable(shopCartService.getShopCart(id))
        .orElseThrow(() -> new ShopNoCartFoundException(id));

    MP3 mp3 = mp3Repository.findById(orderElementDTO.getMp3id()).orElseThrow(
        () -> new MP3CanNotFoundException(orderElementDTO.getMp3id()));
    ShopCartElement element = shopCartService.addElementToShopCart(shopCart, mp3);
    EntityModel<ShopCartElement> shopCartRes = shopCartElementResourceAssembler.toModel(element);

    return ResponseEntity.created(
        new URI(shopCartRes.getRequiredLink(IanaLinkRelations.SELF).expand().getHref()))
        .body(shopCartRes);
  }

  /**
   * @see <a href="http://restcookbook.com/HTTP%20Methods/put-vs-post/" >PUT vs POST</a>
   */
  @Operation(summary = "create or alter a single element from a certain cart from storage", security = @SecurityRequirement(name = "basicScheme"))
  @PutMapping(path = "/cart/{id}/element/{elementId}")
  public ResponseEntity<EntityModel<ShopCartElement>> addElementToExistingCart(
      @PathVariable Long id,
      @PathVariable Long elementId, @RequestBody CartOrderElementDTO orderElementDTO)
      throws URISyntaxException {

    ShopCart shopCart = Optional.ofNullable(shopCartRepository.findOneById(id))
        .orElseGet(() -> shopCartService.getShopCart(id));

    MP3 mp3 = mp3Repository.findById(orderElementDTO.getMp3id()).orElseThrow(
        () -> new MP3CanNotFoundException(orderElementDTO.getMp3id()));

    Optional<ShopCartElement> shopCartElementOpt = shopCart.getCartElementSet().stream()
        .filter(element -> element.getId().equals(elementId)).findFirst();

    //if object exist alter it and send 200 // OK
    if (shopCartElementOpt.isPresent()) {
      ShopCartElement element = shopCartElementOpt.get();
      return ResponseEntity.ok(shopCartElementResourceAssembler
          .toModel(shopCartService.alterElementInShopCart(mp3, element)));
    } else {

      //if object does not exist create it and send 201
      ShopCartElement element = shopCartService.addElementToShopCart(shopCart, mp3);
      EntityModel<ShopCartElement> shopCartRes = shopCartElementResourceAssembler.toModel(element);

      return ResponseEntity
          .created(new URI(shopCartRes.getRequiredLink(IanaLinkRelations.SELF).expand().getHref()))
          .body(shopCartRes);
    }
  }

  // NO HAL just for preview purposes with inherit projection
  @Operation(summary = "No HAL-Response for comparison", security = @SecurityRequirement(name = "basicScheme"))
  @GetMapping(path = "/plainCart/{id}")
  public ShopCart getCartByIdPlain(@PathVariable Long id) {
    return shopCartRepository.findOneById(id);
  }
}
