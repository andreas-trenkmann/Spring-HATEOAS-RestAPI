package org.trenkmann.restsample.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import lombok.Data;
import org.springframework.hateoas.core.Relation;

@Data
@Entity
@Relation(collectionRelation = "shopCartElements")
public class ShopCartElement {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @JsonIgnore
  @ManyToOne( fetch = FetchType.LAZY )
  private ShopCart shopCart;

  @OneToOne
  @JsonInclude
  private MP3 mp3;

  private int orderNr;

}
