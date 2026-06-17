package com.example.gestortarea;

public class Tarea {

    private String id;
    private int numero;
    private String titulo;
    private String descripcion;
    private int estado;

    public Tarea() {

    }

    public Tarea(String id, int numero, String titulo, String descripcion, int estado) {
        this.id = id;
        this.numero = numero;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.estado = estado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo){
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion){
        this.descripcion = descripcion;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado){
        this.estado = estado;
    }
}