package org.trenkmann.restsample.model.dto;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MP3DTO implements Serializable {

  private static final long serialVersionUID = 1905111034950251207L;

  private String title;
  private String artist;
  private String album;
  private String length;
  private int albumOrderNumber;

  public MP3DTO(String title, String artist, String album, String length,
      int albumOrderNumber) {
    super();
    this.title = title;
    this.artist = artist;
    this.album = album;
    this.length = length;
    this.albumOrderNumber = albumOrderNumber;
  }

  @Override
  public String toString() {
    return "MP3 [title=" + title + ", artist=" + artist + ", album=" + album
        + ", length=" + length
        + ", albumOrderNumber=" + albumOrderNumber + "]";
  }
}
