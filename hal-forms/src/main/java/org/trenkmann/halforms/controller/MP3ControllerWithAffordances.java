package org.trenkmann.halforms.controller;

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
import org.trenkmann.halforms.data.MP3Repository;
import org.trenkmann.halforms.exception.MP3CanNotDeleteException;
import org.trenkmann.halforms.exception.MP3CanNotFoundException;
import org.trenkmann.halforms.model.MP3Item;
import org.trenkmann.halforms.model.MP3ItemDTO;


@RestController
public class MP3ControllerWithAffordances {

  private final MP3ResourceAssemblerWithAffordances assembler;
  private final MP3Repository mp3Repository;
  private final ObjectMapper mapper = new ObjectMapper();

  MP3ControllerWithAffordances(MP3Repository mp3Repository,
      MP3ResourceAssemblerWithAffordances assembler) {
    this.assembler = assembler;
    this.mp3Repository = mp3Repository;
  }

  // aggregates
  @GetMapping(path = "/mp3s")
  public CollectionModel<EntityModel<MP3Item>> getAllMP3s() {

    return assembler.toCollectionModel(mp3Repository.findAll());

  }

  // single item
  @GetMapping(path = "/mp3/{id}")
  public EntityModel<MP3Item> getMP3ById(@PathVariable Long id) {
    MP3Item mp3Item = mp3Repository.findById(id).orElseThrow(() -> new MP3CanNotFoundException(id));

    return assembler.toModel(mp3Item);
  }

  @PostMapping(path = "/mp3s")
  public ResponseEntity<RepresentationModel> newMP3(@RequestBody MP3ItemDTO mp3ItemDTO)
      throws URISyntaxException {

    MP3Item mp3Item = mapper.convertValue(mp3ItemDTO, MP3Item.class);
    EntityModel<MP3Item> entityModel = assembler.toModel(mp3Repository.save(mp3Item));
    return ResponseEntity
        .created(
            new URI(entityModel.getRequiredLink(IanaLinkRelations.SELF).getHref()))
        .body(entityModel);
  }

  @PutMapping(path = "/mp3/{id}")
  public EntityModel<MP3Item> changeExistingMP3(@PathVariable Long id,
      @RequestBody MP3ItemDTO mp3ItemDTO) {

    MP3Item changedMP3Item = mapper.convertValue(mp3ItemDTO, MP3Item.class);
    MP3Item originMP3Item = mp3Repository.findById(id).orElse(new MP3Item(id));
    originMP3Item.setAlbum(changedMP3Item.getAlbum());
    originMP3Item.setAlbumOrderNumber(changedMP3Item.getAlbumOrderNumber());
    originMP3Item.setArtist(changedMP3Item.getArtist());
    originMP3Item.setLength(changedMP3Item.getLength());
    originMP3Item.setTitle(changedMP3Item.getTitle());

    return assembler.toModel(mp3Repository.save(originMP3Item));
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
