package org.trenkmann.restsample.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.mediatype.vnderrors.VndErrors;
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
  private final ShopOrderResourceAssembler shopOrderAssembler;
  private final ShopCartRepository shopCartRepository;

  ShopOrderController(ShopOrderRepository shopOrderRepository,
      ShopOrderResourceAssembler shopOrderAssembler, ShopCartRepository shopCartRepository) {
    this.shopOrderRepository = shopOrderRepository;
    this.shopOrderAssembler = shopOrderAssembler;
    this.shopCartRepository = shopCartRepository;
  }

  @Operation(summary = "get all order from storage", security = @SecurityRequirement(name = "basicScheme"))
  @GetMapping(path = "/orders")
  public CollectionModel<EntityModel<ShopOrder>> getAllOrders() {

    return shopOrderAssembler.toCollectionModel(shopOrderRepository.findAll());
  }

  @Operation(summary = "get a single order from storage", security = @SecurityRequirement(name = "basicScheme"))
  @GetMapping(path = "/order/{orderId}")
  public EntityModel<ShopOrder> getOrderById(@PathVariable Long orderId) {
    ShopOrder shopOrder = shopOrderRepository.findById(orderId)
        .orElseThrow(() -> new ShoporderNotFoundException(orderId));
    return shopOrderAssembler.toModel(shopOrder);
  }

  @Operation(summary = "create a single order to storage", security = @SecurityRequirement(name = "basicScheme"))
  @PostMapping(path = "/orders")
  public ResponseEntity<EntityModel<ShopOrder>> newShopOrder(@RequestBody ShopOrderDTO shopOrderdto)
      throws URISyntaxException {

    ShopCart shopCart = shopCartRepository.findById(
        shopOrderdto.getShopCartId())
        .orElseThrow(() -> new ShopNoCartFoundException(shopOrderdto.getShopCartId()));

    ShopOrder shopOrder = new ShopOrder(shopCart);
    ShopOrder newShopOrder = shopOrderRepository.save(shopOrder);
    return ResponseEntity.created(new URI(
        shopOrderAssembler.toModel(newShopOrder).getRequiredLink(IanaLinkRelations.SELF).expand()
            .getHref()))
        .body(shopOrderAssembler.toModel(newShopOrder));
  }

  @Operation(summary = "delete a single order from storage", security = @SecurityRequirement(name = "basicScheme"))
  @DeleteMapping(path = "/order/{orderId}/cancel")
  public ResponseEntity<RepresentationModel> cancelOrder(@PathVariable Long orderId) {
    ShopOrder shopOrder = shopOrderRepository.findById(orderId)
        .orElseThrow(() -> new ShoporderNotFoundException(orderId));
    if (shopOrder.getStatus() == ShopOrderStatus.IN_PROGRESS) {
      shopOrder.setStatus(ShopOrderStatus.CANCELLED);
      return ResponseEntity.ok(shopOrderAssembler.toModel(shopOrderRepository.save(shopOrder)));
    }
    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(
        new VndErrors.VndError("Method not allowed",
            "You can not cancel in state " + shopOrder.getStatus())
    );
  }

  @Operation(summary = "alter or create a single order to storage", security = @SecurityRequirement(name = "basicScheme"))
  @PutMapping(path = "/order/{orderId}/complete")
  public ResponseEntity<RepresentationModel> completeOrder(@PathVariable Long orderId) {
    ShopOrder shopOrder = shopOrderRepository.findById(orderId)
        .orElseThrow(() -> new ShoporderNotFoundException(orderId));
    if (shopOrder.getStatus() == ShopOrderStatus.IN_PROGRESS) {
      shopOrder.setStatus(ShopOrderStatus.COMPLETED);
      return ResponseEntity.ok(shopOrderAssembler.toModel(shopOrderRepository.save(shopOrder)));
    }
    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(
        new VndErrors.VndError("Method not allowed",
            "You can not complete in state " + shopOrder.getStatus())
    );
  }
}
