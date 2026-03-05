package es.iesagora.actividad_de_seguimiento.viewModel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import es.iesagora.actividad_de_seguimiento.api_rest.Resource;
import es.iesagora.actividad_de_seguimiento.model.Peliculas;
import es.iesagora.actividad_de_seguimiento.repository.PelisRepository;
import es.iesagora.actividad_de_seguimiento.repository.PreferencesRepository;

public class PelisViewModel extends AndroidViewModel {
    private final PelisRepository repository;
    private final PreferencesRepository prefsRepo;
    private final MutableLiveData<Resource<List<Peliculas>>> pelisResult = new MutableLiveData<>();

    public PelisViewModel(@NonNull Application application) {
        super(application);
        repository = new PelisRepository();
        prefsRepo = new PreferencesRepository(application);
        fetchPelis();
    }

    public LiveData<Resource<List<Peliculas>>> getPelisResult() { return pelisResult; }

    public void fetchPelis() {
        String idioma = prefsRepo.getLanguageCode();
        repository.getPelisList(idioma, result -> pelisResult.setValue(result));
    }
}