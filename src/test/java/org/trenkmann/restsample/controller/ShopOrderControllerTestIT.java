package org.trenkmann.restsample.controller;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedList;
import javax.transaction.Transactional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.trenkmann.restsample.data.MP3Repository;
import org.trenkmann.restsample.data.ShopCartElementRepository;
import org.trenkmann.restsample.data.ShopCartRepository;
import org.trenkmann.restsample.model.ShopCart;
import org.trenkmann.restsample.model.ShopCartElement;
import org.trenkmann.restsample.model.dto.CartOrderElementDTO;

/**
 * @author andreas trenkmann
 */
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class ShopOrderControllerTestIT {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ShopCartRepository shopCartRepository;

  @Autowired
  ShopCartElementRepository shopCartElementRepository;

  @Autowired
  MP3Repository mp3Repository;

  @Before
  public void initialize() throws Exception {

    //also given
    ShopCart shopCart = new ShopCart();
    shopCart.setCartElementSet(new LinkedList<>());

    ShopCartElement shopCartElement = new ShopCartElement();
    shopCartElement.setShopCart(shopCart);
    shopCartElement.setMp3(this.mp3Repository.findById(1L).orElseThrow(Exception::new));
    shopCartElement.setOrderNr(1);

    shopCart.getCartElementSet().add(shopCartElement);
    shopCart.setElementCounter(1);
    this.shopCartRepository.saveAndFlush(shopCart);
  }

  @Test
  public void givenShopCart_whenGetShopCarts_thenReturnHALJsonOK() throws Exception {

    //given
    ShopCart shopCart = this.shopCartRepository.findOneById(1L);

    //when
    this.mockMvc.perform(MockMvcRequestBuilders.get("/carts")
        .accept(MediaTypes.HAL_JSON))
        .andDo(print())
        //then
        .andExpect(content().contentType(MediaTypes.HAL_JSON))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$._embedded.shopCarts[0].elementCounter", is(shopCart.getElementCounter())));
  }

  @Test
  public void givenShopCart_whenGetShopCartById_thenReturnHALJson() throws Exception {
    //given
    ShopCart shopCart = this.shopCartRepository.findById(1L).orElseThrow(Exception::new);

    //when
    this.mockMvc.perform(MockMvcRequestBuilders.get("/cart/" + shopCart.getId())
        .accept(MediaTypes.HAL_JSON))
        .andDo(print())
        //then
        .andExpect(content().contentType(MediaTypes.HAL_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.elementCounter", is(shopCart.getElementCounter())))
        .andExpect(jsonPath("$._links.elementInCart.href",
            endsWith("/cart/" + shopCart.getId() + "/elements")));
  }

  @Test
  public void givenShopCart_whenDeleteShopCartById_thenReturnNoContent() throws Exception {
    //given
    ShopCart shopCart = this.shopCartRepository.findOneById(1L);

    //when
    this.mockMvc.perform(MockMvcRequestBuilders.delete("/cart/" + shopCart.getId())
        .accept(MediaTypes.HAL_JSON))
        .andDo(print())
        //then
        .andExpect(status().isNoContent());
  }

  @Test
  public void givenShopCartElement_whenGetShopCartElements_thenReturnHALJsonAndOK()
      throws Exception {

    //given
    ShopCart shopCart = this.shopCartRepository.findById(1L).orElseThrow(Exception::new);
    ShopCartElement shopCartElement = shopCartElementRepository.findById(1L)
        .orElseThrow(Exception::new);

    //when
    this.mockMvc.perform(MockMvcRequestBuilders.get("/cart/" + shopCart.getId() + "/elements")
        .accept(MediaTypes.HAL_JSON))
        .andDo(print())
        //then
        .andExpect(content().contentType(MediaTypes.HAL_JSON))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$._embedded.shopCartElements[0].orderNr", is(shopCartElement.getOrderNr())))
        .andExpect(jsonPath("$._embedded.shopCartElements[0]._links.mp3.href",
            endsWith("/mp3/" + shopCartElement.getMp3().getId())));
  }

  @Test
  public void givenShopCartElement_whenPostShopCartElements_thenReturnHALJsonAndCreated()
      throws Exception {

    //given
    ObjectMapper mapper = new ObjectMapper();
    ShopCart shopCart;
    shopCart = this.shopCartRepository.findById(1L).orElseThrow(Exception::new);

    ShopCartElement newShopCartElement = new ShopCartElement();
    newShopCartElement.setMp3(this.mp3Repository.findById(5L).orElseThrow(Exception::new));
    newShopCartElement.setOrderNr(2);

    CartOrderElementDTO element = new CartOrderElementDTO();
    element.setMp3id(5L);

    //when
    this.mockMvc.perform(MockMvcRequestBuilders.post("/cart/" + shopCart.getId() + "/elements")
        .content(mapper.writeValueAsString(element))
        .contentType(MediaTypes.HAL_JSON))
        .andDo(print())
        //then
        .andExpect(content().contentType(MediaTypes.HAL_JSON))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.orderNr", is(newShopCartElement.getOrderNr())))
        .andExpect(jsonPath("$._links.mp3.href", endsWith("/mp3/" + element.getMp3id())));
  }

  @Test
  public void givenShopCartElement_whenPutShopCartElements_thenReturnHALJsonAndCreated()
      throws Exception {

    //given
    ObjectMapper mapper = new ObjectMapper();

    ShopCartElement newShopCartElement = new ShopCartElement();
    newShopCartElement.setMp3(this.mp3Repository.findById(5L).orElseThrow(Exception::new));
    newShopCartElement.setOrderNr(2);

    CartOrderElementDTO element = new CartOrderElementDTO();
    element.setMp3id(5L);

    //when
    this.mockMvc.perform(MockMvcRequestBuilders.put("/cart/" + 1 + "/element/" + 2)
        .content(mapper.writeValueAsString(element))
        .contentType(MediaTypes.HAL_JSON))
        .andDo(print())
        //then
        .andExpect(content().contentType(MediaTypes.HAL_JSON))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.orderNr", is(newShopCartElement.getOrderNr())))
        .andExpect(jsonPath("$._links.mp3.href", endsWith("/mp3/" + element.getMp3id())));
  }
}