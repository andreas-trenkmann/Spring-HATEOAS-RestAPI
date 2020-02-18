package org.trenkmann.restsample.controller;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import org.trenkmann.restsample.model.MP3;

@Component
public class MP3ResourceAssembler implements RepresentationModelAssembler<MP3, EntityModel<MP3>> {

  @Override
  public EntityModel<MP3> toModel(MP3 entity) {
    return new EntityModel<>(entity,
        linkTo(methodOn(MP3Controller.class).getMP3ById(entity.getId())).withSelfRel(),
        linkTo(methodOn(MP3Controller.class).getAllMP3s()).withRel("mp3s"));
  }

  @Override
  public CollectionModel<EntityModel<MP3>> toCollectionModel(
      Iterable<? extends MP3> entities) {

    List<EntityModel<MP3>> list = StreamSupport.stream(entities.spliterator(), false)
        .map(this::toModel)
        .collect(Collectors.toList());

    return new CollectionModel<>(list,
        linkTo(methodOn(MP3Controller.class).getAllMP3s()).withSelfRel());
  }
}
