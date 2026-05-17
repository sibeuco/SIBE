package co.edu.uco.sibe.aplicacion.comando.validador;

import co.edu.uco.sibe.aplicacion.comando.DatosEmpleadoComando;
import co.edu.uco.sibe.aplicacion.transversal.ErrorCargaMasivaExcepcion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValidadorEmpleadosCargaMasivaTest {

    private ValidadorEmpleadosCargaMasiva validador;

    @BeforeEach
    void setUp() {
        validador = new ValidadorEmpleadosCargaMasiva();
    }

    private DatosEmpleadoComando empleadoValido(String identificacion) {
        return new DatosEmpleadoComando("Juan Perez", "M", identificacion,
                "CHIP001", "COD01", "GRUPO1", "CC001", "Centro", "COD_CIUDAD", "Bogota");
    }

    @Test
    void noDeberiaLanzarExcepcionConDatosValidos() {
        var empleados = List.of(empleadoValido("123"), empleadoValido("456"));
        assertDoesNotThrow(() -> validador.validar(empleados));
    }

    @Test
    void deberiaDetectarDocumentoDuplicado() {
        var empleados = List.of(empleadoValido("123"), empleadoValido("123"));
        var ex = assertThrows(ErrorCargaMasivaExcepcion.class, () -> validador.validar(empleados));
        assertEquals(1, ex.getErrores().size());
        assertEquals(3, ex.getErrores().get(0).numeroFila());
        assertTrue(ex.getErrores().get(0).descripcion().contains("Documento duplicado"));
        assertTrue(ex.getErrores().get(0).descripcion().contains("123"));
    }

    @Test
    void deberiaDetectarNombreVacio() {
        var empleado = new DatosEmpleadoComando("", "M", "999", "CHIP", "COD", "GRUPO", "CC", "Centro", "COD_C", "Ciudad");
        var ex = assertThrows(ErrorCargaMasivaExcepcion.class, () -> validador.validar(List.of(empleado)));
        assertTrue(ex.getErrores().stream().anyMatch(e -> e.descripcion().contains("Nombre vacío")));
    }

    @Test
    void deberiaDetectarIdentificacionVacia() {
        var empleado = new DatosEmpleadoComando("Juan", "M", "", "CHIP", "COD", "GRUPO", "CC", "Centro", "COD_C", "Ciudad");
        var ex = assertThrows(ErrorCargaMasivaExcepcion.class, () -> validador.validar(List.of(empleado)));
        assertTrue(ex.getErrores().stream().anyMatch(e -> e.descripcion().contains("Identificación vacía")));
    }

    @Test
    void deberiaDetectarMultiplesErroresEnMultiplesFilas() {
        var empleado1 = new DatosEmpleadoComando("", "M", "", "CHIP", "COD", "GRUPO", "CC", "Centro", "COD_C", "Ciudad");
        var empleado2 = new DatosEmpleadoComando("Ana", "F", "456", "", "COD", "GRUPO", "CC", "Centro", "COD_C", "Ciudad");
        var ex = assertThrows(ErrorCargaMasivaExcepcion.class, () -> validador.validar(List.of(empleado1, empleado2)));
        assertTrue(ex.getErrores().size() >= 2);
        assertTrue(ex.getErrores().stream().anyMatch(e -> e.numeroFila() == 2));
        assertTrue(ex.getErrores().stream().anyMatch(e -> e.descripcion().contains("ID de carnet vacío")));
    }

    @Test
    void deberiaIndicarNumeroDeFilaCorrectamente() {
        var empleados = List.of(
                empleadoValido("001"),
                empleadoValido("002"),
                new DatosEmpleadoComando("", "M", "003", "CHIP", "COD", "GRUPO", "CC", "Centro", "COD_C", "Ciudad")
        );
        var ex = assertThrows(ErrorCargaMasivaExcepcion.class, () -> validador.validar(empleados));
        assertEquals(4, ex.getErrores().get(0).numeroFila());
    }
}
