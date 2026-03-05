package es.iesagora.actividad_de_seguimiento.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import es.iesagora.actividad_de_seguimiento.data.PendientesEntidad;
import es.iesagora.actividad_de_seguimiento.repository.AuthRepository;
import es.iesagora.actividad_de_seguimiento.repository.PendientesRepository;

public class ExplorarViewModel extends AndroidViewModel {
    private final AuthRepository authRepo;
    private final PendientesRepository pendientesRepo;

    private MutableLiveData<Integer> accionDialogo = new MutableLiveData<>();
    private MutableLiveData<PendientesEntidad> pendienteEncontrado = new MutableLiveData<>();
    private MutableLiveData<String> nombreUsuario = new MutableLiveData<>();

    public ExplorarViewModel(@NonNull Application application) {
        super(application);
        authRepo = new AuthRepository();
        pendientesRepo = new PendientesRepository();
    }

    private static boolean bienvenidaYaMostrada = false;
    public void comprobarBienvenida() {
        if (bienvenidaYaMostrada){
            return;
        }

        accionDialogo.setValue(0);

        authRepo.getNombreUsuario(new AuthRepository.NameCallback() {
            @Override
            public void onSuccess(String nombre) {
                nombreUsuario.postValue(nombre);
                bienvenidaYaMostrada = true;

                if (nombre != null && !nombre.isEmpty()) {
                    buscarPendienteAleatorio();
                } else {
                    accionDialogo.postValue(1);
                }
            }

            @Override
            public void onError(String message) {
                bienvenidaYaMostrada = true;
                accionDialogo.postValue(1);
            }
        });
    }

    private void buscarPendienteAleatorio() {
        pendientesRepo.obtenerPendientesAleatorio().observeForever(p -> {
            if (p != null) {
                pendienteEncontrado.postValue(p);
                accionDialogo.postValue(2);
            } else {
                accionDialogo.postValue(0);
            }
        });
    }

    public LiveData<String> getNombreUsuario() {
        return nombreUsuario;
    }

    public LiveData<Integer> getAccionDialogo() { return accionDialogo; }
    public LiveData<PendientesEntidad> getPendienteEncontrado() { return pendienteEncontrado; }
}