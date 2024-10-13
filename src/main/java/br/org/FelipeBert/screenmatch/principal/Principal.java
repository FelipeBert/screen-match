package br.org.FelipeBert.screenmatch.principal;

import br.org.FelipeBert.screenmatch.model.DadosEpisodio;
import br.org.FelipeBert.screenmatch.model.DadosSerie;
import br.org.FelipeBert.screenmatch.model.DadosTemporada;
import br.org.FelipeBert.screenmatch.model.Episodio;
import br.org.FelipeBert.screenmatch.service.ConsumoApi;
import br.org.FelipeBert.screenmatch.service.ConverterDados;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private final Scanner scanner = new Scanner(System.in);

    private final String ENDERECO = "http://www.omdbapi.com/?t=";

    private final String API_KEY = "&apikey=key";

    private final ConsumoApi consumoApi = new ConsumoApi();

    private final ConverterDados conversor = new ConverterDados();

    public void exibiMenu() throws JsonProcessingException {
        System.out.println("Digite o Nome da Serie que deseja buscar: ");
        String nomeSerie = scanner.nextLine();
        nomeSerie = nomeSerie.replace(" ", "+");

        var json = consumoApi.obterDados(ENDERECO + nomeSerie + API_KEY);

        DadosSerie dadosSerie = conversor.obterDados(json, DadosSerie.class);

        System.out.println(dadosSerie + "\n");

        List<DadosTemporada> temporadas = new ArrayList<>();

        for(int i = 1; i <= dadosSerie.totalTemporadas(); i++){
            json = consumoApi.obterDados(ENDERECO + nomeSerie + "&season=" + i + API_KEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }

        List<DadosEpisodio> dadosEpisodios = temporadas
                .stream()
                .flatMap(t -> t.episodios().stream())
                .toList();

        //.collect(Collectors.toList()) -> Permite adicao de novos dados;
        //dadosEpisodios.add(new DadosEpisodio("teste", 3, "8","2020-01-01"));

//        dadosEpisodios.stream()
//                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
//                .peek(e -> System.out.println("Primeiro filtro N/A " + e))
//                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
//                .peek(e -> System.out.println("Ordenacao " + e))
//                .limit(10)
//                .peek(e -> System.out.println("Limite 10 " + e))
//                .map(e -> e.titulo().toUpperCase())
//                .peek(e -> System.out.println("Mapeamento " + e))
//                .forEach(System.out::println);

        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numeroTemporada(), d) ) ).collect(Collectors.toList());

//        System.out.println("Digite um Trecho do Titulo do episodio que deseja buscar: ");
//        String trechoTitulo = scanner.nextLine();
//
//        Optional<Episodio> episodioBuscado = episodios.stream()
//                                                    .filter(e -> e.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase()))
//                                                    .findFirst();
//
//        if(episodioBuscado.isEmpty()){
//            System.out.println("Não foi possivel encontrar o episodio!");
//        }
//        else {
//            System.out.println("Episodio encontrado, Pertence a " + episodioBuscado.get().getNumeroTemporada() + " Temporada");
//        }

        Map<Integer, Double> avaliacoesPorTemporada = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getNumeroTemporada,
                        Collectors.averagingDouble(Episodio::getAvaliacao)));

        System.out.println("Media das Temporadas");
        System.out.println(avaliacoesPorTemporada);

        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));
        System.out.println("Media " + est.getAverage());
        System.out.println("Melhor Episodio " + est.getMax());
        System.out.println("Pior Episodio " + est.getMin());
        System.out.println("Quantidade Episodios avaliados " + est.getCount());

        // episodios.forEach(System.out::println);
        
//        System.out.println("A partir de que ano voce deseja ver os episodios: ");
//        int ano = scanner.nextInt();
//        scanner.nextLine();
//
//        LocalDate dataBusca = LocalDate.of(ano, 1, 1);
//
//        episodios.stream()
//                .filter(e -> e.getLancamento() != null && e.getLancamento().isAfter(dataBusca))
//                .forEach(System.out::println);

        scanner.close();
    }
}