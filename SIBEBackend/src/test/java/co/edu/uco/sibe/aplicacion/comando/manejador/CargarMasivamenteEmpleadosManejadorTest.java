package co.edu.uco.sibe.aplicacion.comando.manejador;

import co.edu.uco.sibe.aplicacion.comando.fabrica.EmpleadoFabrica;
import co.edu.uco.sibe.aplicacion.comando.validador.ValidadorEmpleadosCargaMasiva;
import co.edu.uco.sibe.aplicacion.puerto.servicio.ProcesadorArchivoEmpleadoServicio;
import co.edu.uco.sibe.aplicacion.transversal.ComandoRespuesta;
import co.edu.uco.sibe.aplicacion.transversal.ResumenCargaMasiva;
import co.edu.uco.sibe.dominio.modelo.Empleado;
import co.edu.uco.sibe.dominio.puerto.comando.EmpleadoRepositorioComando;
import co.edu.uco.sibe.dominio.usecase.comando.CargarMasivamenteEmpleadosUseCase;
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
class CargarMasivamenteEmpleadosManejadorTest {

    @Mock private ProcesadorArchivoEmpleadoServicio procesadorArchivoEmpleadoServicio;
    @Mock private EmpleadoFabrica empleadoFabrica;
    @Mock private CargarMasivamenteEmpleadosUseCase cargarMasivamenteEmpleadosUseCase;
    @Mock private EmpleadoRepositorioComando empleadoRepositorioComando;
    @Mock private ValidadorEmpleadosCargaMasiva validadorEmpleadosCargaMasiva;

    private CargarMasivamenteEmpleadosManejador manejador;

    @BeforeEach
    void setUp() {
        manejador = new CargarMasivamenteEmpleadosManejador(procesadorArchivoEmpleadoServicio, empleadoFabrica, cargarMasivamenteEmpleadosUseCase, empleadoRepositorioComando, validadorEmpleadosCargaMasiva);
    }

    @Test
    void deberiaDesactivarTodosAntesDeCargar() {
        MultipartFile archivo = mock(MultipartFile.class);
        var empleadoComando = mock(co.edu.uco.sibe.aplicacion.comando.DatosEmpleadoComando.class);
        Empleado empleado = mock(Empleado.class);

        when(procesadorArchivoEmpleadoServicio.procesar(archivo)).thenReturn(List.of(empleadoComando));
        when(empleadoFabrica.construir(empleadoComando)).thenReturn(empleado);
        when(empleadoRepositorioComando.desactivarTodos()).thenReturn(5);
        when(cargarMasivamenteEmpleadosUseCase.ejecutar(empleado)).thenReturn(false);

        manejador.ejecutar(archivo);

        InOrder inOrder = inOrder(validadorEmpleadosCargaMasiva, empleadoRepositorioComando, cargarMasivamenteEmpleadosUseCase);
        inOrder.verify(validadorEmpleadosCargaMasiva).validar(List.of(empleadoComando));
        inOrder.verify(empleadoRepositorioComando).desactivarTodos();
        inOrder.verify(cargarMasivamenteEmpleadosUseCase).ejecutar(empleado);
    }

    @Test
    void deberiaRetornarResumenConConteosCorrecto() {
        MultipartFile archivo = mock(MultipartFile.class);
        var empleadoComando1 = mock(co.edu.uco.sibe.aplicacion.comando.DatosEmpleadoComando.class);
        var empleadoComando2 = mock(co.edu.uco.sibe.aplicacion.comando.DatosEmpleadoComando.class);
        Empleado empleado1 = mock(Empleado.class);
        Empleado empleado2 = mock(Empleado.class);

        when(procesadorArchivoEmpleadoServicio.procesar(archivo)).thenReturn(List.of(empleadoComando1, empleadoComando2));
        when(empleadoFabrica.construir(empleadoComando1)).thenReturn(empleado1);
        when(empleadoFabrica.construir(empleadoComando2)).thenReturn(empleado2);
        when(empleadoRepositorioComando.desactivarTodos()).thenReturn(3);
        when(cargarMasivamenteEmpleadosUseCase.ejecutar(empleado1)).thenReturn(false);
        when(cargarMasivamenteEmpleadosUseCase.ejecutar(empleado2)).thenReturn(true);

        ComandoRespuesta<ResumenCargaMasiva> resultado = manejador.ejecutar(archivo);

        ResumenCargaMasiva resumen = resultado.getValor();
        assertEquals(1, resumen.getActualizados());
        assertEquals(1, resumen.getNuevosCreados());
        assertEquals(2, resumen.getMarcadosInactivos());
        assertEquals(2, resumen.getTotalProcesados());
    }
}
