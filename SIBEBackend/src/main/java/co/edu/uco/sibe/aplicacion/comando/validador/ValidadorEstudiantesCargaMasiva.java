package co.edu.uco.sibe.aplicacion.comando.validador;

import co.edu.uco.sibe.aplicacion.comando.DatosEstudianteComando;
import co.edu.uco.sibe.aplicacion.transversal.ErrorCargaMasivaExcepcion;
import co.edu.uco.sibe.aplicacion.transversal.ErrorFilaCargaMasiva;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Component
public class ValidadorEstudiantesCargaMasiva {

    private static final Pattern PATRON_EMAIL =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public void validar(List<DatosEstudianteComando> estudiantes) {
        List<ErrorFilaCargaMasiva> errores = new ArrayList<>();
        Set<String> identificacionesVistas = new HashSet<>();

        for (int i = 0; i < estudiantes.size(); i++) {
            int numeroFila = i + 2;
            DatosEstudianteComando estudiante = estudiantes.get(i);

            if (isBlank(estudiante.getNombreCompleto())) {
                errores.add(new ErrorFilaCargaMasiva(numeroFila, "Nombre completo vacío"));
            }

            if (isBlank(estudiante.getNumeroIdentificacion())) {
                errores.add(new ErrorFilaCargaMasiva(numeroFila, "Número de identificación vacío"));
            } else if (!identificacionesVistas.add(estudiante.getNumeroIdentificacion())) {
                errores.add(new ErrorFilaCargaMasiva(numeroFila,
                        "Documento duplicado: " + estudiante.getNumeroIdentificacion()));
            }

            if (!isBlank(estudiante.getCorreoPersonal()) && !esEmailValido(estudiante.getCorreoPersonal())) {
                errores.add(new ErrorFilaCargaMasiva(numeroFila,
                        "Correo personal inválido: " + estudiante.getCorreoPersonal()));
            }

            if (!isBlank(estudiante.getCorreoInstitucional()) && !esEmailValido(estudiante.getCorreoInstitucional())) {
                errores.add(new ErrorFilaCargaMasiva(numeroFila,
                        "Correo institucional inválido: " + estudiante.getCorreoInstitucional()));
            }

            if (isBlank(estudiante.getProgramaAcademico())) {
                errores.add(new ErrorFilaCargaMasiva(numeroFila, "Programa académico vacío"));
            }

            if (isBlank(estudiante.getFacultad())) {
                errores.add(new ErrorFilaCargaMasiva(numeroFila, "Facultad vacía"));
            }
        }

        if (!errores.isEmpty()) {
            throw new ErrorCargaMasivaExcepcion(errores);
        }
    }

    private boolean esEmailValido(String email) {
        return PATRON_EMAIL.matcher(email).matches();
    }

    private boolean isBlank(String valor) {
        return valor == null || valor.isBlank();
    }
}
