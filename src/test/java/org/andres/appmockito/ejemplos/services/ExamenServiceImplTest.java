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
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)//extendemos de Mockito y habilitamos las annotations
class ExamenServiceImplTest {

    //Given
    //Anotacion para crear mecks
    @Mock
    ExamenRepository repository;
    @Mock
    PreguntasRepository preguntasRepository;
    @Captor
    ArgumentCaptor<Long> captor;

    //
    @Mock
    PreguntaRepositoryImpl preguntasRepositoryImpl;
    @Mock
    ExamenRepositoryImpl repositoryImpl;


    //esta etiqueta solo se puede utilizar solo para los Impl de las interfaces
    @InjectMocks //inyecta las dependencias por el constructor
    ExamenServiceImpl service;

    @BeforeEach
    void setUp() {
//        MockitoAnnotations.openMocks(this);//habilitamos los annotations
        //REEMPLAZO LA INSTACION POR INYECCION DE DEPENDENCIAS
//        repository = mock(ExamenRepository.class);
//        preguntasRepository = mock(PreguntasRepository.class);
//        service = new ExamenServiceImpl(repository, preguntasRepository);
    }

    @Test
    void findExamenPoNombre(){
        when(repository.findAll()).thenReturn(Datos.EXAMENES);

        Optional<Examen> examen = service.findExamenPorNombre("Matemáticas");
        assertTrue(examen.isPresent());
        assertEquals(5L,examen.orElseThrow().getId());
        assertEquals("Matemáticas", examen.get().getNombre());
    }

    @Test
    void findExamenPoNombreListaVacia(){
        ExamenRepository repository = mock(ExamenRepository.class);
        ExamenService service = new ExamenServiceImpl(repository);
        List<Examen> datos = Collections.emptyList();

        when(repository.findAll()).thenReturn(datos);
        Optional<Examen> examen = service.findExamenPorNombre("Matemáticas");

        assertFalse(examen.isPresent());
    }

    @Test
    void testPreguntasExamen(){
        //any -> se utiliza para pasar cualquier tipo de valor rendom
        //anyLong -> cualquier tipo de valor Long
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntasRepository.findPreguntasPorExamenID(anyLong())).thenReturn(Datos.PREGUNTAS);
        Examen examen = service.findExamenPorNombreConPreguntas("Historia");

        assertEquals(5,examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("Aritmética"));
    }

    @Test
    void testPreguntasExamenVerificar(){
        //any -> se utiliza para pasar cualquier tipo de valor rendom
        //anyLong -> cualquier tipo de valor Long
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntasRepository.findPreguntasPorExamenID(anyLong())).thenReturn(Datos.PREGUNTAS);
        Examen examen = service.findExamenPorNombreConPreguntas("Historia");

        assertEquals(5,examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("Aritmética"));

        //En el caso del verify, los merotods se colocan fuera del parentesis
        //se verifica si se llamo al metodo tal
        verify(repository).findAll();
        verify(preguntasRepository).findPreguntasPorExamenID(anyLong());
    }

    @Test
    void testNoExisteExamenVerify(){
        //any -> se utiliza para pasar cualquier tipo de valor rendom
        //anyLong -> cualquier tipo de valor Long
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntasRepository.findPreguntasPorExamenID(anyLong())).thenReturn(Datos.PREGUNTAS);
        Examen examen = service.findExamenPorNombreConPreguntas("Historia2");
        assertNull(examen);
        //En el caso del verify, los merotods se colocan fuera del parentesis
        //se verifica si se llamo al metodo tal
        verify(repository).findAll();
        verify(preguntasRepository).findPreguntasPorExamenID(anyLong());
    }

    @Test
    void testGuardarExamen(){//Entorno inpulsado al comportamiento - Given/When/Then
        //Given - Dado
        Examen newExamen = Datos.EXAMEN;
        newExamen.setPreguntas(Datos.PREGUNTAS);

        //Simulamos el guardo con id incremental como si fuera a la base de datos
        //con clases anónimas
        when(repository.guardar(any(Examen.class))).then(new Answer<Examen>(){

            Long secuencia = 8L;

            @Override
            public Examen answer(InvocationOnMock invocationOnMock) throws Throwable {
                Examen examen =invocationOnMock.getArgument(0);
                examen.setId(secuencia++);
                return examen;
            }
        });

        //When - Cuando
        Examen examen = service.guardar(newExamen);

        //Then - Entonces
        assertNotNull(examen.getId());
        assertEquals(8L, examen.getId());
        assertEquals("Física", examen.getNombre());

        verify(repository).guardar(any(Examen.class));
        verify(preguntasRepository).guardarVarias(anyList());

    }

    @Test
    void testManejoException(){
        when(repository.findAll()).thenReturn(Datos.EXAMENES_ID_NULL);
        //se puede crear una clase con una excepcion propia
        when(preguntasRepository.findPreguntasPorExamenID(isNull())).thenThrow(IllegalArgumentException.class);
        //verificamos que haya saltado la excepcion IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> {
            service.findExamenPorNombreConPreguntas("Matemáticas");
        });

        verify(repository).findAll();
        verify(preguntasRepository).findPreguntasPorExamenID(isNull());
    }

    @Test
    void testArgumentMatchers(){
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntasRepository.findPreguntasPorExamenID(anyLong())).thenReturn(Datos.PREGUNTAS);

        service.findExamenPorNombreConPreguntas("Matemáticas");

        verify(repository).findAll();
        //verifico con expreciones y argumentMatchers
        verify(preguntasRepository).findPreguntasPorExamenID(argThat(arg -> arg != null && arg.equals(5L)));
        //otra forma
        verify(preguntasRepository).findPreguntasPorExamenID(argThat(arg -> arg != null && arg >= 5L));
        //otra forma de verificar
        verify(preguntasRepository).findPreguntasPorExamenID(eq(5L));
    }

    @Test
    void testArgumentMatchers2(){
        when(repository.findAll()).thenReturn(Datos.EXAMENES_ID_NEGATIVOS);
        when(preguntasRepository.findPreguntasPorExamenID(anyLong())).thenReturn(Datos.PREGUNTAS);

        service.findExamenPorNombreConPreguntas("Matemáticas");

        verify(repository).findAll();
        verify(preguntasRepository).findPreguntasPorExamenID(argThat(new MiArgsMatchers()));
    }

    @Test
    void testArgumentMatchers3(){
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntasRepository.findPreguntasPorExamenID(anyLong())).thenReturn(Datos.PREGUNTAS);

        service.findExamenPorNombreConPreguntas("Matemáticas");

        verify(repository).findAll();
        //en este caso solo estariamos utilizando el metodo matches y no imprimiria el toString
        verify(preguntasRepository).findPreguntasPorExamenID(argThat((argument) -> argument != null && argument > 0));
    }

    public static class MiArgsMatchers implements ArgumentMatcher<Long> {

        private Long argument;

        @Override
        public boolean matches(Long aLong) {
            this.argument = aLong;
            return aLong != null && aLong > 0;
        }

        @Override
        public String toString() {
            return "Es para mostrar un mensaje personalizado de error " +
                    "que imprime mockito en caso de que falle el test" +
                    "argument= " + argument + " debe ser un entero positivo";
        }
    }

    @Test
    void testArgumentCaptor(){
        //damos el comportamiento al findAll para que devuelva los examenes
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        //invocar el service
        service.findExamenPorNombreConPreguntas("Matemáticas");

        //instanciamos el captor para un tipo Long
        //ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);//REEMPLAZO CON ANOTACIONES
        //capturamos el examen
        verify(preguntasRepository).findPreguntasPorExamenID(captor.capture());

        //comparamos con el esperado
        assertEquals(5L,captor.getValue());
    }

    @Test
    void testDoThrow(){
        Examen examen = Datos.EXAMEN;
        examen.setPreguntas(Datos.PREGUNTAS);
        //IMPORTANTE
        //when(preguntasRepository.guardarVarias(anyList())).thenThrow(IllegalArgumentException.class);
        //En el caso que necesitemos lanzar una excepcion sobre un metodo que devuelve void
        //no utilizaremos el thenThrow sino el dothrow
        //se hace alrevez, ya que no podemos esperar una excepcion empezaremos con el do
        doThrow(IllegalArgumentException.class).when(preguntasRepository).guardarVarias(anyList());

        assertThrows(IllegalArgumentException.class, () -> {
            service.guardar(examen);
        });
    }

    @Test
    void testDoAnswer() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        doAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return id == 5L ? Datos.PREGUNTAS : Collections.emptyList();
        }).when(preguntasRepository).findPreguntasPorExamenID(anyLong());

        Examen examen = service.findExamenPorNombreConPreguntas("Matemáticas");
        assertEquals(5L, examen.getId());
        assertEquals("Matemáticas", examen.getNombre());

        verify(preguntasRepository).findPreguntasPorExamenID(anyLong());
    }

    @Test
        //si se necesitara probar el metodo real y no el simulado con el mock
    //si es que no se puede simular el metodo de terceros
    void testDoCallRealMethod() {
        //en parte voy a simular
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        //y en parte la real
        doCallRealMethod().when(preguntasRepositoryImpl).findPreguntasPorExamenID(anyLong());
        Examen examen = service.findExamenPorNombreConPreguntas("Matemáticas");
        assertEquals(5L, examen.getId());
        assertEquals("Matemáticas", examen.getNombre());
    }

    // los espias no son metodos mock en todos los casos, si es que nosotros no llamamos a los metodos
    //mockeados, automáticamente se utilizara los metodos reales sin necesidad de usa el doCallRealMethod()
    //este es un hibrido entre un spy y un mock, no hace falta especificar los when
    @Test
    void testSpy() {
        ExamenRepository examenRepository = spy(ExamenRepositoryImpl.class);
        PreguntasRepository preguntasRepository = spy(PreguntaRepositoryImpl.class);
        ExamenService examenService = new ExamenServiceImpl(examenRepository, preguntasRepository);

        //llamamos al simulado
        List<String> preguntas = Arrays.asList("POO");
        doReturn(preguntas).when(preguntasRepository).findPreguntasPorExamenID(anyLong());

        Examen examen = examenService.findExamenPorNombreConPreguntas("Matemáticas");
        assertEquals(5, examen.getId());
        assertEquals("Matemáticas", examen.getNombre());
        assertEquals(1, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("POO"));

        verify(examenRepository).findAll();
        verify(preguntasRepository).findPreguntasPorExamenID(anyLong());
    }

    @Test //para verificar el orden de las implementaciones
    void testOrderDeInvocaciones() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);

        service.findExamenPorNombreConPreguntas("Matemáticas");
        service.findExamenPorNombreConPreguntas("Lenguaje");

        InOrder inOrder = inOrder(preguntasRepository);
        inOrder.verify(preguntasRepository).findPreguntasPorExamenID(5L);
        inOrder.verify(preguntasRepository).findPreguntasPorExamenID(6L);

    }

    @Test
    void testNumeroDeInvocaciones() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        service.findExamenPorNombreConPreguntas("Matemáticas");

        verify(preguntasRepository).findPreguntasPorExamenID(5L); //verifica que se haya hecho tan solo una vez
        verify(preguntasRepository, times(1)).findPreguntasPorExamenID(5L); //verifica que se haya hecho tan solo una vez
        verify(preguntasRepository, atLeast(1)).findPreguntasPorExamenID(5L); //verifica que se haya hecho almenos una vez
        verify(preguntasRepository, atLeastOnce()).findPreguntasPorExamenID(5L); //verifica que se haya hecho al menos una vez de forma estatica sin especificar la cantidad
        verify(preguntasRepository, atMost(10)).findPreguntasPorExamenID(5L); //verifica que se haya hecho como mucho diez veces
        verify(preguntasRepository, atMostOnce()).findPreguntasPorExamenID(5L); //verifica que se haya hecho como maximo una vez

    }

    @Test
    void testNumeroDeInvocacionesListaVacía() {
        when(repository.findAll()).thenReturn(Collections.emptyList());
        service.findExamenPorNombreConPreguntas("Matemáticas");

        verify(preguntasRepository, never()).findPreguntasPorExamenID(5L);
        verifyNoInteractions(preguntasRepository);//similar

    }
}