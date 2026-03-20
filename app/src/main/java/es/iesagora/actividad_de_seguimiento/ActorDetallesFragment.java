package es.iesagora.actividad_de_seguimiento;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.iesagora.actividad_de_seguimiento.adapter.FilmografiaAdapter;
import es.iesagora.actividad_de_seguimiento.api_rest.RetrofitClient;
import es.iesagora.actividad_de_seguimiento.api_rest.TMDB_API;
import es.iesagora.actividad_de_seguimiento.databinding.FragmentActorDetallesBinding;
import es.iesagora.actividad_de_seguimiento.model.ActorDetails;
import es.iesagora.actividad_de_seguimiento.model.Cast;
import es.iesagora.actividad_de_seguimiento.model.CombinedCreditsResponse;
import es.iesagora.actividad_de_seguimiento.model.CombinedCreditsResponse.CombinedCredit;
import es.iesagora.actividad_de_seguimiento.model.Peliculas;
import es.iesagora.actividad_de_seguimiento.model.Series;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActorDetallesFragment extends Fragment {

    private FragmentActorDetallesBinding binding;
    private TMDB_API apiService;
    private FilmografiaAdapter filmografiaAdapter;
    private int actorId;


    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private boolean isSiguiendo = false;
    private Cast actorActual;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentActorDetallesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = RetrofitClient.getClient().create(TMDB_API.class);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        filmografiaAdapter = new FilmografiaAdapter(obra -> {
            if ("movie".equals(obra.getMediaType())) {
                obtenerPeliYNavegar(obra.getId());
            } else if ("tv".equals(obra.getMediaType())) {
                obtenerSerieYNavegar(obra.getId());
            }
        });
        binding.rvActorFilmografia.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvActorFilmografia.setAdapter(filmografiaAdapter);


        if (getArguments() != null && getArguments().containsKey("actor")) {
            actorActual = (Cast) getArguments().getSerializable("actor");
            if (actorActual != null) {
                actorId = actorActual.getId();
                binding.tvActorDetalleNombre.setText(actorActual.getName());
                Glide.with(this)
                        .load("https://image.tmdb.org/t/p/w342" + actorActual.getProfilePath())
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .into(binding.ivActorDetalleFoto);

                comprobarEstadoSeguimiento();
                cargarInfoBiografica();
                cargarFilmografia();
            }
        }

        binding.btnSeguirActor.setOnClickListener(v -> toggleSeguirActor());
    }


    private void comprobarEstadoSeguimiento() {
        if (mAuth.getCurrentUser() == null) return;

        String uid = mAuth.getCurrentUser().getUid();
        db.collection("users").document(uid).collection("actores_favoritos").document(String.valueOf(actorId))
                .addSnapshotListener((documentSnapshot, error) -> {
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        isSiguiendo = true;
                        binding.btnSeguirActor.setImageResource(R.drawable.estrella);
                    } else {
                        isSiguiendo = false;
                        binding.btnSeguirActor.setImageResource(R.drawable.estrella_vacia);
                    }
                });
    }

    private void toggleSeguirActor() {
        if (mAuth.getCurrentUser() == null) {
            mostrarError("Debes iniciar sesión para seguir a un actor.");
            return;
        }

        String uid = mAuth.getCurrentUser().getUid();
        DocumentReference docRef = db.collection("users").document(uid)
                .collection("actores_favoritos").document(String.valueOf(actorId));

        if (isSiguiendo) {
            docRef.delete();
        } else {
            Map<String, Object> actorData = new HashMap<>();
            actorData.put("id", actorActual.getId());
            actorData.put("name", actorActual.getName());
            actorData.put("profilePath", actorActual.getProfilePath());
            actorData.put("timestamp", FieldValue.serverTimestamp());

            docRef.set(actorData);
        }
    }


    private void cargarInfoBiografica() {
        apiService.getActorDetails(actorId).enqueue(new Callback<ActorDetails>() {
            @Override
            public void onResponse(Call<ActorDetails> call, Response<ActorDetails> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ActorDetails details = response.body();

                    binding.tvActorBiografia.setText(details.getBiography().isEmpty() ?
                            "Biografía no disponible." : details.getBiography());
                    binding.tvActorNacimiento.setText(details.getBirthday() != null ?
                            details.getBirthday() : "Desconocido");
                    binding.tvActorLugarNacimiento.setText(details.getPlaceOfBirth() != null ?
                            details.getPlaceOfBirth() : "Desconocido");
                }
            }

            @Override
            public void onFailure(Call<ActorDetails> call, Throwable t) {
                mostrarError("No se ha podido cargar la biografía.");
            }
        });
    }

    private void cargarFilmografia() {
        apiService.getCombinedCredits(actorId).enqueue(new Callback<CombinedCreditsResponse>() {
            @Override
            public void onResponse(Call<CombinedCreditsResponse> call, Response<CombinedCreditsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CombinedCredit> lista = response.body().getCast();
                    if (lista != null) {
                        lista.sort((o1, o2) -> Double.compare(o2.getVoteAverage(), o1.getVoteAverage()));
                        filmografiaAdapter.setObras(lista.subList(0, Math.min(lista.size(), 15)));
                    }
                }
            }

            @Override
            public void onFailure(Call<CombinedCreditsResponse> call, Throwable t) {
                mostrarError("No se ha podido cargar la filmografía.");
            }
        });
    }

    private void obtenerPeliYNavegar(int id) {
        apiService.getMovieDetails(id).enqueue(new Callback<Peliculas>() {
            @Override
            public void onResponse(Call<Peliculas> call, Response<Peliculas> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("peli", response.body());
                    Navigation.findNavController(requireView())
                            .navigate(R.id.action_actorDetallesFragment_to_detallesFragment, bundle);
                }
            }
            @Override
            public void onFailure(Call<Peliculas> call, Throwable t) {
                mostrarError("No se pudieron obtener los detalles de la película.");
            }
        });
    }

    private void obtenerSerieYNavegar(int id) {
        apiService.getSerieDetails(id).enqueue(new Callback<Series>() {
            @Override
            public void onResponse(Call<Series> call, Response<Series> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("serie", response.body());
                    Navigation.findNavController(requireView())
                            .navigate(R.id.action_actorDetallesFragment_to_detallesFragment, bundle);
                }
            }
            @Override
            public void onFailure(Call<Series> call, Throwable t) {
                mostrarError("No se pudieron obtener los detalles de la serie.");
            }
        });
    }

    private void mostrarError(String mensaje) {
        if (getContext() != null) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Error de carga")
                    .setMessage(mensaje)
                    .setPositiveButton("Aceptar", null)
                    .show();
        }
    }
}