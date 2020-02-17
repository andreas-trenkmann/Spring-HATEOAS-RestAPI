package org.trenkmann.restsample.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.trenkmann.restsample.model.ShopCartElement;

/**
 * @author andreastrenkmann
 */
public interface ShopCartElementRepository extends JpaRepository<ShopCartElement,Long> {

}
