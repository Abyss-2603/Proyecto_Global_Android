package es.iesagora.actividad_de_seguimiento;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import es.iesagora.actividad_de_seguimiento.adapter.PendientesAdapter;
import es.iesagora.actividad_de_seguimiento.data.PendientesEntidad;
import es.iesagora.actividad_de_seguimiento.viewModel.PendientesViewModel;

public class PendientesFragment extends Fragment {

    private PendientesViewModel viewModel;
    private PendientesAdapter adapter;

    private RecyclerView recyclerView;
    private LinearLayout layoutVacio;
    private Button btnIrAExplorar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pendientes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerViewPendientes);
        layoutVacio = view.findViewById(R.id.layoutVacio);
        btnIrAExplorar = view.findViewById(R.id.button);

        viewModel = new ViewModelProvider(this).get(PendientesViewModel.class);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new PendientesAdapter(new PendientesAdapter.OnItemClickListener() {
            @Override
            public void onEliminarClick(PendientesEntidad pendiente) {
                viewModel.eliminar(pendiente);
            }

            @Override
            public void onVerDetalleClick(PendientesEntidad pendiente) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("pendiente_db", pendiente);

                NavHostFragment.findNavController(PendientesFragment.this)
                        .navigate(R.id.action_pendientesFragment_to_detallesFragment, bundle);
            }
        });

        recyclerView.setAdapter(adapter);

        viewModel.obtenerPendientes().observe(getViewLifecycleOwner(), lista -> {
            if (lista == null || lista.isEmpty()) {
                layoutVacio.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                layoutVacio.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                adapter.setLista(lista);
            }
        });

        btnIrAExplorar.setOnClickListener(v -> {
            NavHostFragment.findNavController(PendientesFragment.this)
                    .navigate(R.id.explorarFragment);
        });
    }
}