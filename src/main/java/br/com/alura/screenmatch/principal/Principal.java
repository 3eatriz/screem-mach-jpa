package br.com.alura.screenmatch.principal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

@Service
public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";
    private List<DadosSerie> dadosSeries = new ArrayList<>();
    private List<Serie> series = new ArrayList<>();

    private Optional<Serie> serieBuscada;

    @Autowired
    private SerieRepository serieRepository;

    public Principal(SerieRepository serieRepository) {
        this.serieRepository = serieRepository;
    }

    public void exibeMenu() {
        var opcao = -1;
        while (opcao != 0) {
            var menu = """
                    1 - Buscar séries
                    2 - Buscar episódios
                    3 - Listar séries buscadas
                    4 - Buscar série por título
                    5 - Buscar séries por ator
                    6 - Buscar top 5 séries
                    7 - Buscar série por categoria
                    8 - Filtrar séries por número de temporadas e avaliação
                    9 - Buscar episódio por trecho
                    10 - Top 5 episódios por série
                    11 - Buscar episódios a partir de uma data

                    0 - Sair
                    """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
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
                    buscarSeriesPorAtor();
                    break;
                case 6:
                    buscarTop5Series();
                    break;
                case 7:
                    buscarSeriesPorCategoria();
                    break;
                case 8:
                    filtrarSeriesPorTemporadasEAvaliacao();
                    break;
                case 9:
                    buscarEpisodioPorTrecho();
                    break;
                case 10:
                    topEpisodiosPorSerie();
                    break;
                case 11:
                    buscarEpisodiosDepoisDeUmaData();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }

    }

    private void buscarEpisodiosDepoisDeUmaData() {
        buscarSeriePorTitulo();
        if(serieBuscada.isPresent()) {
            System.out.println("Digite o ano limite de lançamento");
            var anoLancamento = leitura.nextInt();
            leitura.nextLine();

            List<Episodio> episodiosAno = serieRepository.episodioPorSerieEAno(anoLancamento, serieBuscada.get());
            episodiosAno.forEach(e -> System.out.println("Temporada: " + e.getTemporada() + " - Episódio: " + e.getTitulo() + " - Data de Lançamento: " + e.getDataLancamento()));
        }
    }

    private void topEpisodiosPorSerie() {
        buscarSeriePorTitulo();
        if(serieBuscada.isPresent()) {
            var serie = serieBuscada.get();
            List<Episodio> topEpisodios = serieRepository.topEpisodiosPorSerie(serie);
            System.out.println("Top 5 episódios da série " + serie.getTitulo() + ":");
            topEpisodios.forEach(e -> System.out.println("Temporada: " + e.getTemporada() + " - Episódio: " + e.getTitulo() + " - Avaliação: " + e.getAvaliacao()));
        }
    }

    private void buscarEpisodioPorTrecho() {
        System.out.println("Digite o nome para buscar os episódios:");
        var trecho = leitura.nextLine();
        List<Episodio> episodios = serieRepository.episodiosPorTrecho(trecho);
        episodios.forEach(e -> System.out.println("Série: " + e.getSerie().getTitulo() + " - Temporada: " + e.getTemporada() + " - Episódio: " + e.getTitulo()));

    }
        

    private void filtrarSeriesPorTemporadasEAvaliacao() {
        System.out.println("Digite o número máximo de temporadas para filtrar as séries:");
        var totalTemporadas = leitura.nextInt();
        System.out.println("Digite a avaliação mínima para filtrar as séries:");
        var avaliacao = leitura.nextDouble();
        List<Serie> series = serieRepository.seriesPorTemporadaEAvaliacao(totalTemporadas, avaliacao);
        System.out.println("Series encontradas com no máximo " + totalTemporadas + " temporadas e avaliação mínima de " + avaliacao + ":");
        series.forEach(System.out::println);
    }

    private void buscarSeriesPorCategoria() {
        System.out.println("Digite a categoria para buscar as séries:");
        var nomeGenero = leitura.nextLine();
        Categoria categoria = Categoria.fromPortugues(nomeGenero);
        List<Serie> series = serieRepository.findByGenero(categoria);
        System.out.println("Series encontradas para a categoria " + nomeGenero + ":");
        series.forEach(System.out::println);
    }

    private void buscarTop5Series() {
        System.out.println("Top 5 séries por avaliação:");
        List<Serie> top5Series = serieRepository.findTop5ByOrderByAvaliacaoDesc();
        top5Series.forEach(s -> System.out.println(s.getTitulo() + " - Avaliação: " + s.getAvaliacao()));
    }

    private void buscarSeriesPorAtor() {
        System.out.println("Digite o nome do ator para buscar as séries:");
        var nomeAtor = leitura.nextLine();
        System.out.println("Digite a avaliação mínima para buscar as séries:");
        var avaliacaoMinima = leitura.nextDouble();
        List<Serie> series = serieRepository.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacaoMinima);
        System.out.println("Series encontradas para o ator " + nomeAtor + ":");
        series.forEach(s -> System.out.println(s.getTitulo() + " - Avaliação: " + s.getAvaliacao()));
    }

    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados);
        serieRepository.save(serie);
        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        System.out.println(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie() {
        listarSeriesBuscadas();
        System.out.println("Digite o nome da série para buscar os episódios");
        var nomeSerie = leitura.nextLine();

        Optional<Serie> serieBuscada = serieRepository.findByTituloContainingIgnoreCase(nomeSerie);

        if (serieBuscada.isPresent()) {
            var serie = serieBuscada.get();
            List<DadosTemporada> temporadas = new ArrayList<>();
            for (int i = 1; i <= serie.getTotalTemporadas(); i++) {
                var json = consumo
                        .obterDados(ENDERECO + serie.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream().map(e -> new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());

            serie.setEpisodios(episodios);
            serieRepository.save(serie);
        } else {
            System.out.println("Série não encontrada.");
        }
    }

    private void listarSeriesBuscadas() {

        series = serieRepository.findAll();

        System.out.println();
        System.out.println("Series buscadas:");
        System.out.println();

        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarSeriePorTitulo() {
        System.out.println("Digite o nome da série para a busca:");
        var nomeSerie = leitura.nextLine();
        serieBuscada = serieRepository.findByTituloContainingIgnoreCase(nomeSerie);

        if(serieBuscada.isPresent()) {
            System.out.println("Dados da série: " + serieBuscada.get());
        } else {
            System.out.println("Série não encontrada.");
        }
    }
}