package com.example.tfg.binDetails;

import java.io.Serializable;

public class BinDetails implements Serializable {

    private String titulo;
    private String ubicacion;
    private String tipo;
    private String estado;
    private int imagenResId;


    public BinDetails(String titulo, String ubicacion, String tipo, String estado, int imagenResId) {
        this.titulo = titulo;
        this.ubicacion = ubicacion;
        this.tipo = tipo;
        this.estado = estado;
        this.imagenResId = imagenResId;
    }

    // Getters
    public String getTitulo() {
        return titulo;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public String getTipo() {
        return tipo;
    }

    public String getEstado() {
        return estado;
    }

    public int getImagenResId() {
        return imagenResId;
    }
}
