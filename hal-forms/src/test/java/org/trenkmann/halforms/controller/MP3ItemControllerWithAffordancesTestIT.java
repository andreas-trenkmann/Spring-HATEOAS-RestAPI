package org.trenkmann.halforms.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.trenkmann.halforms.config.HypermediaConfiguration;
import org.trenkmann.halforms.data.MP3Repository;
import org.trenkmann.halforms.model.MP3Item;
import org.trenkmann.halforms.model.MP3ItemDTO;

/**
 * @author andreas trenkmann
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Import({HypermediaConfiguration.class})
public class MP3ItemControllerWithAffordancesTestIT {

  @Autowired
  private MockMvc mvc;
  @MockBean
  private MP3Repository repository;
  private final ObjectMapper objectMapper = new ObjectMapper();

  private void givenMP3Items() {
    BDDMockito.given(repository.findAll()).willReturn(Arrays.asList( //
        new MP3Item(4L, "title", "artist", "album", "30:20", 1), //
        new MP3Item(6L, "title", "artist", "album", "30:20", 2)));
  }

  @Test
  public void givenMP3Item_whenGetMP3s_thenReturnHALForm() throws Exception {

    //given
    givenMP3Items();

    //when
    mvc.perform(get("/mp3s").accept(MediaTypes.HAL_FORMS_JSON_VALUE)).andDo(
        print()) //
        .andExpect(status().isOk()) //
        .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_FORMS_JSON_VALUE)) //
        .andExpect(jsonPath("$._embedded.mP3Items[0].id", is(4))) //
        .andExpect(jsonPath("$._embedded.mP3Items[0]._templates.default.method", is("put"))) //
        .andExpect(
            jsonPath("$._embedded.mP3Items[0]._links.self.href", is("http://localhost/mp3/4")));
  }


  @Test
  public void givenMP3Item_whenGetMP3ById_thenReturnMP3ItemNotFoundException() throws Exception {

    //given
    //nothing
    //when
    mvc.perform(get("/mp3/2").accept(MediaTypes.HAL_FORMS_JSON_VALUE)).andDo(
        print()) //
        .andExpect(status().isNotFound()) //
        .andExpect(MockMvcResultMatchers.content().string("Can not found MP3 with id 2"));
  }

  @Test
  public void givenMP3Item_whenPostMP3Item_thenReturnHALJsonAndCreated() throws Exception {

    //given
    givenMP3Items();
    MP3ItemDTO mp3 = new MP3ItemDTO("title", "artist", "album", "3:18", 0);
    MP3Item mp3Item = objectMapper.convertValue(mp3, MP3Item.class);

    //when
    when(repository.save(any())).thenReturn(mp3Item);
    this.mvc.perform(post("/mp3s")
        .content(objectMapper.writeValueAsString(mp3))
        .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        //then
        .andExpect(status().is(HttpStatus.CREATED.value()))
        .andExpect(content().contentType(MediaTypes.HAL_JSON))
        .andExpect(jsonPath("$.title", is(mp3.getTitle())))
        .andExpect(header().string("location", startsWith("http://localhost/mp3/")));
  }

  @Test
  public void givenMP3Item_whenPutMP3ById_thenReturnHALJsonAndOK() throws Exception {

    //given
    givenMP3Items();
    MP3Item mp3 = new MP3Item(1, "title", "artist", "album", "3:18", 0);
    mp3.setLength("2:15");
    mp3.setTitle("new Title");

    //when
    when(repository.save(any())).thenReturn(mp3);
    this.mvc.perform(put("/mp3/" + mp3.getId())
        .content(objectMapper.writeValueAsString(mp3))
        .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        //then
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(content().contentType(MediaTypes.HAL_JSON))
        .andExpect(jsonPath("$.title", is(mp3.getTitle())))
        .andExpect(jsonPath("$.artist", is(mp3.getArtist())))
        .andExpect(jsonPath("$.length", is(mp3.getLength())));

  }

  @Test
  public void givenMP3Item_whenGetMP3ById_thenReturnHALJsonAndOK() throws Exception {

    //given
    MP3Item mp3 = new MP3Item(1, "title", "artist", "album", "3:18", 0);

    //when
    when(repository.findById(anyLong())).thenReturn(Optional.of(mp3));
    this.mvc.perform(get("/mp3/" + mp3.getId())
        .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        //then
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(content().contentType(MediaTypes.HAL_JSON))
        .andExpect(jsonPath("$.title", is(mp3.getTitle())))
        .andExpect(jsonPath("$.artist", is(mp3.getArtist())))
        .andExpect(jsonPath("$.length", is(mp3.getLength())));

  }

  @Test
  public void givenMP3Item_whenDeleteMP3ById_thenReturnOK() throws Exception {

    //given
    givenMP3Items();
    MP3Item mp3Item = new MP3Item(1, "title", "artist", "album", "3:18", 0);

    //when
    this.mvc.perform(delete("/mp3/" + mp3Item.getId())
        .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        //then
        .andExpect(status().is(HttpStatus.NO_CONTENT.value()));
  }

}