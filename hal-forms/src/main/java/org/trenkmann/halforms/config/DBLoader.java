package org.trenkmann.halforms.config;

import java.util.Random;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.trenkmann.halforms.data.MP3Repository;
import org.trenkmann.halforms.model.MP3Item;


@Component
public class DBLoader implements ApplicationRunner {

  Logger logger = LoggerFactory.getLogger(DBLoader.class);
  private final MP3Repository mP3Repository;
  private Random random = new Random();

  @Autowired
  DBLoader(MP3Repository mP3Repository) {
    this.mP3Repository = mP3Repository;
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    String[] templates = {
        "Jump up and %s",
        "%s (acoustic)",
        "%s (Remix)",
        "%s (live Version)",
        "Undisclore: %s",
        "Demotape: %s",
        "Gimme a trip to: %s",
        "%s with me"
    };

    String[] buzzWords = {
        "have a nice time",
        "U Turn me Up",
        "Feel good",
        "Groovy",
        "Supertrooper",
        "give me a call",
        "bauser this",
        "Just the swag",
    };

    String[] artistFirstName = {
        "Jennifer %s",
        "Briteney %s",
        "Sandro %s",
        "Thomas %s",
        "Kerstin %s"
    };

    String[] artistLastName = {
        "Lopez",
        "Kiedis",
        "Flea",
        "de la Rocha",
        "Tankian"
    };

    String[] albums = {
        "Thriller",
        "Their Greatest Hits",
        "Midnight Memories",
        "Unorthodox Jukebox",
        "Artpop",
        "To Be Loved",
        "Night Visions"

    };
    long bound = 200;

    IntStream.range(0, 100)
        .forEach(i -> {
          String template = templates[i % templates.length];
          String buzzword = buzzWords[i % buzzWords.length];
          String album = albums[i % albums.length];
          String firstName = artistFirstName[i % artistFirstName.length];
          String lastname = artistLastName[i % artistLastName.length];
          MP3Item mp3Item = new MP3Item(random.nextInt(1000 - 200) + bound,
              String.format(template, buzzword),
              String.format(firstName, lastname), album,
              random.nextInt(60 - 59) + 59 + ":" + random.nextInt(60 - 59) + 59,
              random.nextInt(5 - 4) + 5);

          mP3Repository.save(mp3Item);

          logger.info("JPA-Data Entry = {}", mp3Item);

        });

  }

}
