package es.iesagora.actividad_de_seguimiento.viewModel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import es.iesagora.actividad_de_seguimiento.api_rest.Resource;
import es.iesagora.actividad_de_seguimiento.model.Series;
import es.iesagora.actividad_de_seguimiento.repository.PreferencesRepository;
import es.iesagora.actividad_de_seguimiento.repository.SeriesRepository;

public class SeriesViewModel extends AndroidViewModel {
    private final SeriesRepository repository;
    private final PreferencesRepository prefsRepo;
    private final MutableLiveData<Resource<List<Series>>> seriesResult = new MutableLiveData<>();

    public SeriesViewModel(@NonNull Application application) {
        super(application);
        repository = new SeriesRepository();
        prefsRepo = new PreferencesRepository(application);
        fetchSeries();
    }

    public LiveData<Resource<List<Series>>> getSeriesResult() { return seriesResult; }

    public void fetchSeries() {
        String idioma = prefsRepo.getLanguageCode();
        repository.getSeriesList(idioma, result -> seriesResult.setValue(result));
    }
}