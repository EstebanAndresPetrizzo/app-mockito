package org.andres.appmockito.ejemplos.services;

import org.andres.appmockito.ejemplos.models.Examen;
import org.andres.appmockito.ejemplos.repositories.ExamenRepository;
import org.andres.appmockito.ejemplos.repositories.ExamenRepositoryImpl;
import org.andres.appmockito.ejemplos.repositories.PreguntaRepositoryImpl;
import org.andres.appmockito.ejemplos.repositories.PreguntasRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)//extendemos de Mockito y habilitamos las annotations
class ExamenServiceImplSpyTest {

    //
    @Spy
    PreguntaRepositoryImpl preguntasRepositoryImpl;
    @Spy
    ExamenRepositoryImpl repositoryImpl;

    //esta etiqueta solo se puede utilizar solo para los Impl de las interfaces
    @InjectMocks //inyecta las dependencias por el constructor
    ExamenServiceImpl service;

    // los espias no son metodos mock en todos los casos, si es que nosotros no llamamos a los metodos
    //mockeados, automáticamente se utilizara los metodos reales sin necesidad de usa el doCallRealMethod()
    //este es un hibrido entre un spy y un mock, no hace falta especificar los when
    @Test
    void testSpy() {
        //llamamos al simulado
        List<String> preguntas = Arrays.asList("POO");
        doReturn(preguntas).when(preguntasRepositoryImpl).findPreguntasPorExamenID(anyLong());

        Examen examen = service.findExamenPorNombreConPreguntas("Matemáticas");
        assertEquals(5, examen.getId());
        assertEquals("Matemáticas", examen.getNombre());
        assertEquals(1, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("POO"));

        verify(repositoryImpl).findAll();
        verify(preguntasRepositoryImpl).findPreguntasPorExamenID(anyLong());
    }
}