package co.edu.uco.sibe.infraestructura.adaptador.mapeador;

import org.apache.poi.ss.usermodel.Row;
import java.util.List;
import java.util.Map;

@FunctionalInterface
public interface FilaExcelMapeador<T> {
    T mapearFila(Row fila, Map<String, Integer> mapaColumnas);

    default List<String> obtenerColumnasRequeridas() {
        return List.of();
    }
}