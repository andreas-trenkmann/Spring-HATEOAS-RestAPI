package org.trenkmann.restsample.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author andreas trenkmann
 */
@Entity
@Data
@NoArgsConstructor
public class ShopOrder {

  @Id
  @GeneratedValue
  private Long id;
  private ShopOrderStatus status;

  @OneToOne
  private ShopCart shopCart;

  public ShopOrder(ShopCart shopCart) {
    this.shopCart = shopCart;
    this.setStatus(ShopOrderStatus.IN_PROGRESS);
  }
}
