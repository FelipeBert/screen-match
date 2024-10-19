package br.org.FelipeBert.screenmatch.service;

import br.org.FelipeBert.screenmatch.dto.EpisodioDTO;
import br.org.FelipeBert.screenmatch.dto.SerieDTO;
import br.org.FelipeBert.screenmatch.model.Categoria;
import br.org.FelipeBert.screenmatch.model.Episodio;
import br.org.FelipeBert.screenmatch.model.Serie;
import br.org.FelipeBert.screenmatch.repository.SeriesRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SerieService {
    private SeriesRepository repository;

    public SerieService(SeriesRepository repository) {
        this.repository = repository;
    }

    public List<SerieDTO> obterTodasAsSeries(){
        return converteParaSerieDto(repository.findAll());
    }

    public List<SerieDTO> obterTopCinco() {
        return converteParaSerieDto(repository.findTop5ByOrderByAvaliacaoDesc());
    }

    public List<SerieDTO> obterLancamentos() {
        return converteParaSerieDto(repository.lancamentosMaisRecentes());
    }

    public SerieDTO obterSeriePorId(Long id) {
        Optional<Serie> serieBuscada = repository.findById(id);

        if(serieBuscada.isPresent()){
            Serie serieEncontrada = serieBuscada.get();
            return new SerieDTO(serieEncontrada.getId(), serieEncontrada.getTitulo(),
                    serieEncontrada.getTotalTemporadas(), serieEncontrada.getAtores(),
                    serieEncontrada.getSinopse(), serieEncontrada.getGenero(), serieEncontrada.getPoster(), serieEncontrada.getAvaliacao());
        }
        return null;
    }

    public List<EpisodioDTO> obterTodasAsTempordas(Long id) {
        Optional<Serie> serieBuscada = repository.findById(id);

        if(serieBuscada.isPresent()){
            Serie serieEncontrada = serieBuscada.get();

           return converteParaEpisodioDto(serieEncontrada.getEpisodios());
        }
        return null;
    }

    public List<EpisodioDTO> obterTemporadaPorNumero(Long id, Long numeroTemporada) {
        Optional<Serie> serieBuscada = repository.findById(id);

        if(serieBuscada.isPresent()){
            List<Episodio> episodiosPorTemporada = repository.obterEpisodiosPorTemporda(id, numeroTemporada);

            return converteParaEpisodioDto(episodiosPorTemporada);
        }
        return null;
    }

    public List<SerieDTO> obterSeriesPorGenero(String genero) {
        Categoria categoria = Categoria.fromPortugues(genero);
        List<Serie> series = repository.findByGenero(categoria);
        return converteParaSerieDto(series);
    }

    public List<EpisodioDTO> obterTopCincoEpisodios(Long id) {
        Optional<Serie> serieBuscada = repository.findById(id);

        if(serieBuscada.isPresent()){
            List<Episodio> topCincoEpisodios = repository.topEpisodiosPorSerie(serieBuscada.get());

            return converteParaEpisodioDto(topCincoEpisodios);
        }
        return null;
    }

    private List<SerieDTO> converteParaSerieDto(List<Serie> series){
        return series.stream().map(s -> new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(),
                        s.getAtores(), s.getSinopse(), s.getGenero(), s.getPoster(), s.getAvaliacao()))
                .collect(Collectors.toList());
    }

    private List<EpisodioDTO> converteParaEpisodioDto(List<Episodio> episodios){
        return episodios.stream()
                .map(e -> new EpisodioDTO(e.getNumeroTemporada(), e.getNumeroEpisodio(), e.getTitulo()))
                .collect(Collectors.toList());
    }
}