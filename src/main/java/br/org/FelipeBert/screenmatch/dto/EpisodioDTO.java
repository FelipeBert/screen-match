package br.org.FelipeBert.screenmatch.dto;

import java.time.LocalDate;

public record EpisodioDTO(Integer numeroTemporada,
                          Integer numeroEpisodio,
                          String titulo) {
}