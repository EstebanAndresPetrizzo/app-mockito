package org.andres.appmockito.ejemplos.repositories;

import org.andres.appmockito.ejemplos.DatosImpl;
import org.andres.appmockito.ejemplos.models.Examen;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ExamenRepositoryImpl implements ExamenRepository{
    @Override
    public List<Examen> findAll() {
        try {
            System.out.println("ExamenRepositoryOtro.findAll");
            TimeUnit.SECONDS.sleep(5);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        return DatosImpl.EXAMENES;
    }

    @Override
    public Examen guardar(Examen examen) {
        return DatosImpl.EXAMEN;
    }
}
