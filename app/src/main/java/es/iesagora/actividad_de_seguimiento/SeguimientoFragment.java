package es.iesagora.actividad_de_seguimiento;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

// IMPORTANTE: Importa tu adaptador y tu entidad
import es.iesagora.actividad_de_seguimiento.adapter.SeguimientoAdapter;
import es.iesagora.actividad_de_seguimiento.data.SeguimientoEntidad;
import es.iesagora.actividad_de_seguimiento.viewModel.SeguimientoViewModel;

public class SeguimientoFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayout layoutVacio;
    private FloatingActionButton fabAdd;

    private SeguimientoViewModel viewModel;
    private SeguimientoAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_seguimiento, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rvSeguimiento);
        layoutVacio = view.findViewById(R.id.layoutVacioSeguimiento);
        fabAdd = view.findViewById(R.id.fabAdd);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new SeguimientoAdapter(new SeguimientoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(SeguimientoEntidad seguimiento) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("seguimiento", seguimiento);

                Navigation.findNavController(view).navigate(R.id.action_seguimientoFragment_to_detalleSeguimientoFragment, bundle);
            }
        });

        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(SeguimientoViewModel.class);

        viewModel.obtenerSeguimientos().observe(getViewLifecycleOwner(), lista -> {
            if (lista == null || lista.isEmpty()) {
                layoutVacio.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                layoutVacio.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                adapter.setLista(lista);
            }
        });

        fabAdd.setOnClickListener(v -> {
            Navigation.findNavController(view)
                    .navigate(R.id.action_seguimientoFragment_to_anadirSeguimientoFragment);
        });
    }
}