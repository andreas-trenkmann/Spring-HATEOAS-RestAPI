package org.trenkmann.restsample.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.trenkmann.restsample.data.ShopCartRepository;
import org.trenkmann.restsample.data.ShopOrderRepository;
import org.trenkmann.restsample.exception.ShopNoCartFoundException;
import org.trenkmann.restsample.exception.ShoporderNotFoundException;
import org.trenkmann.restsample.model.ShopCart;
import org.trenkmann.restsample.model.ShopOrder;
import org.trenkmann.restsample.model.ShopOrderStatus;
import org.trenkmann.restsample.model.dto.ShopOrderDTO;

/**
 * @author andreas trenkmann
 */
@RestController
public class ShopOrderController {

  private final ShopOrderRepository shopOrderRepository;
  private final ShopOrderAssembler shopOrderAssembler;
  private final ShopCartRepository shopCartRepository;

  ShopOrderController(ShopOrderRepository shopOrderRepository,
      ShopOrderAssembler shopOrderAssembler, ShopCartRepository shopCartRepository) {
    this.shopOrderRepository = shopOrderRepository;
    this.shopOrderAssembler = shopOrderAssembler;
    this.shopCartRepository = shopCartRepository;
  }

  @GetMapping(path = "/orders")
  public Resources<Resource<ShopOrder>> getAllOrders() {

    List<Resource<ShopOrder>> listOfOrders = shopOrderRepository.findAll().stream()
        .map(shopOrderAssembler::toResource).collect(
            Collectors.toList());

    return new Resources<>(listOfOrders,
        linkTo(methodOn(ShopOrderController.class).getAllOrders()).withSelfRel());
  }

  @GetMapping(path = "/order/{orderId}")
  public Resource<ShopOrder> getOrderById(@PathVariable Long orderId) {
    ShopOrder shopOrder = shopOrderRepository.findById(orderId)
        .orElseThrow(() -> new ShoporderNotFoundException(orderId));
    return shopOrderAssembler.toResource(shopOrder);
  }

  @PostMapping(path = "/orders")
  public ResponseEntity<Resource<ShopOrder>> newShopOrder(@RequestBody ShopOrderDTO shopOrderdto) {

    ShopCart shopCart = shopCartRepository.findById(
        shopOrderdto.getShopCartId())
        .orElseThrow(() -> new ShopNoCartFoundException(shopOrderdto.getShopCartId())
        );

    ShopOrder shopOrder = new ShopOrder(shopCart);
    ShopOrder newShopOrder = shopOrderRepository.save(shopOrder);
    return ResponseEntity.created(
        linkTo(methodOn(ShopOrderController.class).getOrderById(newShopOrder.getId())).toUri())
        .body(shopOrderAssembler.toResource(newShopOrder));
  }

  @DeleteMapping(path = "/order/{orderId}/cancel")
  public ResponseEntity<ResourceSupport> cancelOrder(@PathVariable Long orderId) {
    ShopOrder shopOrder = shopOrderRepository.findById(orderId)
        .orElseThrow(() -> new ShoporderNotFoundException(orderId));
    if (shopOrder.getStatus() == ShopOrderStatus.IN_PROGRESS) {
      shopOrder.setStatus(ShopOrderStatus.CANCELLED);
      return ResponseEntity.ok(shopOrderAssembler.toResource(shopOrderRepository.save(shopOrder)));
    }
    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(
        new VndErrors.VndError("Method not allowed",
            "You can not cancel in state " + shopOrder.getStatus())
    );
  }

  @PutMapping(path = "/order/{orderId}/complete")
  public ResponseEntity<ResourceSupport> completeOrder(@PathVariable Long orderId) {
    ShopOrder shopOrder = shopOrderRepository.findById(orderId)
        .orElseThrow(() -> new ShoporderNotFoundException(orderId));
    if (shopOrder.getStatus() == ShopOrderStatus.IN_PROGRESS) {
      shopOrder.setStatus(ShopOrderStatus.COMPLETED);
      return ResponseEntity.ok(shopOrderAssembler.toResource(shopOrderRepository.save(shopOrder)));
    }
    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(
        new VndErrors.VndError("Method not allowed",
            "You can not complete in state " + shopOrder.getStatus())
    );
  }


}
