package es.iesagora.actividad_de_seguimiento.api_rest;

import es.iesagora.actividad_de_seguimiento.model.Peliculas;
import es.iesagora.actividad_de_seguimiento.model.PelisAllResponse;
import es.iesagora.actividad_de_seguimiento.model.Series;
import es.iesagora.actividad_de_seguimiento.model.SeriesAllResponse;
import es.iesagora.actividad_de_seguimiento.model.VideoResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TMDB_API {
    @GET("movie/popular")
    Call<PelisAllResponse> getPopularMovies(@Query("language") String language);

    @GET("tv/popular")
    Call<SeriesAllResponse> getPopularSeries(@Query("language") String language);

    @GET("movie/{id}/videos")
    Call<VideoResponse> getMovieVideos(@Path("id") int id);

    @GET("tv/{id}/videos")
    Call<VideoResponse> getSeriesVideos(@Path("id") int id);

    @GET("movie/{id}")
    Call<Peliculas> getMovieDetails(@Path("id") int id);

    @GET("search/movie")
    Call<PelisAllResponse> searchMovies(@Query("query") String query);

    @GET("search/tv")
    Call<SeriesAllResponse> searchSeries(@Query("query") String query);

    @GET("tv/{id}")
    Call<Series> getSeriesDetails(@Path("id") int id); //
}