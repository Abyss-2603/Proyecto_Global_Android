package es.iesagora.actividad_de_seguimiento.model;

import java.util.ArrayList;
import java.util.List;

public class Generos {
    public static String getGeneros(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) return "Sin género";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            int id = ids.get(i);
            String nombre = "";
            switch (id) {
                case 28: nombre = "Acción"; break;
                case 12: nombre = "Aventura"; break;
                case 16: nombre = "Animación"; break;
                case 35: nombre = "Comedia"; break;
                case 18: nombre = "Drama"; break;
                case 27: nombre = "Terror"; break;
                case 878: nombre = "Ciencia Ficción"; break;
                case 53: nombre = "Suspense"; break;
                case 10759: nombre = "Acción y Aventura"; break;
                case 10765: nombre = "Ciencia Ficción y Fantasía"; break;
                default: continue;
            }
            sb.append(nombre);
            if (i < ids.size() - 1) sb.append(", ");
        }
        return sb.toString();
    }

    public static List<String> getGenerosList(List<Integer> ids) {
        List<String> nombres = new ArrayList<>();
        if (ids == null) return nombres;
        for (int id : ids) {
            switch (id) {
                case 28: nombres.add("Acción"); break;
                case 12: nombres.add("Aventura"); break;
                case 16: nombres.add("Animación"); break;
                case 35: nombres.add("Comedia"); break;
                case 18: nombres.add("Drama"); break;
                case 27: nombres.add("Terror"); break;
                case 878: nombres.add("Ciencia Ficción"); break;
                case 53: nombres.add("Suspense"); break;
                case 10759: nombres.add("Acción y Aventura"); break;
                case 10765: nombres.add("Ciencia Ficción y Fantasía"); break;
            }
        }
        return nombres;
    }
}
