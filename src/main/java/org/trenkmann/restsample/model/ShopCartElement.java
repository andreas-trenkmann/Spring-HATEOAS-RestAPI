package org.trenkmann.restsample.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import lombok.Data;
import org.springframework.hateoas.core.Relation;

@Data
@Entity
@Relation(collectionRelation = "shopCartElements")
public class ShopCartElement implements Serializable {

  private static final long serialVersionUID = 1905122034950241207L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  private ShopCart shopCart;

  @OneToOne
  @JsonInclude
  private MP3 mp3;

  private int orderNr;

}
