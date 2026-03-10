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
        bienvenidaYaMostrada = true;

        accionDialogo.setValue(0);

        authRepo.getNombreUsuario(new AuthRepository.NameCallback() {
            @Override
            public void onSuccess(String nombre) {
                nombreUsuario.setValue(nombre);

                if (nombre != null && !nombre.isEmpty()) {
                    buscarPendienteAleatorio();
                } else {
                    accionDialogo.setValue(1);
                }
            }

            @Override
            public void onError(String message) {
                accionDialogo.setValue(1);
            }
        });
    }

    private void buscarPendienteAleatorio() {
        pendientesRepo.obtenerPendientesAleatorio().observeForever(p -> {
            if (p != null) {
                pendienteEncontrado.setValue(p);
                accionDialogo.setValue(2);
            } else {
                accionDialogo.setValue(0);
            }
        });
    }

    public LiveData<String> getNombreUsuario() {
        return nombreUsuario;
    }

    public LiveData<Integer> getAccionDialogo() { return accionDialogo; }
    public LiveData<PendientesEntidad> getPendienteEncontrado() { return pendienteEncontrado; }


    public void resetearDialogo() {
        accionDialogo.setValue(0);
    }

    public static void resetearBienvenida() {
        bienvenidaYaMostrada = false;
    }
}