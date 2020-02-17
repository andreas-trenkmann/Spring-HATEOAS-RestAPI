package org.trenkmann.restsample.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
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

@RestController
public class MP3Controller {

  private final MP3ResourceAssembler assembler;
  private MP3Repository mp3Repository;

  MP3Controller(MP3Repository mp3Repository, MP3ResourceAssembler assembler){
    this.assembler = assembler;
    this.mp3Repository = mp3Repository;
  }

  // aggregates
  @GetMapping(path = "/mp3s")
  public Resources<Resource<MP3>> getAllMP3s() {
    List<Resource<MP3>> list =  mp3Repository.findAll().stream().map(
        assembler::toResource)
        .collect(Collectors.toList());

    return new Resources<>(list,
        linkTo(methodOn(MP3Controller.class).getAllMP3s()).withSelfRel());
  }

  // single item
  @GetMapping(path = "/mp3/{id}")
  public Resource<MP3> getMP3ById(@PathVariable Long id) {
    MP3 mp3 = mp3Repository.findById(id).orElseThrow(() -> new MP3CanNotFoundException(id));

    return assembler.toResource(mp3);
  }

  @PostMapping(path = "/mp3s")
  public ResponseEntity<ResourceSupport> newMP3(@RequestBody MP3 mp3) throws URISyntaxException {
    Resource<MP3> resource = assembler.toResource(mp3Repository.save(mp3));
    return ResponseEntity
        .created(
            new URI(resource.getId().expand().getHref()))
        .body(resource);
  }

  @PutMapping(path = "/mp3/{id}")
  public Resource<MP3> changeExistingMP3(@PathVariable Long id, @RequestBody MP3 changedMP3) {
    MP3 originMP3 = mp3Repository.findById(id).orElse(new MP3(id));
    originMP3.setAlbum(changedMP3.getAlbum());
    originMP3.setAlbumOrderNumber(changedMP3.getAlbumOrderNumber());
    originMP3.setArtist(changedMP3.getArtist());
    originMP3.setLength(changedMP3.getLength());
    originMP3.setTitle(changedMP3.getTitle());

    return assembler.toResource(mp3Repository.save(originMP3));
  }

  @DeleteMapping(path = "/mp3/{id}")
  public ResponseEntity<ResourceSupport> deleteMP3(@PathVariable Long id) {
    try {
      mp3Repository.deleteById(id);
    } catch (Exception ex) {
      throw new MP3CanNotDeleteException(id);
    }
    return ResponseEntity.noContent().build();
  }
}
