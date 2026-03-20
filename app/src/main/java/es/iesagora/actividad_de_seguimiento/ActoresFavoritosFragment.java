package es.iesagora.actividad_de_seguimiento;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import es.iesagora.actividad_de_seguimiento.adapter.ActorAdapter;
import es.iesagora.actividad_de_seguimiento.model.Cast;

public class ActoresFavoritosFragment extends Fragment {

    private RecyclerView rvActores;
    private TextView tvSinFavoritos;
    private ActorAdapter actorAdapter;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_actores_favoritos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvActores = view.findViewById(R.id.rvActoresFavoritos);
        tvSinFavoritos = view.findViewById(R.id.tvSinFavoritos);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        actorAdapter = new ActorAdapter(actor -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("actor", actor);
            Navigation.findNavController(view).navigate(R.id.action_actoresFavoritosFragment_to_actorDetallesFragment, bundle);
        });

        rvActores.setLayoutManager(new GridLayoutManager(getContext(), 3));
        rvActores.setAdapter(actorAdapter);

        cargarActoresFavoritos();
    }

    private void cargarActoresFavoritos() {
        if (mAuth.getCurrentUser() == null) {
            tvSinFavoritos.setVisibility(View.VISIBLE);
            return;
        }

        String uid = mAuth.getCurrentUser().getUid();

        db.collection("users").document(uid).collection("actores_favoritos")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;

                    List<Cast> listaActores = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : value) {
                        Cast actor = new Cast();
                        if (doc.contains("id")) actor.setId(doc.getLong("id").intValue());
                        if (doc.contains("name")) actor.setName(doc.getString("name"));
                        if (doc.contains("profilePath")) actor.setProfilePath(doc.getString("profilePath"));

                        listaActores.add(actor);
                    }

                    actorAdapter.setActores(listaActores);

                    if (listaActores.isEmpty()) {
                        tvSinFavoritos.setVisibility(View.VISIBLE);
                        rvActores.setVisibility(View.GONE);
                    } else {
                        tvSinFavoritos.setVisibility(View.GONE);
                        rvActores.setVisibility(View.VISIBLE);
                    }
                });
    }
}