package org.trenkmann.halforms.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.trenkmann.halforms.config.HypermediaConfiguration;
import org.trenkmann.halforms.data.MP3Repository;
import org.trenkmann.halforms.model.MP3Item;

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

  @Test
  public void givenMP3_whenGetMP3s_thenReturnHALForm() throws Exception {

    //given
    BDDMockito.given(repository.findAll()).willReturn(Arrays.asList( //
        new MP3Item(4L, "title", "artist", "album", "30:20", 1), //
        new MP3Item(6L, "title", "artist", "album", "30:20", 2)));

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

}