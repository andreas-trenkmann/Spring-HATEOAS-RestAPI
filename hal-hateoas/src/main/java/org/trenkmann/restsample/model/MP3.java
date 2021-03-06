package org.trenkmann.restsample.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class MP3 implements Serializable {

  private static final long serialVersionUID = 1905111034950251207L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @NotNull
  @Column(columnDefinition = "VARCHAR", length = 100)
  private String title;

  @NotNull
  @Column(columnDefinition = "VARCHAR", length = 100)
  private String artist;

  @NotNull
  @Column(columnDefinition = "VARCHAR", length = 100)
  private String album;

  @NotNull
  @Column(columnDefinition = "VARCHAR", length = 12)
  private String length;

  private int albumOrderNumber;

  public MP3(long id, String title, String artist, String album, String length,
      int albumOrderNumber) {
    super();
    this.id = id;
    this.title = title;
    this.artist = artist;
    this.album = album;
    this.length = length;
    this.albumOrderNumber = albumOrderNumber;
  }

  public MP3(Long id) {
    this.id = id;
    this.title = "";
    this.artist = "";
    this.album = "";
    this.length = "";
  }

  @Override
  public String toString() {
    return "MP3 [id=" + id + ", title=" + title + ", artist=" + artist + ", album=" + album
        + ", length=" + length
        + ", albumOrderNumber=" + albumOrderNumber + "]";
  }


}
