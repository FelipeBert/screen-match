package br.org.FelipeBert.screenmatch.repository;

import br.org.FelipeBert.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeriesRepository extends JpaRepository<Serie, Long> {
}
