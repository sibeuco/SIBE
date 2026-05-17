package co.edu.uco.sibe.aplicacion.transversal;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResumenCargaMasiva {
    private final int actualizados;
    private final int nuevosCreados;
    private final int marcadosInactivos;
    private final int totalProcesados;
}
