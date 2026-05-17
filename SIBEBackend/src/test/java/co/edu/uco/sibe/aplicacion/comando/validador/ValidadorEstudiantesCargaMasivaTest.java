package co.edu.uco.sibe.aplicacion.comando.validador;

import co.edu.uco.sibe.aplicacion.comando.DatosEstudianteComando;
import co.edu.uco.sibe.aplicacion.transversal.ErrorCargaMasivaExcepcion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValidadorEstudiantesCargaMasivaTest {

    private ValidadorEstudiantesCargaMasiva validador;

    @BeforeEach
    void setUp() {
        validador = new ValidadorEstudiantesCargaMasiva();
    }

    private DatosEstudianteComando estudianteValido(String identificacion) {
        return new DatosEstudianteComando(
                "Maria Gomez", identificacion, "CARNET001",
                "1/15/2000", "F", "Colombiana", "Soltera",
                "Calle 1", "Bogota", "3001234567", "3001234568",
                "maria@correo.com", "maria@universidad.edu.co",
                "Ingenieria de Sistemas", "Ingenieria",
                "2023", "20231", "120", "4.2",
                "Activa", "Presencial", "30", "Bus"
        );
    }

    @Test
    void noDeberiaLanzarExcepcionConDatosValidos() {
        var estudiantes = List.of(estudianteValido("123"), estudianteValido("456"));
        assertDoesNotThrow(() -> validador.validar(estudiantes));
    }

    @Test
    void deberiaDetectarDocumentoDuplicado() {
        var estudiantes = List.of(estudianteValido("789"), estudianteValido("789"));
        var ex = assertThrows(ErrorCargaMasivaExcepcion.class, () -> validador.validar(estudiantes));
        assertEquals(1, ex.getErrores().size());
        assertEquals(3, ex.getErrores().get(0).numeroFila());
        assertTrue(ex.getErrores().get(0).descripcion().contains("Documento duplicado"));
    }

    @Test
    void deberiaDetectarCorreoPersonalInvalido() {
        var estudiante = new DatosEstudianteComando(
                "Ana Lopez", "100", "CARNET002",
                "1/15/2000", "F", "Colombiana", "Soltera",
                "Calle 2", "Medellin", "3001111111", "3002222222",
                "correo-invalido", "ana@universidad.edu.co",
                "Medicina", "Salud",
                "2022", "20221", "60", "3.8",
                "Activa", "Presencial", "20", "Metro"
        );
        var ex = assertThrows(ErrorCargaMasivaExcepcion.class, () -> validador.validar(List.of(estudiante)));
        assertTrue(ex.getErrores().stream().anyMatch(e -> e.descripcion().contains("Correo personal inválido")));
    }

    @Test
    void deberiaDetectarCorreoInstitucionalInvalido() {
        var estudiante = new DatosEstudianteComando(
                "Carlos Rios", "200", "CARNET003",
                "1/15/2000", "M", "Colombiana", "Soltero",
                "Calle 3", "Cali", "3003333333", "3004444444",
                "carlos@correo.com", "no-es-un-correo",
                "Derecho", "Ciencias Sociales",
                "2021", "20211", "90", "4.0",
                "Activa", "Virtual", "40", "A pie"
        );
        var ex = assertThrows(ErrorCargaMasivaExcepcion.class, () -> validador.validar(List.of(estudiante)));
        assertTrue(ex.getErrores().stream().anyMatch(e -> e.descripcion().contains("Correo institucional inválido")));
    }

    @Test
    void deberiaDetectarNombreVacio() {
        var estudiante = new DatosEstudianteComando(
                "", "300", "CARNET004",
                "1/15/2000", "F", "Colombiana", "Soltera",
                "Calle 4", "Bogota", "3005555555", "3006666666",
                "x@correo.com", "x@universidad.edu.co",
                "Biologia", "Ciencias",
                "2023", "20231", "30", "3.5",
                "Activa", "Presencial", "15", "Taxi"
        );
        var ex = assertThrows(ErrorCargaMasivaExcepcion.class, () -> validador.validar(List.of(estudiante)));
        assertTrue(ex.getErrores().stream().anyMatch(e -> e.descripcion().contains("Nombre completo vacío")));
    }

    @Test
    void deberiaAceptarCorreosVaciosComoOpcionales() {
        var estudiante = new DatosEstudianteComando(
                "Pedro Sosa", "400", "CARNET005",
                "1/15/2000", "M", "Colombiana", "Soltero",
                "Calle 5", "Barranquilla", "", "",
                "", "",
                "Arquitectura", "Artes",
                "2023", "20231", "45", "3.9",
                "Activa", "Presencial", "25", "Bicicleta"
        );
        assertDoesNotThrow(() -> validador.validar(List.of(estudiante)));
    }
}
