package es.iesagora.actividad_de_seguimiento;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.Query;

import java.util.Calendar;

import es.iesagora.actividad_de_seguimiento.adapter.SeguimientoAdapter;
import es.iesagora.actividad_de_seguimiento.data.SeguimientoEntidad;
import es.iesagora.actividad_de_seguimiento.viewModel.SeguimientoViewModel;

public class SeguimientoFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayout layoutVacio;
    private FloatingActionButton fabAdd;
    private EditText etBuscar;
    private Button btnFiltros, btnOrdenar;

    private SeguimientoViewModel viewModel;
    private SeguimientoAdapter adapter;

    private String queryTitulo = "";
    private String ordenCampo = "fecha";
    private Query.Direction ordenDir = Query.Direction.DESCENDING;
    private Float filtroPuntMin = null;
    private Float filtroPuntMax = null;
    private String filtroFechaIni = null;
    private String filtroFechaFin = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_seguimiento, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rvSeguimiento);
        layoutVacio = view.findViewById(R.id.layoutVacioSeguimiento);
        fabAdd = view.findViewById(R.id.fabAdd);
        etBuscar = view.findViewById(R.id.etBuscarSeguimiento);
        btnFiltros = view.findViewById(R.id.btnFiltros);
        btnOrdenar = view.findViewById(R.id.btnOrdenar);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new SeguimientoAdapter(seguimiento -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("seguimiento", seguimiento);
            Navigation.findNavController(view).navigate(R.id.action_seguimientoFragment_to_detalleSeguimientoFragment, bundle);
        });
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(SeguimientoViewModel.class);

        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                queryTitulo = s.toString();
                aplicarFiltrosBD();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnOrdenar.setOnClickListener(v -> mostrarMenuOrdenar());
        btnFiltros.setOnClickListener(v -> mostrarMenuFiltros());

        fabAdd.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_seguimientoFragment_to_anadirSeguimientoFragment);
        });

        aplicarFiltrosBD();
    }

    private void aplicarFiltrosBD() {
        viewModel.obtenerSeguimientosFiltrados(queryTitulo, filtroPuntMin, filtroPuntMax, filtroFechaIni, filtroFechaFin, ordenCampo, ordenDir)
                .observe(getViewLifecycleOwner(), lista -> {
                    if (lista == null || lista.isEmpty()) {
                        layoutVacio.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);

                        if (!queryTitulo.isEmpty() || filtroPuntMin != null || filtroFechaIni != null) {
                            mostrarAlerta("Sin resultados", "No se encontraron seguimientos que coincidan con los filtros aplicados.");
                        }
                    } else {
                        layoutVacio.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        adapter.setLista(lista);
                    }
                });
    }

    private void mostrarMenuOrdenar() {
        com.google.android.material.bottomsheet.BottomSheetDialog dialog =
                new com.google.android.material.bottomsheet.BottomSheetDialog(requireContext());
        View v = getLayoutInflater().inflate(R.layout.layout_menu_ordenar, null);

        v.findViewById(R.id.btnOrdenReciente).setOnClickListener(view -> {
            ordenCampo = "fecha";
            ordenDir = com.google.firebase.firestore.Query.Direction.DESCENDING;
            aplicarFiltrosBD();
            dialog.dismiss();
        });

        v.findViewById(R.id.btnOrdenAntigua).setOnClickListener(view -> {
            ordenCampo = "fecha";
            ordenDir = com.google.firebase.firestore.Query.Direction.ASCENDING;
            aplicarFiltrosBD();
            dialog.dismiss();
        });

        v.findViewById(R.id.btnOrdenPuntAlta).setOnClickListener(view -> {
            ordenCampo = "puntuacion";
            ordenDir = com.google.firebase.firestore.Query.Direction.DESCENDING;
            aplicarFiltrosBD();
            dialog.dismiss();
        });

        v.findViewById(R.id.btnOrdenPuntBaja).setOnClickListener(view -> {
            ordenCampo = "puntuacion";
            ordenDir = com.google.firebase.firestore.Query.Direction.ASCENDING;
            aplicarFiltrosBD();
            dialog.dismiss();
        });

        dialog.setContentView(v);
        dialog.show();
    }

    private void mostrarMenuFiltros() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View v = getLayoutInflater().inflate(R.layout.layout_menu_filtros, null);

        EditText etIni = v.findViewById(R.id.etFechaInicio);
        EditText etFin = v.findViewById(R.id.etFechaFin);
        Spinner spinMin = v.findViewById(R.id.spinnerPuntMin);
        Spinner spinMax = v.findViewById(R.id.spinnerPuntMax);
        Button btnLimpiar = v.findViewById(R.id.btnLimpiarFiltros);
        Button btnAplicar = v.findViewById(R.id.btnAplicarFiltros);

        String[] opcionesPuntuacion = {"0", "1", "2", "3", "4", "5"};
        ArrayAdapter<String> spinAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, opcionesPuntuacion);
        spinMin.setAdapter(spinAdapter);
        spinMax.setAdapter(spinAdapter);

        if (filtroPuntMin != null) spinMin.setSelection(Math.round(filtroPuntMin));
        else spinMin.setSelection(0);

        if (filtroPuntMax != null) spinMax.setSelection(Math.round(filtroPuntMax));
        else spinMax.setSelection(5);

        if (filtroFechaIni != null) etIni.setText(filtroFechaIni);
        if (filtroFechaFin != null) etFin.setText(filtroFechaFin);

        etIni.setOnClickListener(view -> mostrarCalendario(etIni));
        etFin.setOnClickListener(view -> mostrarCalendario(etFin));

        btnLimpiar.setOnClickListener(view -> {
            etIni.setText("");
            etFin.setText("");
            spinMin.setSelection(0);
            spinMax.setSelection(5);
        });

        btnAplicar.setOnClickListener(view -> {
            float min = Float.parseFloat(spinMin.getSelectedItem().toString());
            float max = Float.parseFloat(spinMax.getSelectedItem().toString());

            if (min > max) {
                mostrarAlerta("Error de validación", "La puntuación mínima no puede ser mayor a la máxima.");
                return;
            }

            filtroPuntMin = min;
            filtroPuntMax = max;
            filtroFechaIni = etIni.getText().toString().isEmpty() ? null : etIni.getText().toString();
            filtroFechaFin = etFin.getText().toString().isEmpty() ? null : etFin.getText().toString();

            aplicarFiltrosBD();
            dialog.dismiss();
        });

        dialog.setContentView(v);
        dialog.show();
    }

    private void mostrarCalendario(EditText editText) {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(requireContext(), (picker, y, m, d) -> {
            String dia = d < 10 ? "0" + d : String.valueOf(d);
            String mes = (m + 1) < 10 ? "0" + (m + 1) : String.valueOf(m + 1);
            editText.setText(dia + "/" + mes + "/" + y);
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        if (getContext() != null) {
            new AlertDialog.Builder(requireContext())
                    .setTitle(titulo)
                    .setMessage(mensaje)
                    .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss())
                    .show();
        }
    }
}