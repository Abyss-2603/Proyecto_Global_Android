package es.iesagora.actividad_de_seguimiento;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import es.iesagora.actividad_de_seguimiento.adapter.ActorAdapter;
import es.iesagora.actividad_de_seguimiento.adapter.ComentarioAdapter;
import es.iesagora.actividad_de_seguimiento.api_rest.RetrofitClient;
import es.iesagora.actividad_de_seguimiento.api_rest.TMDB_API;
import es.iesagora.actividad_de_seguimiento.data.PendientesEntidad;
import es.iesagora.actividad_de_seguimiento.databinding.FragmentDetallesBinding;
import es.iesagora.actividad_de_seguimiento.model.Cast;
import es.iesagora.actividad_de_seguimiento.model.Comentario;
import es.iesagora.actividad_de_seguimiento.model.CreditsResponse;
import es.iesagora.actividad_de_seguimiento.model.Generos;
import es.iesagora.actividad_de_seguimiento.model.Peliculas;
import es.iesagora.actividad_de_seguimiento.model.Series;
import es.iesagora.actividad_de_seguimiento.viewModel.AuthViewModel;
import es.iesagora.actividad_de_seguimiento.viewModel.DetallesViewModel;
import es.iesagora.actividad_de_seguimiento.api_rest.Resource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetallesFragment extends Fragment {
    private FragmentDetallesBinding binding;
    private DetallesViewModel viewModel;
    private AuthViewModel authViewModel;
    private ComentarioAdapter adapter;
    private String peliculaId;


    private ActorAdapter actorAdapter;
    private TMDB_API apiService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDetallesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(DetallesViewModel.class);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        apiService = RetrofitClient.getClient().create(TMDB_API.class);

        adapter = new ComentarioAdapter();
        binding.rvComentarios.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvComentarios.setAdapter(adapter);

        // Configuración del carrusel de actores
        actorAdapter = new ActorAdapter(actor -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("actor", actor);

            androidx.navigation.Navigation.findNavController(requireView())
                    .navigate(R.id.action_detallesFragment_to_actorDetallesFragment, bundle);
        });
        binding.rvReparto.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvReparto.setAdapter(actorAdapter);

        if (getArguments() != null) {
            if (getArguments().containsKey("peli")) {
                Peliculas peli = (Peliculas) getArguments().getSerializable("peli");
                configurarPelicula(peli);
                viewModel.fetchTrailer(peli.getId(), true);
                cargarReparto(peli.getId(), true);
            } else if (getArguments().containsKey("serie")) {
                Series serie = (Series) getArguments().getSerializable("serie");
                configurarSerie(serie);
                viewModel.fetchTrailer(serie.getId(), false);
                cargarReparto(serie.getId(), false);
            } else if (getArguments().containsKey("pendiente_db")) {
                PendientesEntidad item = (PendientesEntidad) getArguments().getSerializable("pendiente_db");
                configurarDesdePendiente(item);

                boolean esPeli = "PELICULA".equalsIgnoreCase(item.getTipo());
                viewModel.fetchTrailer(item.getIdAPI(), esPeli);
                cargarReparto(item.getIdAPI(), esPeli);
            }
        }

        binding.btnEnviarComentario.setOnClickListener(v -> {
            String mensaje = binding.etNuevoComentario.getText().toString().trim();
            if (!mensaje.isEmpty()) {
                guardarComentario(mensaje);
            }
        });

        viewModel.getTrailerKey().observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == Resource.Status.SUCCESS) {
                binding.btnVerTrailer.setOnClickListener(v -> abrirYoutube(resource.data));
            }
        });
    }


    // Cargar los 10 actores principales de la obra
    private void cargarReparto(int id, boolean esPeli) {
        Call<CreditsResponse> call = esPeli ? apiService.getMovieCredits(id) : apiService.getSerieCredits(id);

        call.enqueue(new Callback<CreditsResponse>() {
            @Override
            public void onResponse(Call<CreditsResponse> call, Response<CreditsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Cast> cast = response.body().getCast();
                    if (cast != null && !cast.isEmpty()) {
                        List<Cast> top10 = cast.subList(0, Math.min(cast.size(), 10));
                        actorAdapter.setActores(top10);
                    }
                }
            }

            @Override
            public void onFailure(Call<CreditsResponse> call, Throwable t) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Error")
                        .setMessage("No se ha podido cargar el reparto de actores en este momento.")
                        .setPositiveButton("Aceptar", null)
                        .show();
            }
        });
    }

    private void guardarComentario(String mensaje) {
        com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            String nombre = user.getDisplayName() != null ? user.getDisplayName() : "Usuario";
            String fotoUrl = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "https://beofqesolwccnygzxryf.supabase.co/storage/v1/object/public/recuerdos/avatar_defecto.png";

            Comentario nuevo = new Comentario(uid, nombre, mensaje, fotoUrl, com.google.firebase.firestore.FieldValue.serverTimestamp());

            FirebaseFirestore.getInstance()
                    .collection("multimedia").document(peliculaId)
                    .collection("comments")
                    .add(nuevo)
                    .addOnSuccessListener(ref -> binding.etNuevoComentario.setText(""));
        }
    }

    private void escucharComentarios() {
        FirebaseFirestore.getInstance()
                .collection("multimedia").document(peliculaId)
                .collection("comments")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        List<Comentario> lista = value.toObjects(Comentario.class);
                        adapter.setComentarios(lista);
                        binding.lblComentarios.setText("Comentarios (" + lista.size() + ")");
                        if (lista.isEmpty()) {
                            binding.containerSinComentarios.setVisibility(View.VISIBLE);
                            binding.rvComentarios.setVisibility(View.GONE);
                        } else {
                            binding.containerSinComentarios.setVisibility(View.GONE);
                            binding.rvComentarios.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void configurarDesdePendiente(PendientesEntidad item) {
        this.peliculaId = String.valueOf(item.getIdAPI());
        binding.tvDetalleTitulo.setText(item.getTitulo());
        binding.tvSinopsis.setText(item.getDescripcion());
        binding.tvDetalleSubtituloHeader.setText(item.getInfo());
        binding.tvInfoMetrica.setText(item.getInfo());
        boolean esPeli = "PELICULA".equalsIgnoreCase(item.getTipo());
        binding.tvInfoTipo.setText(esPeli ? "Película" : "Serie de TV");
        binding.lblMetrica.setText(esPeli ? "Duración" : "Temporadas");
        Glide.with(this).load("https://image.tmdb.org/t/p/w780" + item.getPosterPath()).into(binding.ivDetallePoster);
        binding.cgGeneros.removeAllViews();
        Chip chip = new Chip(getContext());
        chip.setText(item.getGeneros());
        binding.cgGeneros.addView(chip);
        escucharComentarios();
    }

    private void configurarPelicula(Peliculas peli) {
        this.peliculaId = String.valueOf(peli.getId());
        binding.tvDetalleTitulo.setText(peli.getTitle());
        binding.tvSinopsis.setText(peli.getOverview());
        String duracion = (peli.getRuntime() > 0) ? peli.getRuntime() + " min" : "2 h 49 min";
        binding.tvDetalleSubtituloHeader.setText(duracion);
        binding.tvInfoTipo.setText("Película");
        binding.lblMetrica.setText("Duración");
        binding.tvInfoMetrica.setText(duracion);
        cargarImagenYGeneros(peli.getPosterPath(), peli.getGenreIds());
        escucharComentarios();
    }

    private void configurarSerie(Series serie) {
        this.peliculaId = String.valueOf(serie.getId());
        binding.tvDetalleTitulo.setText(serie.getTitle());
        binding.tvSinopsis.setText(serie.getOverview());
        int temporadas = (serie.getNumberOfSeasons() > 0) ? serie.getNumberOfSeasons() : 4;
        binding.tvDetalleSubtituloHeader.setText(temporadas + " temporadas • Netflix");
        binding.tvInfoTipo.setText("Serie de TV");
        binding.lblMetrica.setText("Temporadas");
        binding.tvInfoMetrica.setText(String.valueOf(temporadas));
        cargarImagenYGeneros(serie.getPosterPath(), serie.getGenreIds());
        escucharComentarios();
    }

    private void cargarImagenYGeneros(String path, List<Integer> ids) {
        Glide.with(this).load("https://image.tmdb.org/t/p/w780" + path).into(binding.ivDetallePoster);
        binding.cgGeneros.removeAllViews();
        List<String> nombresGeneros = Generos.getGenerosList(ids);
        for (String nombre : nombresGeneros) {
            Chip chip = new Chip(getContext());
            chip.setText(nombre);
            chip.setChipBackgroundColorResource(android.R.color.transparent);
            chip.setChipStrokeWidth(2f);
            chip.setChipStrokeColorResource(android.R.color.darker_gray);
            binding.cgGeneros.addView(chip);
        }
    }

    private void abrirYoutube(String key) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + key));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }
}