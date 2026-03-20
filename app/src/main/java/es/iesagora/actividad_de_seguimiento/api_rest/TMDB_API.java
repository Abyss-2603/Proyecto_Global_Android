package es.iesagora.actividad_de_seguimiento.api_rest;

import es.iesagora.actividad_de_seguimiento.model.ActorDetails;
import es.iesagora.actividad_de_seguimiento.model.CombinedCreditsResponse;
import es.iesagora.actividad_de_seguimiento.model.CreditsResponse;
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
    @GET("tv/{tv_id}")
    Call<Series> getSerieDetails(@Path("tv_id") int tvId);

    @GET("search/movie")
    Call<PelisAllResponse> searchMovies(@Query("query") String query);

    @GET("search/tv")
    Call<SeriesAllResponse> searchSeries(@Query("query") String query);

    @GET("tv/{id}")
    Call<Series> getSeriesDetails(@Path("id") int id);



    //-----------AMPLIACIÓN------------

    // 1. Obtener el reparto de una película
    @GET("movie/{movie_id}/credits")
    Call<CreditsResponse> getMovieCredits(@Path("movie_id") int movieId);

    // 2. Obtener el reparto de una serie
    @GET("tv/{tv_id}/credits")
    Call<CreditsResponse> getSerieCredits(@Path("tv_id") int tvId);

    // 3. Información biográfica y detalles del actor
    @GET("person/{person_id}")
    Call<ActorDetails> getActorDetails(@Path("person_id") int personId);

    // 4. Filmografía del actor
    @GET("person/{person_id}/combined_credits")
    Call<CombinedCreditsResponse> getCombinedCredits(@Path("person_id") int personId);
}