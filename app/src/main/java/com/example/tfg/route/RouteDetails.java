package com.example.tfg.route;

import com.example.tfg.binDetails.BinDetails;

import java.io.Serializable;
import java.util.List;

public class RouteDetails implements Serializable {
    private String titulo;
    private List<BinDetails> binDetailsList;

    public RouteDetails(String titulo, List<BinDetails> binDetailsList) {
        this.titulo = titulo;
        this.binDetailsList = binDetailsList;
    }

    // Getter and Setter for titulo
    public String getTitulo() {
        return titulo;
    }

    // Getter and Setter for binDetailsList
    public List<BinDetails> getBinDetailsList() {
        return binDetailsList;
    }
}

