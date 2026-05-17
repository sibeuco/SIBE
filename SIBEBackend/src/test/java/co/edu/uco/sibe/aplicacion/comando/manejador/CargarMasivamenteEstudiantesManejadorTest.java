package co.edu.uco.sibe.aplicacion.comando.manejador;

import co.edu.uco.sibe.aplicacion.comando.fabrica.EstudianteFabrica;
import co.edu.uco.sibe.aplicacion.comando.validador.ValidadorEstudiantesCargaMasiva;
import co.edu.uco.sibe.aplicacion.puerto.servicio.ProcesadorArchivoEstudianteServicio;
import co.edu.uco.sibe.aplicacion.transversal.ComandoRespuesta;
import co.edu.uco.sibe.aplicacion.transversal.ResumenCargaMasiva;
import co.edu.uco.sibe.dominio.modelo.Estudiante;
import co.edu.uco.sibe.dominio.puerto.comando.EstudianteRepositorioComando;
import co.edu.uco.sibe.dominio.usecase.comando.CargarMasivamenteEstudiantesUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CargarMasivamenteEstudiantesManejadorTest {

    @Mock private ProcesadorArchivoEstudianteServicio procesadorArchivoEstudianteServicio;
    @Mock private EstudianteFabrica estudianteFabrica;
    @Mock private CargarMasivamenteEstudiantesUseCase cargarMasivamenteEstudiantesUseCase;
    @Mock private EstudianteRepositorioComando estudianteRepositorioComando;
    @Mock private ValidadorEstudiantesCargaMasiva validadorEstudiantesCargaMasiva;

    private CargarMasivamenteEstudiantesManejador manejador;

    @BeforeEach
    void setUp() {
        manejador = new CargarMasivamenteEstudiantesManejador(procesadorArchivoEstudianteServicio, estudianteFabrica, cargarMasivamenteEstudiantesUseCase, estudianteRepositorioComando, validadorEstudiantesCargaMasiva);
    }

    @Test
    void deberiaDesactivarTodosAntesDeCargar() {
        MultipartFile archivo = mock(MultipartFile.class);
        var estudianteComando = mock(co.edu.uco.sibe.aplicacion.comando.DatosEstudianteComando.class);
        Estudiante estudiante = mock(Estudiante.class);

        when(procesadorArchivoEstudianteServicio.procesar(archivo)).thenReturn(List.of(estudianteComando));
        when(estudianteFabrica.construir(estudianteComando)).thenReturn(estudiante);
        when(estudianteRepositorioComando.desactivarTodos()).thenReturn(5);
        when(cargarMasivamenteEstudiantesUseCase.ejecutar(estudiante)).thenReturn(false);

        manejador.ejecutar(archivo);

        InOrder inOrder = inOrder(validadorEstudiantesCargaMasiva, estudianteRepositorioComando, cargarMasivamenteEstudiantesUseCase);
        inOrder.verify(validadorEstudiantesCargaMasiva).validar(List.of(estudianteComando));
        inOrder.verify(estudianteRepositorioComando).desactivarTodos();
        inOrder.verify(cargarMasivamenteEstudiantesUseCase).ejecutar(estudiante);
    }

    @Test
    void deberiaRetornarResumenConConteosCorrecto() {
        MultipartFile archivo = mock(MultipartFile.class);
        var estudianteComando1 = mock(co.edu.uco.sibe.aplicacion.comando.DatosEstudianteComando.class);
        var estudianteComando2 = mock(co.edu.uco.sibe.aplicacion.comando.DatosEstudianteComando.class);
        Estudiante estudiante1 = mock(Estudiante.class);
        Estudiante estudiante2 = mock(Estudiante.class);

        when(procesadorArchivoEstudianteServicio.procesar(archivo)).thenReturn(List.of(estudianteComando1, estudianteComando2));
        when(estudianteFabrica.construir(estudianteComando1)).thenReturn(estudiante1);
        when(estudianteFabrica.construir(estudianteComando2)).thenReturn(estudiante2);
        when(estudianteRepositorioComando.desactivarTodos()).thenReturn(3);
        when(cargarMasivamenteEstudiantesUseCase.ejecutar(estudiante1)).thenReturn(false);
        when(cargarMasivamenteEstudiantesUseCase.ejecutar(estudiante2)).thenReturn(true);

        ComandoRespuesta<ResumenCargaMasiva> resultado = manejador.ejecutar(archivo);

        ResumenCargaMasiva resumen = resultado.getValor();
        assertEquals(1, resumen.getActualizados());
        assertEquals(1, resumen.getNuevosCreados());
        assertEquals(2, resumen.getMarcadosInactivos());
        assertEquals(2, resumen.getTotalProcesados());
    }
}
