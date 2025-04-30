package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.*;

import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;


import java.util.*;
import java.util.stream.Collectors;

public class Principal {


    private SerieRepository repository;

    public Principal(SerieRepository repository){
        this.repository = repository;
    }

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";
    private List<Serie> series = new ArrayList<>();

    private Optional<Serie> serieBuscada;

    public void exibeMenu() {
        var menu = """
                1 - Buscar séries
                2 - Buscar episódios
                3 - Listar Séries Buscadas
                4 - Buscar série por título
                5 - Buscar séries por autor
                6 - Top 5 séries
                7 - Buscar séries por categoria
                8 - Buscar por séries por total de temporadas + avaliação 
                9 - Buscar episódio por trecho
                10 - Top 5 episódios por série
                
                0 - Sair                                 
                """;

        System.out.println(menu);
        var opcao = leitura.nextInt();
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
            case 5:
                buscarSeriesPorAtor();
                break;
            case 6:
                buscarTop5Series();
                break;
            case 7:
                buscarSeriesPorCategoria();
            case 8:
                buscarSeriesPorTemporadas();
            case 9:
                buscarEpisodioPorTrecho();
            case 10:
                buscarTop5EpisodiosPorSerie();
            case 0:
                System.out.println("Saindo...");
                break;
            default:
                System.out.println("Opção inválida");
        }
    }




    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados);
        repository.save(serie);
        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie() {
        listarSeriesBuscadas();
        System.out.println("Escolha uma séria pelo nome: ");
        var nomeSerie = leitura.nextLine();

        Optional<Serie> serie = this.series.stream().filter(s -> s.getTitulo().toLowerCase().contains(nomeSerie.toLowerCase())).findFirst();

        if (serie.isPresent()) {
            var serieEncontrada = serie.get();
            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream().flatMap(d -> d.episodios().stream().map(e -> new Episodio(d.numero(), e)))
                    .toList();
            serieEncontrada.setEpisodios(episodios);
            repository.save(serieEncontrada);
        } else {
            System.out.println("Série não encontrada");
        }
    }

    private void listarSeriesBuscadas() {
        this.series = repository.findAll();
        this.series.stream().sorted(Comparator.comparing(Serie::getTitulo)).forEach(System.out::println);
    }

    private void buscarSeriePorTitulo() {
        System.out.println("Digite o título da série: ");
        var nome = leitura.nextLine();

        serieBuscada = repository.findByTituloContainingIgnoreCase(nome);

        if (serieBuscada.isPresent()){
            System.out.println("Dados da série: " + serieBuscada);
        }
        else {
            System.out.println("Série não encontrada.");
        }

    }


    private void buscarSeriesPorAtor() {
        System.out.println("Digite o nome do ator:");
        var nome  = leitura.nextLine();
        System.out.println("Avaliações a partir de que valor?");
        var avaliacao = leitura.nextDouble();

        List<Serie> seriesEncontradas = repository.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nome, avaliacao);
        System.out.println("Séries em que " + nome + " trabalhou: ");
        seriesEncontradas.forEach(s -> System.out.println(s.getTitulo() + " avaliação: " + s.getAvaliacao()));
    }

    private void buscarTop5Series() {
        List<Serie> seriesTop = repository.findTop5ByOrderByAvaliacaoDesc();
        seriesTop.forEach(s -> System.out.println(s.getTitulo() + " avaliação: " + s.getAvaliacao()));
    }

    private void buscarSeriesPorCategoria() {
        System.out.println("Deseja buscar séries de que categoria/gênero: ");
        var nomeGenero = leitura.nextLine();
        ECategoria categoria = ECategoria.fromPortugues(nomeGenero);
        List<Serie> seriesPorCategoria = repository.findByGenero(categoria);
        System.out.println("Sérires da categoria: " + nomeGenero);
        seriesPorCategoria.forEach(System.out::println);
    }

    private void buscarSeriesPorTemporadas() {
        System.out.println("Digite o total de temporadas: ");
        var temporadas = leitura.nextInt();
        System.out.println("Digite a avaliação");
        var avaliacao = leitura.nextDouble();

        List<Serie> seriesEncontradas = repository.seriesPorTemporadaEAvaliacao(temporadas, avaliacao);
        seriesEncontradas.forEach(System.out::println);
    }

    private void buscarEpisodioPorTrecho() {
        System.out.println("Qual o nome do episódio para busca?");
        var trechoEpisodio = leitura.nextLine();
        List<Episodio> episodiosEncontrados = repository.episodiosPorTrecho(trechoEpisodio);
        episodiosEncontrados.forEach( e -> System.out.printf("Série: %s Temporada %s - Episódio %s - %s\n",
                e.getSerie().getTitulo(), e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo()));

    }

    private void buscarTop5EpisodiosPorSerie() {
        buscarSeriePorTitulo();
        if (serieBuscada.isPresent()){
            List<Episodio> topEpisodios = repository.topEpisodiosPorSerie(serieBuscada.get());
            topEpisodios.forEach(e -> System.out.printf("Série: %s Temporada %s - Episódio %s - %s - Avaliacao %.2f\n",
                    e.getSerie().getTitulo(), e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo(), e.getAvaliacao()));
        }
    }


}
