package co.edu.uco.sibe.infraestructura.controlador.comando;

import co.edu.uco.sibe.aplicacion.comando.manejador.CargarMasivamenteEmpleadosManejador;
import co.edu.uco.sibe.aplicacion.comando.manejador.CargarMasivamenteEstudiantesManejador;
import co.edu.uco.sibe.aplicacion.transversal.ComandoRespuesta;
import co.edu.uco.sibe.aplicacion.transversal.ResumenCargaMasiva;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CargaMasivaControladorTest {

    @Mock private CargarMasivamenteEmpleadosManejador cargarMasivamenteEmpleadosManejador;
    @Mock private CargarMasivamenteEstudiantesManejador cargarMasivamenteEstudiantesManejador;

    private CargaMasivaControlador controlador;

    @BeforeEach
    void setUp() {
        controlador = new CargaMasivaControlador(cargarMasivamenteEmpleadosManejador, cargarMasivamenteEstudiantesManejador);
    }

    @Test
    void deberiaCargarMasivamenteEmpleados() {
        MultipartFile archivo = mock(MultipartFile.class);
        var resumen = new ResumenCargaMasiva(10, 5, 3, 15);
        when(cargarMasivamenteEmpleadosManejador.ejecutar(archivo)).thenReturn(new ComandoRespuesta<>(resumen));

        ComandoRespuesta<ResumenCargaMasiva> resultado = controlador.cargarMasivamenteEmpleados(archivo);

        assertEquals(10, resultado.getValor().getActualizados());
        assertEquals(5, resultado.getValor().getNuevosCreados());
        verify(cargarMasivamenteEmpleadosManejador).ejecutar(archivo);
    }

    @Test
    void deberiaCargarMasivamenteEstudiantes() {
        MultipartFile archivo = mock(MultipartFile.class);
        var resumen = new ResumenCargaMasiva(8, 2, 1, 10);
        when(cargarMasivamenteEstudiantesManejador.ejecutar(archivo)).thenReturn(new ComandoRespuesta<>(resumen));

        ComandoRespuesta<ResumenCargaMasiva> resultado = controlador.cargarMasivamenteEstudiantes(archivo);

        assertEquals(8, resultado.getValor().getActualizados());
        assertEquals(2, resultado.getValor().getNuevosCreados());
        verify(cargarMasivamenteEstudiantesManejador).ejecutar(archivo);
    }
}
