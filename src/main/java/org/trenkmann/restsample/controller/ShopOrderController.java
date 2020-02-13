package org.trenkmann.restsample.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
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
import org.trenkmann.restsample.exception.ShopCartNoElementFoundException;
import org.trenkmann.restsample.exception.MP3CanNotFoundException;
import org.trenkmann.restsample.exception.ShopNoCartFoundException;
import org.trenkmann.restsample.model.CartOrderElementDTO;
import org.trenkmann.restsample.model.MP3;
import org.trenkmann.restsample.model.ShopCart;
import org.trenkmann.restsample.model.ShopCartElement;
import org.trenkmann.restsample.service.ShopCartService;

/**
 *
 * @author andreas trenkmann
 */
@RestController
public class ShopOrderController {

  private final ShopCartRepository shopCartRepository;
  private final ShopCartService shopCartService;
  private final ShopCartElementRepository shopCartElementRepository;
  private final MP3Repository mp3Repository;
  private final ShopCartResourceAssembler shopCartResourceAssembler;
  private final ShopCartElementResourceAssembler shopCartElementResourceAssembler;

  public ShopOrderController(ShopCartService shopCartService,
      ShopCartRepository shopCartRepository, ShopCartElementRepository shopCartElementRepository,
      MP3Repository mp3Repository, ShopCartResourceAssembler shopCartResourceAssembler, ShopCartElementResourceAssembler shopCartElementResourceAssembler){
    this.shopCartElementRepository = shopCartElementRepository;
    this.shopCartRepository = shopCartRepository;
    this.shopCartService = shopCartService;
    this.mp3Repository = mp3Repository;
    this.shopCartResourceAssembler = shopCartResourceAssembler;
    this.shopCartElementResourceAssembler = shopCartElementResourceAssembler;
  }

  @GetMapping(path ="/carts")
  public Resources<Resource<ShopCart>> getCarts() {
    List<Resource<ShopCart>> list =  shopCartRepository.findAll().stream()
        .map( shopCartResourceAssembler::toResource)
              .collect(Collectors.toList());

    return new Resources<>(list,
        linkTo(methodOn(ShopOrderController.class).getCarts()).withSelfRel());
  }

  @GetMapping(path ="/cart/{cartId}")
  public Resource<ShopCart> getCartById(@PathVariable Long cartId){
    return shopCartResourceAssembler.toResource(
        Optional.ofNullable(shopCartRepository.findOneById(cartId)).orElseThrow(()
        -> new ShopNoCartFoundException(cartId)));
  }

  @DeleteMapping(path ="/cart/{cartId}")
  public ResponseEntity<?> deleteCartById(@PathVariable Long cartId){
    ShopCart shopCart =  Optional.ofNullable(shopCartRepository.findOneById(cartId)).orElseThrow(()
        -> new ShopNoCartFoundException(cartId));
    shopCartRepository.delete(shopCart);

    return ResponseEntity.noContent().build();
  }

  @GetMapping(path = "/cart/{cartId}/elements")
  public Resources<Resource<ShopCartElement>> getCartElementsByCartId(@PathVariable Long cartId){
    ShopCart shopCart = Optional.ofNullable(shopCartRepository.findOneById(cartId)).orElseThrow(()
        -> new ShopNoCartFoundException(cartId));
    List<Resource<ShopCartElement>> list = shopCart.getCartElementSet().stream()
        .map( shopCartElementResourceAssembler::toResource)
        .collect(Collectors.toList());

    return new Resources<>(list,
        linkTo(methodOn(ShopOrderController.class).getCartElementsByCartId(cartId)).withSelfRel(),
        linkTo(methodOn(ShopOrderController.class).getCarts()).withRel("carts"));
  }

  @GetMapping(path = "/cart/{cartId}/element/{elementId}")
  public Resource<ShopCartElement> getCartElementByCartIdAndElementId(@PathVariable Long cartId, @PathVariable Long elementId){
    ShopCart shopCart = Optional.ofNullable(shopCartRepository.findOneById(cartId)).orElseThrow(()
        -> new ShopNoCartFoundException(cartId));

    ShopCartElement shopCartElement = shopCart.getCartElementSet().stream()
        .filter( element -> element.getId() == elementId).findFirst()
        .orElseThrow( () -> new ShopCartNoElementFoundException(elementId));

    return shopCartElementResourceAssembler.toResource(shopCartElement);
  }

  @DeleteMapping(path = "/cart/{cartId}/element/{elementId}")
  public ResponseEntity<?> deleteCartElementByCartIdAndElementId(@PathVariable Long cartId, @PathVariable Long elementId){
    ShopCart shopCart = Optional.ofNullable(shopCartRepository.findOneById(cartId)).orElseThrow(()
        -> new ShopNoCartFoundException(cartId));

    ShopCartElement shopCartElement = shopCart.getCartElementSet().stream()
        .filter( element -> element.getId() == elementId).findFirst()
        .orElseThrow( () -> new ShopCartNoElementFoundException(elementId));

    shopCartElementRepository.delete(shopCartElement);
    return ResponseEntity.noContent().build();
  }

  /**
   * @see <a href="http://restcookbook.com/HTTP%20Methods/put-vs-post/" >PUT vs POST</a>
   */
  @PostMapping(path = "/cart/{id}/elements")
  public ResponseEntity<Resource<ShopCartElement>> addElementToCart(@PathVariable Long id, @RequestBody CartOrderElementDTO orderElementDTO)
      throws URISyntaxException {

    ShopCart shopCart = Optional.ofNullable(shopCartService.getShopCart(id)).orElseThrow(() -> new ShopNoCartFoundException(id));

    MP3 mp3 = mp3Repository.findById(orderElementDTO.getMp3id()).orElseThrow(
            () -> new MP3CanNotFoundException(orderElementDTO.getMp3id()));
    ShopCartElement element = shopCartService.addElementToShopCart(shopCart, mp3);
    Resource<ShopCartElement> shopCartRes = shopCartElementResourceAssembler.toResource(element);

    return ResponseEntity.created(
        new URI(shopCartRes.getId().expand().getHref()))
        .body(shopCartRes);
  }

  /**
   * @see <a href="http://restcookbook.com/HTTP%20Methods/put-vs-post/" >PUT vs POST</a>
   */
  @PutMapping(path = "/cart/{id}/elements/{elementId}")
  public ResponseEntity<Resource<ShopCartElement>> addElementToExistingCart(@PathVariable Long id, @PathVariable Long elementId, @RequestBody CartOrderElementDTO orderElementDTO)
      throws URISyntaxException {

    ShopCart shopCart = Optional.ofNullable(shopCartRepository.findOneById(id)).orElseGet(() -> shopCartService.getShopCart(id));

    MP3 mp3 = mp3Repository.findById(orderElementDTO.getMp3id()).orElseThrow(
            () -> new MP3CanNotFoundException(orderElementDTO.getMp3id()));

    Optional<ShopCartElement> shopCartElementOpt = shopCart.getCartElementSet().stream()
        .filter( element -> element.getId() == elementId).findFirst();

    if(shopCartElementOpt.isPresent()){
      ShopCartElement element = shopCartElementOpt.get();
      return ResponseEntity.ok( shopCartElementResourceAssembler.toResource(shopCartService.alterElementInShopCart(mp3, element)));
    } else {

      ShopCartElement element = shopCartService.addElementToShopCart(shopCart, mp3);
      Resource<ShopCartElement> shopCartRes = shopCartElementResourceAssembler.toResource(element);

      return ResponseEntity.created(new URI(shopCartRes.getId().expand().getHref()))
          .body(shopCartRes);
    }
  }

  // NO HAL just for preview purposes with inherit projection
  @GetMapping(path ="/plainCart/{id}")
  public ShopCart getCartByIdPlain(@PathVariable Long id){
    return shopCartRepository.findOneById(id);
  }
}
