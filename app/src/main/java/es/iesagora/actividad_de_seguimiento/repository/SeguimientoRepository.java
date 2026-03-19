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

    public MutableLiveData<List<SeguimientoEntidad>> obtenerFiltrados(String tipo) {
        MutableLiveData<List<SeguimientoEntidad>> liveData = new MutableLiveData<>();

        if (tipo.equals("TODOS")) {
            return obtenerTodos();
        }

        getColeccion().whereEqualTo("tipo", tipo)
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        liveData.setValue(value.toObjects(SeguimientoEntidad.class));
                    }
                });
        return liveData;
    }

    public MutableLiveData<List<SeguimientoEntidad>> obtenerConFiltrosBD(
            String tituloBusqueda, Float minPunt, Float maxPunt,
            String fechaIni, String fechaFin,
            String campoOrden, com.google.firebase.firestore.Query.Direction direccion) {

        MutableLiveData<List<SeguimientoEntidad>> liveData = new MutableLiveData<>();
        com.google.firebase.firestore.Query query = getColeccion();

        if (tituloBusqueda != null && !tituloBusqueda.isEmpty()) {
            query = query.whereGreaterThanOrEqualTo("titulo", tituloBusqueda)
                    .whereLessThanOrEqualTo("titulo", tituloBusqueda + "\uf8ff");
        }

        if (minPunt != null) {
            query = query.whereGreaterThanOrEqualTo("puntuacion", minPunt);
        }
        if (maxPunt != null) {
            query = query.whereLessThanOrEqualTo("puntuacion", maxPunt);
        }

        if (fechaIni != null) {
            query = query.whereGreaterThanOrEqualTo("fecha", fechaIni);
        }
        if (fechaFin != null) {
            query = query.whereLessThanOrEqualTo("fecha", fechaFin);
        }

        query = query.orderBy(campoOrden, direccion);

        query.addSnapshotListener((value, error) -> {
            if (error != null) {
                android.util.Log.e("FIRESTORE_FILTRO", "Error o falta de índice: ", error);
                liveData.setValue(null);
                return;
            }

            if (value != null) {
                liveData.setValue(value.toObjects(SeguimientoEntidad.class));
            }
        });

        return liveData;
    }

}