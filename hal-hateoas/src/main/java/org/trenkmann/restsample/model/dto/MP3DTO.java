package org.trenkmann.restsample.model.dto;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MP3DTO implements Serializable {

  private static final long serialVersionUID = 1905111034950251207L;

  @NotNull
  private String title;
  @NotNull
  private String artist;

  private String album;
  @NotNull
  private String length;

  private int albumOrderNumber;

  @Override
  public String toString() {
    return "MP3 [title=" + title + ", artist=" + artist + ", album=" + album
        + ", length=" + length
        + ", albumOrderNumber=" + albumOrderNumber + "]";
  }
}
