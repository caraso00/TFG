package com.example.tfg.binDetails;

import java.io.Serializable;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BinDetails that = (BinDetails) o;
        return Objects.equals(titulo, that.titulo) &&
                Objects.equals(ubicacion, that.ubicacion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(titulo, ubicacion);
    }
}
