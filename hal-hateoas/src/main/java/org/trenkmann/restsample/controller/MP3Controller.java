package org.trenkmann.restsample.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.trenkmann.restsample.data.MP3Repository;
import org.trenkmann.restsample.exception.MP3CanNotDeleteException;
import org.trenkmann.restsample.exception.MP3CanNotFoundException;
import org.trenkmann.restsample.model.MP3;
import org.trenkmann.restsample.model.dto.MP3DTO;

@RestController
public class MP3Controller {

  private final MP3ResourceAssembler assembler;
  private MP3Repository mp3Repository;
  private final ObjectMapper mapper = new ObjectMapper();

  MP3Controller(MP3Repository mp3Repository, MP3ResourceAssembler assembler) {
    this.assembler = assembler;
    this.mp3Repository = mp3Repository;
  }

  // aggregates
  @GetMapping(path = "/mp3s")
  public CollectionModel<EntityModel<MP3>> getAllMP3s() {

    return assembler.toCollectionModel(mp3Repository.findAll());

  }

  // single item
  @GetMapping(path = "/mp3/{id}")
  public EntityModel<MP3> getMP3ById(@PathVariable Long id) {
    MP3 mp3 = mp3Repository.findById(id).orElseThrow(() -> new MP3CanNotFoundException(id));

    return assembler.toModel(mp3);
  }

  @PostMapping(path = "/mp3s")
  public ResponseEntity<RepresentationModel> newMP3(@RequestBody MP3DTO mp3DTO)
      throws URISyntaxException {
    MP3 mp3 = mapper.convertValue(mp3DTO, MP3.class);
    EntityModel<MP3> entityModel = assembler.toModel(mp3Repository.save(mp3));
    return ResponseEntity
        .created(
            new URI(entityModel.getRequiredLink(IanaLinkRelations.SELF).getHref()))
        .body(entityModel);
  }

  @PutMapping(path = "/mp3/{id}")
  public EntityModel<MP3> changeExistingMP3(@PathVariable Long id, @RequestBody MP3DTO mp3DTO) {
    MP3 changedMP3 = mapper.convertValue(mp3DTO, MP3.class);
    MP3 originMP3 = mp3Repository.findById(id).orElse(new MP3(id));
    originMP3.setAlbum(changedMP3.getAlbum());
    originMP3.setAlbumOrderNumber(changedMP3.getAlbumOrderNumber());
    originMP3.setArtist(changedMP3.getArtist());
    originMP3.setLength(changedMP3.getLength());
    originMP3.setTitle(changedMP3.getTitle());

    return assembler.toModel(mp3Repository.save(originMP3));
  }

  @DeleteMapping(path = "/mp3/{id}")
  public ResponseEntity<RepresentationModel> deleteMP3(@PathVariable Long id) {
    try {
      mp3Repository.deleteById(id);
    } catch (Exception ex) {
      throw new MP3CanNotDeleteException(id);
    }
    return ResponseEntity.noContent().build();
  }
}
