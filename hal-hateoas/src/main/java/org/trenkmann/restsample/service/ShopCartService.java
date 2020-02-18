package org.trenkmann.restsample.service;

import java.util.LinkedList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.trenkmann.restsample.data.ShopCartElementRepository;
import org.trenkmann.restsample.data.ShopCartRepository;
import org.trenkmann.restsample.exception.ShopCartNoElementsFoundException;
import org.trenkmann.restsample.model.MP3;
import org.trenkmann.restsample.model.ShopCart;
import org.trenkmann.restsample.model.ShopCartElement;

@Component
public class ShopCartService {

  private final ShopCartRepository shopCartRepository;
  private final ShopCartElementRepository shopCartElementRepository;

  public ShopCartService(ShopCartRepository shopCartRepository,
      ShopCartElementRepository shopCartElementRepository) {
    this.shopCartRepository = shopCartRepository;
    this.shopCartElementRepository = shopCartElementRepository;
  }

  public ShopCartElement addElementToShopCart(ShopCart shopCart, MP3 mp3) {

    List<ShopCartElement> cartElementSet = shopCart.getCartElementSet();
    int elementCounter = shopCart.getElementCounter();

    ShopCartElement shopCartElement = new ShopCartElement();
    shopCartElement.setMp3(mp3);
    shopCartElement.setOrderNr(++elementCounter);
    shopCartElement.setShopCart(shopCart);

    shopCart.setElementCounter(elementCounter);
    cartElementSet.add(shopCartElement);
    shopCart = shopCartRepository.saveAndFlush(shopCart);

    return shopCart.getCartElementSet().get(elementCounter - 1);
  }

  public ShopCart deleteElementFromShopCart(ShopCart shopCart, MP3 mp3) {
    List<ShopCartElement> cartElementSet = shopCart.getCartElementSet();

    if (cartElementSet == null) {
      throw new ShopCartNoElementsFoundException(shopCart.getId());
    }

    ShopCartElement element;
    element = cartElementSet.stream().filter(setElement -> setElement.getMp3() == mp3)
        .findFirst()
        .orElseThrow(() -> new ShopCartNoElementsFoundException(mp3.getId()));
    cartElementSet.remove(element);
    return shopCart;
  }

  public ShopCart getShopCart(Long id) {
    return shopCartRepository.findById(id).orElseGet(this::createNewShopCart);
  }

  private ShopCart createNewShopCart() {
    ShopCart shopCart = new ShopCart();
    shopCart.setElementCounter(0);
    shopCart.setCartElementSet(new LinkedList<>());
    return shopCartRepository.saveAndFlush(shopCart);
  }

  public ShopCartElement alterElementInShopCart(MP3 mp3,
      ShopCartElement element) {

    element.setMp3(mp3);

    return shopCartElementRepository.save(element);

  }
}
