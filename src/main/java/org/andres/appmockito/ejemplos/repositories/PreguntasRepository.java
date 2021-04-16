package org.andres.appmockito.ejemplos.repositories;

import java.util.List;

public interface PreguntasRepository {
    List<String> findPreguntasPorExamenID(Long id);
    void guardarVarias(List<String> preguntas);
}
