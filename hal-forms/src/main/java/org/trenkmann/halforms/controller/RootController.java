package org.trenkmann.halforms.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author andreas trenkmann
 */
@RestController
public class RootController {

  @GetMapping(path = "/")
  public ResponseEntity<RepresentationModel> getRootPath() {
    RepresentationModel resource = new RepresentationModel();
    resource.add(linkTo(methodOn(RootController.class).getRootPath()).withSelfRel());
    resource.add(linkTo(methodOn(MP3ControllerWithAffordances.class).getAllMP3s()).withRel("mp3s"));

    return ResponseEntity.ok(resource);
  }

}
