package org.trenkmann.restsample.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.Matchers.hasSize;
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
import java.util.List;
import org.hibernate.Hibernate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.trenkmann.restsample.RestapiApplication;
import org.trenkmann.restsample.data.MP3Repository;
import org.trenkmann.restsample.model.MP3;

/**
 * @author andreas trenkmann
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RestapiApplication.class)
@AutoConfigureMockMvc
public class MP3ControllerTestIT {

  @Before
  public void initiate(){
    Hibernate.initialize(mp3Repository.findAll());
  }
  @Autowired
  MP3Repository mp3Repository;

  @Autowired
  MockMvc mockMvc;

  @Test
  public void givenMP3_whenGetMP3s_thenReturnHALJsonArrayAndOK() throws Exception {

    //given
    MP3 mp3 = mp3Repository.findById(1L).orElseThrow(Exception::new);
    List<MP3> mp3s = mp3Repository.findAll();

    //when
    this.mockMvc.perform(get("/mp3s"))
        .andDo(print())
        //then
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8))
        .andExpect(jsonPath("$._embedded.mP3s[0].title",is(mp3.getTitle())))
        .andExpect(jsonPath("$._embedded.mP3s[0].artist",is(mp3.getArtist())))
        .andExpect(jsonPath("$._embedded.mP3s[0].album",is(mp3.getAlbum())))
        .andExpect(jsonPath("$._embedded.mP3s[0].length",is(mp3.getLength())))
        .andExpect(jsonPath("$._embedded.mP3s", hasSize(mp3s.size())));
  }

  @Test
  public void  givenMP3_whenPostMP3_thenReturnHALJsonAndCreated() throws Exception{

    //given
    MP3 mp3 = new MP3(999, "title","artist","album","3:18",0);
    ObjectMapper objectMapper = new ObjectMapper();

    //when
    this.mockMvc.perform(post("/mp3s")
        .content(objectMapper.writeValueAsString(mp3))
        .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        //then
        .andExpect(status().is(HttpStatus.CREATED.value()))
        .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8))
        .andExpect(jsonPath("$.title", is(mp3.getTitle())))
        .andExpect(header().string("location", startsWith("http://localhost/mp3/")));
  }

  @Test
  public void givenMP3_whenPutMP3ById_thenReturnHALJsonAndOK() throws Exception {

    //given
    MP3 mp3 = mp3Repository.findById(1L).orElseThrow(Exception::new);
    mp3.setLength("2:15");
    mp3.setTitle("new Title");
    ObjectMapper objectMapper = new ObjectMapper();

    //when
    this.mockMvc.perform(put("/mp3/" + mp3.getId())
        .content(objectMapper.writeValueAsString(mp3))
        .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        //then
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8))
        .andExpect(jsonPath("$.title", is(mp3.getTitle())))
        .andExpect(jsonPath("$.artist", is(mp3.getArtist())))
        .andExpect(jsonPath("$.length", is(mp3.getLength())));

  }

  @Test
  public void givenMP3_whenGetMP3ById_thenReturnHALJsonAndOK() throws Exception {

    //given
    MP3 mp3 = mp3Repository.findById(1L).orElseThrow(Exception::new);

    //when
    this.mockMvc.perform(get("/mp3/" + mp3.getId())
        .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        //then
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8))
        .andExpect(jsonPath("$.title", is(mp3.getTitle())))
        .andExpect(jsonPath("$.artist", is(mp3.getArtist())))
        .andExpect(jsonPath("$.length", is(mp3.getLength())));

  }

  @Test
  public void givenMP3_whenDeleteMP3ById_thenReturnOK() throws Exception {

    //given
    MP3 mp3 = mp3Repository.findById(5L).orElseThrow(Exception::new);

    //when
    this.mockMvc.perform(delete("/mp3/" + mp3.getId())
        .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        //then
        .andExpect(status().is(HttpStatus.NO_CONTENT.value()));
  }


}