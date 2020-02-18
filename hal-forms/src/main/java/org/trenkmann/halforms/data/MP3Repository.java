package org.trenkmann.halforms.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.trenkmann.halforms.model.MP3;

@Repository
public interface MP3Repository extends JpaRepository<MP3, Long> {

}
