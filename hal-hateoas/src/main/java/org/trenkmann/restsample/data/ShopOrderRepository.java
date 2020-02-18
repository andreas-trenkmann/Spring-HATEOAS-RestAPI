package org.trenkmann.restsample.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.trenkmann.restsample.model.ShopOrder;

/**
 * @author andreas trenkmann
 */
public interface ShopOrderRepository extends JpaRepository<ShopOrder, Long> {


}
