package co.edu.uco.sibe.infraestructura.tarea;

import co.edu.uco.sibe.infraestructura.adaptador.dao.PeticionRecuperacionClaveDAO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@AllArgsConstructor
public class LimpiezaPeticionesRecuperacionClaveTarea {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' hh:mm a");

    private final PeticionRecuperacionClaveDAO peticionRecuperacionClaveDAO;

    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void ejecutarLimpieza() {
        var ahora = LocalDateTime.now();
        var fechaLimite = ahora.minusMinutes(5);

        var eliminadas = peticionRecuperacionClaveDAO.eliminarExpiradas(fechaLimite);

        log.info("Limpieza ejecutada: {} peticiones expiradas eliminadas el {}.",
                eliminadas, ahora.format(FORMATO_FECHA));
    }
}
