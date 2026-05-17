package co.edu.uco.sibe.infraestructura.tarea;

import co.edu.uco.sibe.infraestructura.adaptador.dao.PeticionRecuperacionClaveDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LimpiezaPeticionesRecuperacionClaveTareaTest {

    @Mock
    private PeticionRecuperacionClaveDAO peticionRecuperacionClaveDAO;

    private LimpiezaPeticionesRecuperacionClaveTarea tarea;

    @BeforeEach
    void setUp() {
        tarea = new LimpiezaPeticionesRecuperacionClaveTarea(peticionRecuperacionClaveDAO);
    }

    @Test
    void deberiaEliminarPeticionesExpiradasYRegistrarLog() {
        when(peticionRecuperacionClaveDAO.eliminarExpiradas(any(LocalDateTime.class))).thenReturn(120);

        var antes = LocalDateTime.now().minusMinutes(5);
        tarea.ejecutarLimpieza();
        var despues = LocalDateTime.now().minusMinutes(5);

        ArgumentCaptor<LocalDateTime> captor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(peticionRecuperacionClaveDAO).eliminarExpiradas(captor.capture());

        var fechaLimiteUsada = captor.getValue();
        assertTrue(!fechaLimiteUsada.isBefore(antes) && !fechaLimiteUsada.isAfter(despues));
    }

    @Test
    void deberiaEjecutarCorrectamenteCuandoNoHayPeticionesExpiradas() {
        when(peticionRecuperacionClaveDAO.eliminarExpiradas(any(LocalDateTime.class))).thenReturn(0);

        tarea.ejecutarLimpieza();

        verify(peticionRecuperacionClaveDAO).eliminarExpiradas(any(LocalDateTime.class));
    }
}
