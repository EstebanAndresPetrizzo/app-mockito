package org.andres.appmockito.ejemplos.repositories;

import org.andres.appmockito.ejemplos.DatosImpl;

import java.util.List;

public class PreguntaRepositoryImpl implements PreguntasRepository{
    @Override
    public List<String> findPreguntasPorExamenID(Long id) {
        System.out.println("PreguntaRepositoryImpl.findPreguntasPorExamenID");
        return DatosImpl.PREGUNTAS;
    }

    @Override
    public void guardarVarias(List<String> preguntas) {
        System.out.println("PreguntaRepositoryImpl.guardarVarias");
    }
}
