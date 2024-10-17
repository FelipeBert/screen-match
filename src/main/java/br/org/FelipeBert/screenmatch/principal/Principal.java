package br.org.FelipeBert.screenmatch.principal;

import br.org.FelipeBert.screenmatch.model.*;
import br.org.FelipeBert.screenmatch.repository.SeriesRepository;
import br.org.FelipeBert.screenmatch.service.ConsumoApi;
import br.org.FelipeBert.screenmatch.service.ConverterDados;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Value;

import javax.swing.text.html.Option;
import java.util.*;
import java.util.stream.Collectors;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Principal {
    private final Scanner scanner = new Scanner(System.in);

    private final String ENDERECO = "http://www.omdbapi.com/?t=";

    private final String ENDERECO_TRADUCAO = "https://api.mymemory.translated.net/get?q=";

    private final String TRADUCAO_PTBR = "&langpair=";

    private final String API_KEY = System.getenv("OMDB_KEY");

    private final ConsumoApi consumoApi = new ConsumoApi();

    private final ConverterDados conversor = new ConverterDados();

    private List<DadosSerie> dadosSeries = new ArrayList<>();

    private List<Serie> series = new ArrayList<>();

    private SeriesRepository repository;

    private Optional<Serie> serieBusca;

    public Principal(SeriesRepository repository) {
        this.repository = repository;
    }

    public void exibiMenu() throws JsonProcessingException, UnsupportedEncodingException {
        String menu = """
                1 - Buscar Series
                2 - Buscar Episodios
                3 - Listar Series Buscadas
                4 - Buscar Serie por Titulo
                5 - Buscar Series por Ator
                6 - Top 5 Series
                7 - Buscar Series por Categoria
                8 - Buscar Series por Numero Temporadas e Avaliação
                9 - Buscar episodio por Trecho
                10 - Top 5 Episodios por Serie
                11 - Buscar Episodios a partir de uma Data
                
                0 - Sair
                """;
        int opcao;
        do {
            System.out.println(menu);
            opcao = scanner.nextInt();
            scanner.nextLine();
            switch (opcao){
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarSeriePorAtor();
                    break;
                case 6:
                    buscarTopCincoSeries();
                    break;
                case 7:
                    buscarSeriesPorCategoria();
                    break;
                case 8:
                    buscarSeriesPorTemporadasEAvaliacao();
                    break;
                case 9:
                    buscarEpisodioPorTrecho();
                    break;
                case 10:
                    buscarTopCincoEpisodiosPorSerie();
                    break;
                case 11:
                    buscarEpisodiosAposData();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção Invalida");
            }
        }while (opcao != 0 );
        scanner.close();
    }

    private void buscarEpisodiosAposData() {
        buscarSeriePorTitulo();
        if(serieBusca.isPresent()){
            System.out.println("Digite o ano limite de lançamento: ");
            int anoLeitura = scanner.nextInt();
            scanner.nextLine();

            List<Episodio> episodiosPorData = repository.episodiosPorSerieEAno(serieBusca.get(), anoLeitura);
            episodiosPorData.forEach(e ->
                    System.out.printf("Serie: %s Temporada %s - Episodio %s - %s - Avaliação %s\n",
                            e.getSerie().getTitulo(), e.getNumeroTemporada(), e.getNumeroEpisodio(), e.getTitulo(), e.getAvaliacao()));
        }
    }

    private void buscarTopCincoEpisodiosPorSerie() {
        buscarSeriePorTitulo();
        if(serieBusca.isPresent()){
            Serie serie = serieBusca.get();
            List<Episodio> topEpisodios = repository.topEpisodiosPorSerie(serie);
            topEpisodios.forEach(e ->
                    System.out.printf("Serie: %s Temporada %s - Episodio %s - %s - Avaliação %s\n",
                            e.getSerie().getTitulo(), e.getNumeroTemporada(), e.getNumeroEpisodio(), e.getTitulo(), e.getAvaliacao()));
        }
    }

    private void buscarEpisodioPorTrecho() {
        System.out.println("Digite o nome do Episodio: ");
        String nomeTrechoEpisodio = scanner.nextLine();

        List<Episodio> episodiosBuscados = repository.episodiosPorTrecho(nomeTrechoEpisodio);
        episodiosBuscados.forEach(e ->
                System.out.printf("Serie: %s Temporada %s - Episodio %s - %s\n",
                        e.getSerie().getTitulo(), e.getNumeroTemporada(), e.getNumeroEpisodio(), e.getTitulo()));
    }

    private void buscarSeriesPorTemporadasEAvaliacao() {
        System.out.println("Digite o Numero maximo de temporadas de uma serie: ");
        Integer numeroTemporadasMaximo = scanner.nextInt();

        System.out.println("Digite a Avaliacao minima pelo qual deseja buscar: ");
        Double avaliacaoMinima = scanner.nextDouble();

        List<Serie> seriesBuscadas = repository.seriesPorTemporadasEAvaliacao(numeroTemporadasMaximo, avaliacaoMinima);
        System.out.println("Series Encontradas: ");
        seriesBuscadas.forEach(System.out::println);
    }

    private void buscarSeriesPorCategoria() {
        System.out.println("Deseja buscar series por qual Categoria/Genero: ");
        var genero = scanner.nextLine();

        Categoria categoria = Categoria.fromPortugues(genero);
        List<Serie> seriesPorCateogira = repository.findByGenero(categoria);

        System.out.println("Series Categoria: " + genero);
        seriesPorCateogira.forEach(System.out::println);
    }

    private void buscarTopCincoSeries() {
        List<Serie> topSeries = repository.findTop5ByOrderByAvaliacaoDesc();
        topSeries.forEach(System.out::println);
    }

    private void buscarSeriePorAtor() {
        System.out.println("Digite o nome do ator: ");
        String nomeAtor = scanner.nextLine();

        System.out.println("Avaliacao a partir de que valor: ");
        Double avaliacao = scanner.nextDouble();

        List<Serie> seriesEncontradas = repository.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);
        seriesEncontradas.forEach(System.out::println);
    }

    private void buscarSeriePorTitulo() {
        System.out.println("Digite titulo da Serie para busca: ");
        String titulo = scanner.nextLine();
        serieBusca = repository.findByTituloContainingIgnoreCase(titulo);

        if(serieBusca.isPresent()){
            System.out.println("Dados da serie: " + serieBusca.get());
        }
        else {
            System.out.println("Serie nao encontrada!");
        }
    }

    private void listarSeriesBuscadas() {
        series = repository.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarSerieWeb() throws JsonProcessingException, UnsupportedEncodingException {
        DadosSerie dadosBuscados = getDadosSerie();
        Serie serie = new Serie(dadosBuscados);
        //dadosSeries.add(dadosBuscados);
        repository.save(serie);
        System.out.println(dadosBuscados);
    }

    private DadosSerie getDadosSerie() throws JsonProcessingException, UnsupportedEncodingException {
        System.out.println("Digite nome da Serie para busca: ");
        String nomeSerie = scanner.nextLine();

        String json = consumoApi.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dadosSerie = conversor.obterDados(json, DadosSerie.class);

        String sinopseCodificada = URLEncoder.encode(dadosSerie.sinopse(), "UTF-8");
        String traducaoEncode = URLEncoder.encode("en|pt-br");

        json = consumoApi.obterDados(ENDERECO_TRADUCAO + sinopseCodificada + TRADUCAO_PTBR + traducaoEncode).trim();
        TextoTraduzido traducao = conversor.obterDados(json, TextoTraduzido.class);

        return new DadosSerie(
                dadosSerie.titulo(),
                dadosSerie.totalTemporadas(),
                dadosSerie.atores(),
                traducao.traducao().textoTraduzido(),
                dadosSerie.genero(),
                dadosSerie.poster(),
                dadosSerie.avalaiacao()
        );
    }

    private void buscarEpisodioPorSerie() throws JsonProcessingException, UnsupportedEncodingException {
        System.out.println("Escolha uma Serie pelo Nome: ");
        listarSeriesBuscadas();
        String nome = scanner.nextLine();

        Optional<Serie> serie = repository.findByTituloContainingIgnoreCase(nome);

        if(serie.isEmpty()){
            System.out.println("Serie nao Encontrada");
        }
        else{
            Serie serieEncontrada = serie.get();

            List<DadosTemporada> temporadas = new ArrayList<>();
            for(int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++){
                String json = consumoApi.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") +"&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios()
                            .stream()
                            .map(e -> new Episodio(d.numeroTemporada(), e)))
                            .collect(Collectors.toList());

            serieEncontrada.setEpisodios(episodios);
            repository.save(serieEncontrada);
        }
    }
}