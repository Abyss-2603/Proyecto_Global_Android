package es.iesagora.actividad_de_seguimiento.api_rest;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TokenInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        String tokenCompleto = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI5ZDBkY2EwMDlhNmRkN2I5Y2IzMmRhZjcyYjM1YTU4NyIsIm5iZiI6MTc2Nzg4ODg5Mi4xNCwic3ViIjoiNjk1ZmQ3ZmMyN2FmMTZhMDNhOTU5ZTU3Iiwic2NvcGVzIjpbImFwaV9yZWFkIl0sInZlcnNpb24iOjF9.n3BLfUUOmVDrBlVof4fAVSUXTfQuWutM57mqW4A4-5c";

        HttpUrl originalUrl = originalRequest.url();
        HttpUrl urlConIdioma = originalUrl.newBuilder()
                .addQueryParameter("language", "es-ES")
                .build();

        Request newRequest = originalRequest.newBuilder()
                .url(urlConIdioma)
                .header("accept", "application/json")
                .header("Authorization", tokenCompleto)
                .build();

        return chain.proceed(newRequest);
    }
}
