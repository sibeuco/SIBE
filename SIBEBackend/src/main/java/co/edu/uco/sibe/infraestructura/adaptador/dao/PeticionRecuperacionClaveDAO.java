package co.edu.uco.sibe.infraestructura.adaptador.dao;

import co.edu.uco.sibe.infraestructura.adaptador.entidad.PeticionRecuperacionClaveEntidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.UUID;

public interface PeticionRecuperacionClaveDAO extends JpaRepository<PeticionRecuperacionClaveEntidad, UUID> {
    PeticionRecuperacionClaveEntidad findByCorreo(String correo);

    @Modifying
    @Query("DELETE FROM PeticionRecuperacionClaveEntidad p WHERE p.fecha < :fechaLimite")
    int eliminarExpiradas(@Param("fechaLimite") LocalDateTime fechaLimite);
}