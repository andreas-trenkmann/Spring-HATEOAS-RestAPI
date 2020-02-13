package org.trenkmann.restsample.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.trenkmann.restsample.model.EntireCartContentProjection;
import org.trenkmann.restsample.model.ShopCart;

public interface ShopCartRepository extends JpaRepository<ShopCart, Long> {

  ShopCart findOneById(Long cartId);
}
