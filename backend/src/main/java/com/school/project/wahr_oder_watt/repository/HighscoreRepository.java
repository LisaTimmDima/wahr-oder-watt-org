package com.school.project.wahr_oder_watt.repository;

import com.school.project.wahr_oder_watt.dto.HighscoreDto;
import com.school.project.wahr_oder_watt.model.Highscore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HighscoreRepository extends JpaRepository<Highscore, Long> {

  @Query("""
        select new com.school.project.wahr_oder_watt.dto.HighscoreDto(
            coalesce(p.username, 'Unbekannt'),
            h.score
        )
        from Highscore h
        left join h.player p
        order by h.score desc
        """)
  List<HighscoreDto> findAllDtoOrderByScoreDesc();
}