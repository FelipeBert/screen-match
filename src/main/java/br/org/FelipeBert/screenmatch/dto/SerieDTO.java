package br.org.FelipeBert.screenmatch.dto;

import br.org.FelipeBert.screenmatch.model.Categoria;

public record SerieDTO(Long id,
                       String titulo,
                       Integer totalTemporadas,
                       String atores,
                       String sinopse,
                       Categoria categoria,
                       String poster,
                       Double avaliacao) {
}