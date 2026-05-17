package co.edu.uco.sibe.infraestructura.adaptador.repositorio.comando;

import co.edu.uco.sibe.infraestructura.adaptador.dao.MiembroDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MiembroRepositorioComandoImplementacionTest {

    @Mock
    private MiembroDAO miembroDAO;

    private MiembroRepositorioComandoImplementacion repositorio;

    @BeforeEach
    void setUp() {
        repositorio = new MiembroRepositorioComandoImplementacion(miembroDAO);
    }

    @Test
    void deberiaDesactivarPorNumeroIdentificacion() {
        String identificacion = "1234567890";

        repositorio.desactivarPorNumeroIdentificacion(identificacion);

        verify(miembroDAO).desactivarPorNumeroIdentificacion(identificacion);
    }
}
