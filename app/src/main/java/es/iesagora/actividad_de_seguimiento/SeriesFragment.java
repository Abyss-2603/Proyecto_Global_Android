package es.iesagora.actividad_de_seguimiento;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.io.Serializable;

import es.iesagora.actividad_de_seguimiento.adapter.SeriesAdapter;
import es.iesagora.actividad_de_seguimiento.data.PendientesEntidad;
import es.iesagora.actividad_de_seguimiento.viewModel.PendientesViewModel;
import es.iesagora.actividad_de_seguimiento.databinding.FragmentSeriesBinding;
import es.iesagora.actividad_de_seguimiento.model.Generos;
import es.iesagora.actividad_de_seguimiento.viewModel.SeriesViewModel;

public class SeriesFragment extends Fragment {

    private FragmentSeriesBinding binding;
    private SeriesViewModel viewModel;
    private PendientesViewModel pendientesViewModel;
    private SeriesAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSeriesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(SeriesViewModel.class);
        pendientesViewModel = new ViewModelProvider(this).get(PendientesViewModel.class);

        adapter = new SeriesAdapter(
                serie -> {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("serie", (Serializable) serie);
                    bundle.putBoolean("Estado_Seleccionado", serie.isSeleccionado());

                    Navigation.findNavController(view).navigate(R.id.action_explorarFragment_to_detallesFragment, bundle);
                },
                (serie, isChecked) -> {
                    if (isChecked) {
                        String generosStr = Generos.getGeneros(serie.getGenreIds());
                        int temps = serie.getRuntime();
                        String infoStr = temps + (temps == 1 ? " temporada" : " temporadas");

                        PendientesEntidad entidad = new PendientesEntidad(
                                serie.getId(),
                                serie.getTitle(),
                                serie.getOverview(),
                                serie.getPosterPath(),
                                serie.getBackdropPath(),
                                "SERIE",
                                infoStr,
                                generosStr
                        );
                        pendientesViewModel.insertar(entidad);
                    } else {
                        pendientesViewModel.eliminarPorIdApi(serie.getId());
                    }
                }
        );

        binding.rvSeries.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvSeries.setAdapter(adapter);

        viewModel.getSeriesResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            switch (resource.status) {
                case LOADING:
                    binding.progressBar.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    binding.progressBar.setVisibility(View.GONE);
                    if (resource.data != null) {
                        adapter.setLista(resource.data);
                    }
                    break;
                case ERROR:
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), resource.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}