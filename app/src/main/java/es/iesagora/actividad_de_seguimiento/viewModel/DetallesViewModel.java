package es.iesagora.actividad_de_seguimiento.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import es.iesagora.actividad_de_seguimiento.api_rest.Resource;
import es.iesagora.actividad_de_seguimiento.repository.PelisRepository;

public class DetallesViewModel extends ViewModel {
    private final PelisRepository repository;
    private final MutableLiveData<Resource<String>> trailerKey = new MutableLiveData<>();

    public DetallesViewModel() {
        repository = new PelisRepository();
    }

    public LiveData<Resource<String>> getTrailerKey() { return trailerKey; }

    public void fetchTrailer(int id, boolean isMovie) {
        repository.getTrailerKey(id, isMovie, result -> trailerKey.setValue(result));
    }
}