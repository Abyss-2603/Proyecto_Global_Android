package es.iesagora.actividad_de_seguimiento.repository;

import java.util.List;
import es.iesagora.actividad_de_seguimiento.api_rest.Resource;
import es.iesagora.actividad_de_seguimiento.api_rest.RetrofitClient;
import es.iesagora.actividad_de_seguimiento.api_rest.TMDB_API;
import es.iesagora.actividad_de_seguimiento.model.Peliculas;
import es.iesagora.actividad_de_seguimiento.model.PelisAllResponse;
import es.iesagora.actividad_de_seguimiento.model.VideoResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PelisRepository {
    private final TMDB_API api;
    private List<Peliculas> cachePelis = null;

    public PelisRepository() {
        api = RetrofitClient.getClient().create(TMDB_API.class);
    }

    public interface PelisListCallback {
        void onResult(Resource<List<Peliculas>> result);
    }

    public interface VideoCallback {
        void onResult(Resource<String> result);
    }

    public void getPelisList(String language, PelisListCallback callback) {
        callback.onResult(Resource.loading());

        if (cachePelis != null) {
            callback.onResult(Resource.success(cachePelis));
            return;
        }

        api.getPopularMovies(language).enqueue(new Callback<PelisAllResponse>() {
            @Override
            public void onResponse(Call<PelisAllResponse> call, Response<PelisAllResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cachePelis = response.body().getResults();
                    callback.onResult(Resource.success(cachePelis));
                } else {
                    callback.onResult(Resource.error("Error al cargar películas populares"));
                }
            }

            @Override
            public void onFailure(Call<PelisAllResponse> call, Throwable t) {
                callback.onResult(Resource.error("Error de red: " + t.getMessage()));
            }
        });
    }

    public void getTrailerKey(int id, boolean isMovie, VideoCallback callback) {
        Call<VideoResponse> call = isMovie ? api.getMovieVideos(id) : api.getSeriesVideos(id);

        call.enqueue(new Callback<VideoResponse>() {
            @Override
            public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (VideoResponse.VideoResult v : response.body().getResults()) {
                        if ("YouTube".equals(v.getSite()) && "Trailer".equals(v.getType())) {
                            callback.onResult(Resource.success(v.getKey()));
                            return;
                        }
                    }
                    callback.onResult(Resource.error("No se encontró un tráiler en YouTube"));
                }
            }

            @Override
            public void onFailure(Call<VideoResponse> call, Throwable t) {
                callback.onResult(Resource.error("Error al conectar con el servidor"));
            }
        });
    }
}