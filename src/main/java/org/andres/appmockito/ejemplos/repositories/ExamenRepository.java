package org.andres.appmockito.ejemplos.repositories;

import org.andres.appmockito.ejemplos.models.Examen;

import java.util.List;

public interface ExamenRepository {
    List<Examen> findAll();
    Examen guardar(Examen examen);
}
