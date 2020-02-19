package org.trenkmann.halforms.controller;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.SneakyThrows;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import org.trenkmann.halforms.model.MP3Item;

@Component
public class MP3ResourceAssemblerWithAffordances implements
    RepresentationModelAssembler<MP3Item, EntityModel<MP3Item>> {

  @Override
  public EntityModel<MP3Item> toModel(MP3Item entity) {
    return new EntityModel<>(entity,
        linkTo(methodOn(MP3ControllerWithAffordances.class).getMP3ById(entity.getId()))
            .withSelfRel()
            .andAffordance(afford(methodOn(MP3ControllerWithAffordances.class)
                .changeExistingMP3(entity.getId(), null)))
            .andAffordance(
                afford(methodOn(MP3ControllerWithAffordances.class).deleteMP3(entity.getId()))),
        linkTo(methodOn(MP3ControllerWithAffordances.class).getAllMP3s()).withRel("mp3s"));
  }

  @SneakyThrows
  @Override
  public CollectionModel<EntityModel<MP3Item>> toCollectionModel(
      Iterable<? extends MP3Item> entities) {

    List<EntityModel<MP3Item>> list = StreamSupport.stream(entities.spliterator(), false)
        .map(this::toModel)
        .collect(Collectors.toList());

    return new CollectionModel<>(list,
        linkTo(methodOn(MP3ControllerWithAffordances.class).getAllMP3s()).withSelfRel()
            .andAffordance(afford(methodOn(MP3ControllerWithAffordances.class).newMP3(null))));
  }
}
