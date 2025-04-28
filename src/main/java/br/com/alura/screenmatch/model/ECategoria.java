package br.com.alura.screenmatch.model;

public enum ECategoria {

    ACAO("Action", "Ação"),
    ROMANCE("Romance", "Romance"),
    COMEDIA("Comedy", "Comédia"),
    DRAMA("Drama", "Drama"),
    CRIME("Crime", "Crime");

    private String categoriaOmdb;
    private String categoriaPortuges;

    ECategoria(String categoriaOmdb, String categoriaPortuges){
        this.categoriaOmdb = categoriaOmdb;
        this.categoriaPortuges = categoriaPortuges;
    }

    public static  ECategoria fromString(String text){
        for (ECategoria categoria : ECategoria.values()){
            if (categoria.categoriaOmdb.equalsIgnoreCase(text)){
                return categoria;
            }
        }
        throw new IllegalArgumentException("Nenhuma categoria encontrada para a string fornecida: " + text);
    }

    public static  ECategoria fromPortugues(String text){
        for (ECategoria categoria : ECategoria.values()){
            if (categoria.categoriaPortuges.equalsIgnoreCase(text)){
                return categoria;
            }
        }
        throw new IllegalArgumentException("Nenhuma categoria encontrada para a string fornecida: " + text);
    }
}
