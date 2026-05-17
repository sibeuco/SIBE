package co.edu.uco.sibe.infraestructura.adaptador.repositorio.comando;

import co.edu.uco.sibe.dominio.modelo.RegistroAsistencia;
import co.edu.uco.sibe.dominio.puerto.comando.RegistroAsistenciaRepositorioComando;
import co.edu.uco.sibe.dominio.transversal.excepcion.ValorInvalidoExcepcion;
import co.edu.uco.sibe.infraestructura.adaptador.dao.RegistroAsistenciaDAO;
import co.edu.uco.sibe.infraestructura.adaptador.mapeador.RegistroAsistenciaMapeador;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import static co.edu.uco.sibe.dominio.transversal.constante.MensajesErrorConstante.PARTICIPANTE_YA_REGISTRADO_EN_EJECUCION;

@Repository
@AllArgsConstructor
public class RegistroAsistenciaRepositorioComandoImplementacion implements RegistroAsistenciaRepositorioComando {
    private final RegistroAsistenciaDAO registroAsistenciaDAO;
    private final RegistroAsistenciaMapeador registroAsistenciaMapeador;

    @Override
    public UUID guardar(RegistroAsistencia registroAsistencia) {
        try {
            var entidad = registroAsistenciaMapeador.construirEntidad(registroAsistencia);
            return registroAsistenciaDAO.save(entidad).getIdentificador();
        } catch (DataIntegrityViolationException e) {
            throw new ValorInvalidoExcepcion(PARTICIPANTE_YA_REGISTRADO_EN_EJECUCION);
        }
    }
}