package es.iesagora.actividad_de_seguimiento.repository;

import java.util.List;
import es.iesagora.actividad_de_seguimiento.api_rest.Resource;
import es.iesagora.actividad_de_seguimiento.api_rest.RetrofitClient;
import es.iesagora.actividad_de_seguimiento.api_rest.TMDB_API;
import es.iesagora.actividad_de_seguimiento.model.Series;
import es.iesagora.actividad_de_seguimiento.model.SeriesAllResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SeriesRepository {
    private final TMDB_API api;
    private List<Series> cacheSeries = null;

    public SeriesRepository() {
        api = RetrofitClient.getClient().create(TMDB_API.class);
    }

    public interface SeriesListCallback {
        void onResult(Resource<List<Series>> result);
    }

    public void getSeriesList(String language, SeriesListCallback callback) {
        callback.onResult(Resource.loading());

        if (cacheSeries != null) {
            callback.onResult(Resource.success(cacheSeries));
            return;
        }

        api.getPopularSeries(language).enqueue(new Callback<SeriesAllResponse>() {
            @Override
            public void onResponse(Call<SeriesAllResponse> call, Response<SeriesAllResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cacheSeries = response.body().getResults();
                    callback.onResult(Resource.success(cacheSeries));
                } else {
                    callback.onResult(Resource.error("Error al cargar series populares"));
                }
            }

            @Override
            public void onFailure(Call<SeriesAllResponse> call, Throwable t) {
                callback.onResult(Resource.error("Error de red: " + t.getMessage()));
            }
        });
    }
}