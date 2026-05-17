package co.edu.uco.sibe.aplicacion.transversal;

import java.util.List;

public class ErrorCargaMasivaExcepcion extends RuntimeException {

    private final List<ErrorFilaCargaMasiva> errores;

    public ErrorCargaMasivaExcepcion(List<ErrorFilaCargaMasiva> errores) {
        super("Carga fallida. Se encontraron " + errores.size() + " error(es) en el archivo.");
        this.errores = List.copyOf(errores);
    }

    public List<ErrorFilaCargaMasiva> getErrores() {
        return errores;
    }
}
