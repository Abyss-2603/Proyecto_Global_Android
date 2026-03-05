package es.iesagora.actividad_de_seguimiento.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Random;

import es.iesagora.actividad_de_seguimiento.data.PendientesEntidad;

public class PendientesRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String uid;

    public PendientesRepository() {
        this.uid = FirebaseAuth.getInstance().getUid();
    }

    public void insertar(PendientesEntidad p) {
        if (uid == null) return;
        db.collection("users").document(uid)
                .collection("pendientes").document(String.valueOf(p.getIdAPI()))
                .set(p);
    }

    public void eliminarPorIdApi(int idApi) {
        if (uid == null) return;
        db.collection("users").document(uid)
                .collection("pendientes").document(String.valueOf(idApi))
                .delete();
    }

    public MutableLiveData<List<PendientesEntidad>> obtenerTodo() {
        MutableLiveData<List<PendientesEntidad>> liveData = new MutableLiveData<>();
        if (uid != null) {
            db.collection("users").document(uid).collection("pendientes")
                    .addSnapshotListener((value, error) -> {
                        if (value != null) liveData.postValue(value.toObjects(PendientesEntidad.class));
                    });
        }
        return liveData;
    }

    public LiveData<PendientesEntidad> obtenerPendientesAleatorio() {
        MutableLiveData<PendientesEntidad> liveDataAleatorio = new MutableLiveData<>();
        if (uid != null) {
            db.collection("users").document(uid).collection("pendientes")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<PendientesEntidad> lista = queryDocumentSnapshots.toObjects(PendientesEntidad.class);
                            int randomIndex = new Random().nextInt(lista.size());
                            liveDataAleatorio.postValue(lista.get(randomIndex));
                        } else {
                            liveDataAleatorio.postValue(null);
                        }
                    });
        } else {
            liveDataAleatorio.postValue(null);
        }
        return liveDataAleatorio;
    }
}