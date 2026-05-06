package com.example.gestortarea;

public class Tarea {

    private int id;
    private String titulo;
    private String descripcion;
    private int estado;

    public Tarea(int id, String titulo, String descripcion, int estado){
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.estado = estado;
    }

    public int getId(){
        return id;
    }

    public String getTitulo(){
        return titulo;
    }

    public String getDescripcion(){
        return descripcion;
    }

    public int getEstado(){
        return estado;
    }
}