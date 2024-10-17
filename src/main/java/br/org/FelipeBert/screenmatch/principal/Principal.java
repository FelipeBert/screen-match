package br.org.FelipeBert.screenmatch.principal;

import br.org.FelipeBert.screenmatch.model.*;
import br.org.FelipeBert.screenmatch.repository.SeriesRepository;
import br.org.FelipeBert.screenmatch.service.ConsumoApi;
import br.org.FelipeBert.screenmatch.service.ConverterDados;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Value;

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

    public Principal(SeriesRepository repository) {
        this.repository = repository;
    }

    public void exibiMenu() throws JsonProcessingException, UnsupportedEncodingException {
        String menu = """
                1 - Buscar Series
                2 - Buscar Episodios
                3 - Listar Series Buscadas
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
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção Invalida");
            }
        }while (opcao != 0 );
        scanner.close();
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

        Optional<Serie> serie = series.stream()
                .filter(s -> s.getTitulo().toLowerCase().contains(nome.toLowerCase()))
                .findFirst();

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