package es.iesagora.actividad_de_seguimiento.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import es.iesagora.actividad_de_seguimiento.data.PendientesEntidad;
import es.iesagora.actividad_de_seguimiento.repository.PendientesRepository;

public class PendientesViewModel extends AndroidViewModel {
    private final PendientesRepository repository;

    public PendientesViewModel(@NonNull Application application) {
        super(application);
        repository = new PendientesRepository();
    }

    public LiveData<List<PendientesEntidad>> obtenerPendientes() {
        return repository.obtenerTodo();
    }

    public void insertar(PendientesEntidad entidad) {
        repository.insertar(entidad);
    }

    public void eliminar(PendientesEntidad entidad) {
        repository.eliminarPorIdApi(entidad.getIdAPI());
    }

    public void eliminarPorIdApi(int idApi) {
        repository.eliminarPorIdApi(idApi);
    }
}