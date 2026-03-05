package es.iesagora.actividad_de_seguimiento;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
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

import java.util.ArrayList;
import java.util.List;

import es.iesagora.actividad_de_seguimiento.adapter.PelisAdapter;
import es.iesagora.actividad_de_seguimiento.api_rest.RetrofitClient;
import es.iesagora.actividad_de_seguimiento.api_rest.TMDB_API;
import es.iesagora.actividad_de_seguimiento.data.PendientesEntidad;
import es.iesagora.actividad_de_seguimiento.repository.PreferencesRepository;
import es.iesagora.actividad_de_seguimiento.viewModel.PendientesViewModel;
import es.iesagora.actividad_de_seguimiento.databinding.FragmentPelisBinding;
import es.iesagora.actividad_de_seguimiento.model.Generos;
import es.iesagora.actividad_de_seguimiento.model.Peliculas;
import es.iesagora.actividad_de_seguimiento.viewModel.PelisViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PelisFragment extends Fragment {
    private FragmentPelisBinding binding;
    private PelisViewModel viewModel;
    private PelisAdapter adapter;
    private PendientesViewModel pendientesViewModel;

    private List<Peliculas> listaApi = new ArrayList<>();
    private List<PendientesEntidad> listaDb = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPelisBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pendientesViewModel = new ViewModelProvider(this).get(PendientesViewModel.class);
        viewModel = new ViewModelProvider(this).get(PelisViewModel.class);

        TMDB_API apiService = RetrofitClient.getClient().create(TMDB_API.class);

        adapter = new PelisAdapter(
                peli -> {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("peli", peli);
                    bundle.putBoolean("Estado_Seleccionado", peli.isSeleccionado());
                    Navigation.findNavController(view).navigate(R.id.action_explorarFragment_to_detallesFragment, bundle);
                },
                (peli, isChecked) -> {
                    if (isChecked) {
                        apiService.getMovieDetails(peli.getId()).enqueue(new Callback<Peliculas>() {
                            @Override
                            public void onResponse(Call<Peliculas> call, Response<Peliculas> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    Peliculas detalleCompleto = response.body();
                                    int runtime = detalleCompleto.getRuntime();

                                    int horas = runtime / 60;
                                    int min = runtime % 60;
                                    String duracionReal = horas + " h " + min + " min";

                                    String generosStr = Generos.getGeneros(peli.getGenreIds());

                                    PendientesEntidad entidad = new PendientesEntidad(
                                            peli.getId(),
                                            peli.getTitle(),
                                            peli.getOverview(),
                                            peli.getPosterPath(),
                                            peli.getBackdropPath(),
                                            "PELICULA",
                                            duracionReal,
                                            generosStr
                                    );
                                    pendientesViewModel.insertar(entidad);
                                }
                            }

                            @Override
                            public void onFailure(Call<Peliculas> call, Throwable t) {
                                String generosStr = Generos.getGeneros(peli.getGenreIds());

                                PendientesEntidad entidad = new PendientesEntidad(
                                        peli.getId(),
                                        peli.getTitle(),
                                        peli.getOverview(),
                                        peli.getPosterPath(),
                                        peli.getBackdropPath(),
                                        "PELICULA",
                                        "Duración no disponible",
                                        generosStr
                                );

                                pendientesViewModel.insertar(entidad);

                                Toast.makeText(getContext(), "Guardada (sin detalles de duración)", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        pendientesViewModel.eliminarPorIdApi(peli.getId());
                    }
                }
        );

        PreferencesRepository prefs = new PreferencesRepository(requireContext());
        boolean soloWifi = prefs.isWifiOnly();
        boolean hayWifi = false;

        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkCapabilities nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
            if (nc != null && nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                hayWifi = true;
            }
        }

        if (soloWifi && !hayWifi) {
            adapter.setPuedeCargarImagenes(false);
        } else {
            adapter.setPuedeCargarImagenes(true);
        }

        binding.rvPelis.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvPelis.setAdapter(adapter);
        viewModel.getPelisResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            switch (resource.status) {
                case LOADING:
                    binding.progressBar.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    binding.progressBar.setVisibility(View.GONE);
                    if (resource.data != null) {
                        listaApi = resource.data;
                        combinarListas();
                    }
                    break;
                case ERROR:
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), resource.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        pendientesViewModel.obtenerPendientes().observe(getViewLifecycleOwner(), lista -> {
            listaDb = lista;
            combinarListas();
        });
    }

    private void combinarListas() {
        if (listaApi == null || listaApi.isEmpty()) return;

        for (Peliculas peliApi : listaApi) {
            peliApi.setSeleccionado(false);

            if (listaDb != null) {
                for (PendientesEntidad pendienteDb : listaDb) {
                    if (pendienteDb.getIdAPI() == peliApi.getId() && "PELICULA".equals(pendienteDb.getTipo())) {
                        peliApi.setSeleccionado(true);
                        break;
                    }
                }
            }
        }

        adapter.setList(listaApi);
    }
}