package com.example.tfg.adminPanel;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.tfg.R;
import com.example.tfg.adminProfile.AdminProfileActivity;
import com.example.tfg.reportTemp.ReportTempActivity;
import com.example.tfg.route.RouteActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.SubcolumnValue;

public class AdminPanelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        // Grafico de Barras
        GraphView barGraphView = findViewById(R.id.barGraph);
        barGraphView.setTitle("Valoración por tipo de contenedor");
        barGraphView.setBackgroundColor(Color.parseColor("#7a7a7a"));
        barGraphView.getGridLabelRenderer().setNumVerticalLabels(5);
        barGraphView.getGridLabelRenderer().setNumHorizontalLabels(4);

        // Obtener el objeto Viewport
        Viewport viewport = barGraphView.getViewport();

        viewport.setXAxisBoundsManual(true);
        viewport.setYAxisBoundsManual(true);
        viewport.setMinX(0);
        viewport.setMaxX(4);
        viewport.setMinY(0);
        viewport.setMaxY(5);

        // Establecer las etiquetas del eje X
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(barGraphView);
        staticLabelsFormatter.setHorizontalLabels(new String[]{"", "Marron", "Azul", "Amarillo", ""});
        staticLabelsFormatter.setVerticalLabels(new String[]{"0", "1", "2", "3", "4", "5"});
        barGraphView.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);


        // Crear la serie de datos de barras
        BarGraphSeries<DataPoint> barSeries = new BarGraphSeries<>(new DataPoint[]{
                new DataPoint(0, 0), // Marron
                new DataPoint(1, 3.6), // Marron
                new DataPoint(2, 4.5), // Verde
                new DataPoint(3, 2.3)  // Azul
        });
        barSeries.setColor(Color.parseColor("#e1cfff"));
        barSeries.setSpacing(10);
        barGraphView.addSeries(barSeries);

        // Grafico de Puntos
        GraphView pointsGraphView = findViewById(R.id.pointsGraph);
        pointsGraphView.setTitle("Cantidad de contenedores cada 1000 habitantes");
        pointsGraphView.setBackgroundColor(Color.parseColor("#7a7a7a"));

        // Obtener el objeto Viewport
        Viewport pointViewport = pointsGraphView.getViewport();

        pointViewport.setXAxisBoundsManual(true);
        pointViewport.setYAxisBoundsManual(true);
        pointViewport.setMinX(0);
        pointViewport.setMaxX(10);
        pointViewport.setMinY(0);
        pointViewport.setMaxY(10);

        PointsGraphSeries<DataPoint> pointsSeries = new PointsGraphSeries<>(new DataPoint[]{
                new DataPoint(0.5, 1),
                new DataPoint(2.6, 7.5),
                new DataPoint(4, 5),
                new DataPoint(6.3, 4.8),
                new DataPoint(8.2, 6)
        });
        pointsSeries.setColor(Color.parseColor("#e1cfff"));
        pointsGraphView.addSeries(pointsSeries);


        BottomNavigationView navView = findViewById(R.id.adminNavigation);
        navView.setSelectedItemId(R.id.navigation_admin_panel);
        navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.navigation_admin_panel) {
                    return true;
                } else if (id == R.id.navigation_add_temporal) {
                    Intent intent = new Intent(AdminPanelActivity.this, ReportTempActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                } else if (id == R.id.navigation_route) {
                    Intent intent = new Intent(AdminPanelActivity.this, RouteActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                } else if (id == R.id.navigation_admin_profile) {
                    Intent intent = new Intent(AdminPanelActivity.this, AdminProfileActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                }
                return false;
            }
        });
    }

    private List<PointValue> generateLineData() {
        List<PointValue> values = new ArrayList<>();
        // Aquí deberías generar tus datos y añadirlos a la lista de PointValue
        // Por ejemplo:
        values.add(new PointValue(0, 2));
        values.add(new PointValue(1, 4));
        values.add(new PointValue(2, 3));
        values.add(new PointValue(3, 6));
        values.add(new PointValue(4, 8));
        return values;
    }

    private List<SliceValue> generatePieData() {
        List<SliceValue> values = new ArrayList<>();
        // Aquí deberías generar tus datos y añadirlos a la lista de SliceValue
        // Por ejemplo:
        values.add(new SliceValue(25, getResources().getColor(R.color.red)).setLabel("Red"));
        values.add(new SliceValue(20, getResources().getColor(R.color.yellow)).setLabel("Yellow"));
        values.add(new SliceValue(15, getResources().getColor(R.color.blue)).setLabel("Blue"));
        return values;
    }

    private List<Column> generateBarData() {
        List<Column> columns = new ArrayList<>();
        // Aquí deberías generar tus datos y añadirlos a la lista de Column
        // Por ejemplo:
        List<SubcolumnValue> values = new ArrayList<>();
        values.add(new SubcolumnValue(5, ContextCompat.getColor(this, R.color.blue)));
        values.add(new SubcolumnValue(8, ContextCompat.getColor(this, R.color.red)));
        values.add(new SubcolumnValue(3, ContextCompat.getColor(this, R.color.yellow)));

        Column column = new Column(values);
        columns.add(column);
        return columns;
    }
}
