package br.com.alura.screenmatch.model;

public enum ECategoria {

    ACAO("Action"),
    ROMANCE("Romance"),
    COMEDIA("Comedy"),
    DRAMA("Drama"),
    CRIME("Crime");

    private String categoriaOmdb;

    ECategoria(String categoriaOmdb){
        this.categoriaOmdb = categoriaOmdb;
    }

    public static  ECategoria fromString(String text){
        for (ECategoria categoria : ECategoria.values()){
            if (categoria.categoriaOmdb.equalsIgnoreCase(text)){
                return categoria;
            }
        }
        throw new IllegalArgumentException("Nenhuma categoria encontrada para a string fornecida: " + text);
    }
}
