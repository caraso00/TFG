package com.example.tfg.reportAdd;

import android.graphics.drawable.Drawable;

import androidx.lifecycle.ViewModel;

public class ReportViewModel extends ViewModel {

    // Aquí, puedes agregar un LiveData para la ubicación si lo necesitas.

    public boolean validateForm(String tipo, String estado, Drawable foto) {
        // Asegúrate de validar la ubicación correctamente si decides agregarla.
        return tipo != null && !tipo.isEmpty() &&
                estado != null && !estado.isEmpty() &&
                foto != null;
    }
}