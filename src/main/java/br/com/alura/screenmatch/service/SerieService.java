package br.com.alura.screenmatch.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.alura.screenmatch.dto.EpisodioDTO;
import br.com.alura.screenmatch.dto.SerieDTO;
import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;

@Service
public class SerieService {
    @Autowired
    private SerieRepository serieRepository;

    public List<SerieDTO> obterTodasSeries() {
        return converteDados(serieRepository.findAll());
    }

    public List<SerieDTO> obterTop5Series() {
        return converteDados(serieRepository.findTop5ByOrderByAvaliacaoDesc());
    }

    private List<SerieDTO> converteDados(List<Serie> series) {
        return series.stream()
                .map(s -> new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getAvaliacao(),
                        s.getGenero(), s.getAtores(), s.getPoster(), s.getSinopse()))
                .collect(Collectors.toList());
    }

    public List<SerieDTO> obterLancamentos() {
        return converteDados(serieRepository.encontrarEpisodiosMaisRecentes());
    }

    public SerieDTO obterSeriePorId(Long id) {
        Optional<Serie> serieOpt = serieRepository.findById(id);
        if (serieOpt.isPresent()) {
            Serie s = serieOpt.get();
            return new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getAvaliacao(),
                    s.getGenero(), s.getAtores(), s.getPoster(), s.getSinopse());
        }
        return null;
    }

    public List<EpisodioDTO> obterTodasTemporadas(Long id) {
        Optional<Serie> serieOpt = serieRepository.findById(id);
        if (serieOpt.isPresent()) {
            Serie s = serieOpt.get();
            return s.getEpisodios().stream()
                    .map(e -> new EpisodioDTO(e.getNumeroEpisodio(), e.getTemporada(), e.getTitulo()))
                    .collect(Collectors.toList());
        }
        return null;
    }

    public List<EpisodioDTO> obterTemporadasPorNumero(Long id, Long temporada) {
        return converteDadosEpisodio(serieRepository.obterEpisodiosPorTemporada(id, temporada));
    }

    public List<SerieDTO> obterSeriesPorCategoria(String categoria) {
        Categoria cat = Categoria.fromPortugues(categoria);
        return converteDados(serieRepository.findByGenero(cat));
    }

    public List<EpisodioDTO> obterTop5EpisodiosPorSerie(Long id) {
        Optional<Serie> serie = serieRepository.findById(id);
        if(serie.isPresent()) {
            return converteDadosEpisodio(serieRepository.topEpisodiosPorSerie(serie.get()));
        }
        return null;
    }

    private List<EpisodioDTO> converteDadosEpisodio(List<Episodio> episodios) {
        return episodios.stream()
            .map(e -> new EpisodioDTO(e.getNumeroEpisodio(), e.getTemporada(), e.getTitulo()))
            .collect(Collectors.toList());   
    }

}
