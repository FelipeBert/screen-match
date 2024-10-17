package br.org.FelipeBert.screenmatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TextoTraduzido(@JsonAlias("responseData") DadosTraducao traducao) {
}
