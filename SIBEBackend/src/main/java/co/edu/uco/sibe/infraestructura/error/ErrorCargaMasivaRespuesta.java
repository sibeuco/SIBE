package co.edu.uco.sibe.infraestructura.error;

import co.edu.uco.sibe.aplicacion.transversal.ErrorFilaCargaMasiva;

import java.util.List;

public record ErrorCargaMasivaRespuesta(String nombreExcepcion, String mensaje, List<ErrorFilaCargaMasiva> errores) { }
