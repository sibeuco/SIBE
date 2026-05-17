package co.edu.uco.sibe.aplicacion.comando.validador;

import co.edu.uco.sibe.aplicacion.comando.DatosEmpleadoComando;
import co.edu.uco.sibe.aplicacion.transversal.ErrorCargaMasivaExcepcion;
import co.edu.uco.sibe.aplicacion.transversal.ErrorFilaCargaMasiva;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ValidadorEmpleadosCargaMasiva {

    public void validar(List<DatosEmpleadoComando> empleados) {
        List<ErrorFilaCargaMasiva> errores = new ArrayList<>();
        Set<String> identificacionesVistas = new HashSet<>();

        for (int i = 0; i < empleados.size(); i++) {
            int numeroFila = i + 2;
            DatosEmpleadoComando empleado = empleados.get(i);

            if (isBlank(empleado.getNombre())) {
                errores.add(new ErrorFilaCargaMasiva(numeroFila, "Nombre vacío"));
            }

            if (isBlank(empleado.getIdentificacion())) {
                errores.add(new ErrorFilaCargaMasiva(numeroFila, "Identificación vacía"));
            } else if (!identificacionesVistas.add(empleado.getIdentificacion())) {
                errores.add(new ErrorFilaCargaMasiva(numeroFila,
                        "Documento duplicado: " + empleado.getIdentificacion()));
            }

            if (isBlank(empleado.getIdCarnet())) {
                errores.add(new ErrorFilaCargaMasiva(numeroFila, "ID de carnet vacío"));
            }

            if (isBlank(empleado.getGrupoRelacionLaboral())) {
                errores.add(new ErrorFilaCargaMasiva(numeroFila, "Grupo de relación laboral vacío"));
            }
        }

        if (!errores.isEmpty()) {
            throw new ErrorCargaMasivaExcepcion(errores);
        }
    }

    private boolean isBlank(String valor) {
        return valor == null || valor.isBlank();
    }
}
