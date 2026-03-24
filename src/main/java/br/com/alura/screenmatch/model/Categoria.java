package br.com.alura.screenmatch.model;

import java.text.Normalizer;

public enum Categoria {
    ACAO("Action", "Ação"),
    ROMANCE("Romance", "Romance"),
    COMEDIA("Comedy", "Comédia"),
    DRAMA("Drama", "Drama"),
    CRIME("Crime", "Crime");

    private String categoriaOmdb;
    private String categoriaPortugues;

    Categoria(String categoriaOmbd, String categoriaPortugues) {
        this.categoriaOmdb = categoriaOmbd;
        this.categoriaPortugues = categoriaPortugues; // Valor padrão, pode ser ajustado conforme necessário
    }

    public static Categoria fromString(String text) {
        for (Categoria categoria : Categoria.values()) {
            if (categoria.categoriaOmdb.equalsIgnoreCase(text)) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Nenhuma categoria encontrada para a série");
    }

    public static Categoria fromPortugues(String text) {
        String textoNormalizado = normalizarTexto(text);
        for (Categoria categoria : Categoria.values()) {
            if (normalizarTexto(categoria.categoriaPortugues).equalsIgnoreCase(textoNormalizado)) {
                return categoria;
            }
        }
        System.out.println("Texto: " + text);
        throw new IllegalArgumentException("Nenhuma categoria encontrada para a série");
    }

    private static String normalizarTexto(String texto) {
        String nfd = Normalizer.normalize(texto, Normalizer.Form.NFD);
        return nfd.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
    }
}
