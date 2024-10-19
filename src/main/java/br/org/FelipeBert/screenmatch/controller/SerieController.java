package br.org.FelipeBert.screenmatch.controller;

import br.org.FelipeBert.screenmatch.dto.EpisodioDTO;
import br.org.FelipeBert.screenmatch.dto.SerieDTO;
import br.org.FelipeBert.screenmatch.service.SerieService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/series")
public class SerieController {

    private SerieService service;

    public SerieController(SerieService service) {
        this.service = service;
    }

    @GetMapping
    public List<SerieDTO> obterSeries(){
        return service.obterTodasAsSeries();
    }

    @GetMapping("/top5")
    public List<SerieDTO> obterTopCincoSeries(){
        return service.obterTopCinco();
    }

    @GetMapping("/lancamentos")
    public List<SerieDTO> obterLancamentos(){
        return service.obterLancamentos();
    }

    @GetMapping("/{id}")
    public SerieDTO obterPorId(@PathVariable Long id){
        return service.obterSeriePorId(id);
    }

    @GetMapping("/{id}/temporadas/todas")
    public List<EpisodioDTO> obterTodasTemporadas(@PathVariable Long id){
        return service.obterTodasAsTempordas(id);
    }

    @GetMapping("/{id}/temporadas/top")
    public List<EpisodioDTO> obterTopCincoEpisodios(@PathVariable Long id){
        return service.obterTopCincoEpisodios(id);
    }

    @GetMapping("/{id}/temporadas/{numeroTemporada}")
    public List<EpisodioDTO> obterTodasTemporadas(@PathVariable Long id, @PathVariable Long numeroTemporada){
        return service.obterTemporadaPorNumero(id, numeroTemporada);
    }

    @GetMapping("/categoria/{genero}")
    public List<SerieDTO> obterSeriesPorGenero(@PathVariable String genero){
        return service.obterSeriesPorGenero(genero);
    }
}