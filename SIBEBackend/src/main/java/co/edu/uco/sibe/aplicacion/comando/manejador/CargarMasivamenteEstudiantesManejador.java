package co.edu.uco.sibe.aplicacion.comando.manejador;

import co.edu.uco.sibe.aplicacion.comando.fabrica.EstudianteFabrica;
import co.edu.uco.sibe.aplicacion.comando.validador.ValidadorEstudiantesCargaMasiva;
import co.edu.uco.sibe.aplicacion.puerto.servicio.ProcesadorArchivoEstudianteServicio;
import co.edu.uco.sibe.aplicacion.transversal.ComandoRespuesta;
import co.edu.uco.sibe.aplicacion.transversal.ResumenCargaMasiva;
import co.edu.uco.sibe.aplicacion.transversal.manejador.ManejadorComandoRespuesta;
import co.edu.uco.sibe.dominio.puerto.comando.EstudianteRepositorioComando;
import co.edu.uco.sibe.dominio.usecase.comando.CargarMasivamenteEstudiantesUseCase;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Component
@AllArgsConstructor
public class CargarMasivamenteEstudiantesManejador implements ManejadorComandoRespuesta<MultipartFile, ComandoRespuesta<ResumenCargaMasiva>> {
    private final ProcesadorArchivoEstudianteServicio procesadorArchivoEstudianteServicio;
    private final EstudianteFabrica estudianteFabrica;
    private final CargarMasivamenteEstudiantesUseCase cargarMasivamenteEstudiantesUseCase;
    private final EstudianteRepositorioComando estudianteRepositorioComando;
    private final ValidadorEstudiantesCargaMasiva validadorEstudiantesCargaMasiva;

    @Override
    @Transactional
    public ComandoRespuesta<ResumenCargaMasiva> ejecutar(MultipartFile comando) {
        var estudiantesComando = procesadorArchivoEstudianteServicio.procesar(comando);

        validadorEstudiantesCargaMasiva.validar(estudiantesComando);

        int prevActivosDesactivados = estudianteRepositorioComando.desactivarTodos();

        int actualizados = 0;
        int nuevos = 0;

        for (var estudianteComando : estudiantesComando) {
            var estudiante = estudianteFabrica.construir(estudianteComando);
            boolean esNuevo = this.cargarMasivamenteEstudiantesUseCase.ejecutar(estudiante);
            if (esNuevo) {
                nuevos++;
            } else {
                actualizados++;
            }
        }

        int marcadosInactivos = Math.max(0, prevActivosDesactivados - actualizados);
        var resumen = new ResumenCargaMasiva(actualizados, nuevos, marcadosInactivos, estudiantesComando.size());
        return new ComandoRespuesta<>(resumen);
    }
}