package co.edu.uco.sibe.aplicacion.comando.manejador;

import co.edu.uco.sibe.aplicacion.comando.fabrica.EmpleadoFabrica;
import co.edu.uco.sibe.aplicacion.comando.validador.ValidadorEmpleadosCargaMasiva;
import co.edu.uco.sibe.aplicacion.puerto.servicio.ProcesadorArchivoEmpleadoServicio;
import co.edu.uco.sibe.aplicacion.transversal.ComandoRespuesta;
import co.edu.uco.sibe.aplicacion.transversal.ResumenCargaMasiva;
import co.edu.uco.sibe.aplicacion.transversal.manejador.ManejadorComandoRespuesta;
import co.edu.uco.sibe.dominio.puerto.comando.EmpleadoRepositorioComando;
import co.edu.uco.sibe.dominio.usecase.comando.CargarMasivamenteEmpleadosUseCase;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Component
@AllArgsConstructor
public class CargarMasivamenteEmpleadosManejador implements ManejadorComandoRespuesta<MultipartFile, ComandoRespuesta<ResumenCargaMasiva>> {
    private final ProcesadorArchivoEmpleadoServicio procesadorArchivoEmpleadoServicio;
    private final EmpleadoFabrica empleadoFabrica;
    private final CargarMasivamenteEmpleadosUseCase cargarMasivamenteEmpleadosUseCase;
    private final EmpleadoRepositorioComando empleadoRepositorioComando;
    private final ValidadorEmpleadosCargaMasiva validadorEmpleadosCargaMasiva;

    @Override
    @Transactional
    public ComandoRespuesta<ResumenCargaMasiva> ejecutar(MultipartFile comando) {
        var empleadosComando = procesadorArchivoEmpleadoServicio.procesar(comando);

        validadorEmpleadosCargaMasiva.validar(empleadosComando);

        int prevActivosDesactivados = empleadoRepositorioComando.desactivarTodos();

        int actualizados = 0;
        int nuevos = 0;

        for (var empleadoComando : empleadosComando) {
            var empleado = empleadoFabrica.construir(empleadoComando);
            boolean esNuevo = this.cargarMasivamenteEmpleadosUseCase.ejecutar(empleado);
            if (esNuevo) {
                nuevos++;
            } else {
                actualizados++;
            }
        }

        int marcadosInactivos = Math.max(0, prevActivosDesactivados - actualizados);
        var resumen = new ResumenCargaMasiva(actualizados, nuevos, marcadosInactivos, empleadosComando.size());
        return new ComandoRespuesta<>(resumen);
    }
}