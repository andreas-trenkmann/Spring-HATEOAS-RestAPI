package org.trenkmann.restsample.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;
import org.trenkmann.restsample.model.MP3;

@Component
public class MP3ResourceAssembler implements ResourceAssembler<MP3, Resource<MP3>>
{

  @Override
  public Resource<MP3> toResource(MP3 entity) {
    return new Resource<>( entity,
        linkTo(methodOn(MP3Controller.class).getMP3ById(entity.getId())).withSelfRel(),
        linkTo(methodOn(MP3Controller.class).getAllMP3s()).withRel("mp3s"));
  }
}
