package br.org.FelipeBert.screenmatch.model;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Episodio {
    private Integer numeroTemporada;
    private Integer numeroEpisodio;
    private String titulo;
    private Double avaliacao;
    private LocalDate lancamento;

    public Episodio(Integer numeroTemporada, DadosEpisodio dadosEpisodio) {
        this.numeroTemporada = numeroTemporada;
        this.titulo = dadosEpisodio.titulo();
        this.numeroEpisodio = dadosEpisodio.numeroEpisodio();
        try {
            this.avaliacao = Double.valueOf(dadosEpisodio.avaliacao());
        }catch (NumberFormatException e){
            this.avaliacao = 0.0;
        }
        try {
            this.lancamento = LocalDate.parse(dadosEpisodio.dataLancamento());
        }catch (DateTimeParseException e){
            this.lancamento = null;
        }
    }

    public Integer getNumeroTemporada() {
        return numeroTemporada;
    }

    public void setNumeroTemporada(Integer numeroTemporada) {
        this.numeroTemporada = numeroTemporada;
    }

    public Integer getNumeroEpisodio() {
        return numeroEpisodio;
    }

    public void setNumeroEpisodio(Integer numeroEpisodio) {
        this.numeroEpisodio = numeroEpisodio;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Double getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(Double avaliacao) {
        this.avaliacao = avaliacao;
    }

    public LocalDate getLancamento() {
        return lancamento;
    }

    public void setLancamento(LocalDate lancamento) {
        this.lancamento = lancamento;
    }

    @Override
    public String toString() {
        return "Episodio{" +
                "numeroTemporada=" + numeroTemporada +
                ", numeroEpisodio=" + numeroEpisodio +
                ", titulo='" + titulo + '\'' +
                ", avaliacao=" + avaliacao +
                ", lancamento=" + lancamento +
                '}';
    }
}
