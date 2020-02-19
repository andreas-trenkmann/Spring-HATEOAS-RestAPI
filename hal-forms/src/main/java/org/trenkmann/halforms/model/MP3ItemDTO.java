package org.trenkmann.halforms.model;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MP3ItemDTO implements Serializable {

	private static final long serialVersionUID = 1905111034950251207L;

	@NotNull
	private String title;
	@NotNull
	private String artist;

	private String album;
	@NotNull
	private String length;

	private int albumOrderNumber;

	public MP3ItemDTO(String title, String artist, String album, String length,
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
