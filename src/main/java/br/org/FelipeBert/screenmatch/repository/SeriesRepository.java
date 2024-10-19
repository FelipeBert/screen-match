package br.org.FelipeBert.screenmatch.repository;

import br.org.FelipeBert.screenmatch.model.Categoria;
import br.org.FelipeBert.screenmatch.model.Episodio;
import br.org.FelipeBert.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SeriesRepository extends JpaRepository<Serie, Long> {
    Optional<Serie> findByTituloContainingIgnoreCase(String titulo);

    List<Serie> findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(String nomeAtor, Double avaliacao);

    List<Serie> findTop5ByOrderByAvaliacaoDesc();

    List<Serie> findByGenero(Categoria categoria);

    @Query(value = "SELECT s from Serie s WHERE s.avaliacao >= :avaliacao AND s.totalTemporadas <= :numeroTemporadas")
    List<Serie> seriesPorTemporadasEAvaliacao(Integer numeroTemporadas, Double avaliacao);

    @Query(value = "SELECT e from Serie s JOIN s.episodios e WHERE e.titulo ILIKE %:trechoEpisodio%")
    List<Episodio> episodiosPorTrecho(String trechoEpisodio);

    @Query(value = "SELECT e from Serie s JOIN s.episodios e WHERE s =:serie ORDER BY e.avaliacao DESC LIMIT 5")
    List<Episodio> topEpisodiosPorSerie(Serie serie);

    @Query(value = "SELECT e from Serie s JOIN s.episodios e WHERE s = :serieBuscada AND YEAR(e.lancamento) >= :anoLancamento")
    List<Episodio> episodiosPorSerieEAno(Serie serieBuscada, Integer anoLancamento);

    @Query(value = "SELECT s from Serie s JOIN s.episodios e GROUP BY s ORDER BY MAX(e.lancamento) DESC LIMIT 5")
    List<Serie> lancamentosMaisRecentes();

    @Query(value = "SELECT e from Serie s JOIN s.episodios e WHERE s.id = :id AND e.numeroTemporada = :numeroTemporada")
    List<Episodio> obterEpisodiosPorTemporda(Long id, Long numeroTemporada);
}