package org.trenkmann.restsample.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.trenkmann.restsample.model.MP3;

@Repository
public interface MP3Repository extends JpaRepository<MP3, Long> {
	
//	@RestResource(rel = "title-contains", path="title-contains")
//	Page<MP3> findByTitleContaining(@Param("query") String query, Pageable page);
//
//	@RestResource(rel = "artist-contains", path="artist-contains", exported = false)
//	Page<MP3> findByArtistContaining(@Param("query") String query, Pageable page);
//
//	@RestResource(rel = "album-contains", path="album-contains", exported = false)
//	Page<MP3> findByAlbumContaining(@Param("query") String query, Pageable page);
}
