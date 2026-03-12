package es.iesagora.actividad_de_seguimiento.repository;

import androidx.lifecycle.MutableLiveData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.List;
import es.iesagora.actividad_de_seguimiento.data.SeguimientoEntidad;

public class SeguimientoRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    public SeguimientoRepository() {}

    private CollectionReference getColeccion() {
        String uid = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "anonimo";
        return db.collection("users").document(uid).collection("seguimiento");
    }

    public void insertar(SeguimientoEntidad seguimiento) {
        getColeccion().add(seguimiento);
    }

    public MutableLiveData<List<SeguimientoEntidad>> obtenerTodos() {
        MutableLiveData<List<SeguimientoEntidad>> liveData = new MutableLiveData<>();

        getColeccion().orderBy("fecha", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        liveData.setValue(value.toObjects(SeguimientoEntidad.class));
                    }
                });
        return liveData;
    }
}