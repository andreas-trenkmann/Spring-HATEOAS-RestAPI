package org.trenkmann.restsample.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class ShopCart implements Serializable {

  private static final long serialVersionUID = 1905122034950251207L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;


  @OneToMany(cascade = CascadeType.ALL, mappedBy = "shopCart", orphanRemoval = true)
  @JsonManagedReference
  private List<ShopCartElement> cartElementSet;

  private int elementCounter;

  public List<ShopCartElement>  getCartElementSet(){

    return cartElementSet;
  }

  public int hashCode(){
      return Objects.hash(id);
  }

  public boolean equals(Object o){
    if (this == o) return true;
    if (!(o instanceof ShopCart)) return false;
    ShopCart cart = (ShopCart) o;
    return Objects.equals(getId(), cart.getId());

  }

  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("\n class ShopCart{\n").append("id: ").append(elementCounter).append(" \n");
    cartElementSet.forEach(element -> builder.append(element.toString()));
    builder.append("\n}");
    return builder.toString();
  }

}
