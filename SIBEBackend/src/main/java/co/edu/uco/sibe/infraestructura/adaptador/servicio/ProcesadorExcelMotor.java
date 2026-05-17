package co.edu.uco.sibe.infraestructura.adaptador.servicio;

import co.edu.uco.sibe.infraestructura.adaptador.mapeador.FilaExcelMapeador;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import static co.edu.uco.sibe.dominio.transversal.constante.MensajesErrorConstante.*;
import static co.edu.uco.sibe.dominio.transversal.constante.MensajesSistemaConstante.obtenerMensajeConParametro;

@Slf4j
@Component
public class ProcesadorExcelMotor {

    private static final Pattern PATRON_DIACRITICOS = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    public <T> List<T> procesarArchivo(MultipartFile archivo, FilaExcelMapeador<T> mapeador) {
        if (archivo.isEmpty()) {
            throw new IllegalArgumentException(EL_ARCHIVO_ESTA_VACIO);
        }

        List<T> resultados = new ArrayList<>();

        try (InputStream is = archivo.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet hoja = workbook.getSheetAt(0);
            Iterator<Row> filas = hoja.iterator();

            if (!filas.hasNext()) {
                throw new IllegalStateException(EL_ARCHIVO_ESTA_VACIO_O_NO_TIENE_ENCABEZADOS);
            }

            Row filaEncabezados = filas.next();
            Map<String, Integer> mapaColumnas = new HashMap<>();
            for (Cell celda : filaEncabezados) {
                if (celda != null && celda.getCellType() == CellType.STRING) {
                    mapaColumnas.put(normalizarEncabezado(celda.getStringCellValue()), celda.getColumnIndex());
                }
            }

            validarColumnasRequeridas(mapaColumnas, mapeador.obtenerColumnasRequeridas());

            while (filas.hasNext()) {
                Row fila = filas.next();
                if (filaVacia(fila)) continue;

                T dto = mapeador.mapearFila(fila, mapaColumnas);
                resultados.add(dto);
            }

        } catch (IllegalArgumentException | IllegalStateException e) {
            throw e;
        } catch (IOException e) {
            throw new RuntimeException(obtenerMensajeConParametro(ERROR_AL_PROCESAR_EL_ARCHIVO, e.getMessage()), e);
        } catch (Exception e) {
            throw new RuntimeException(obtenerMensajeConParametro(ERROR_INESPERADO, e.getMessage()), e);
        }

        return resultados;
    }

    private boolean filaVacia(Row row) {
        if (row == null) return true;
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK)
                return false;
        }
        return true;
    }

    private String normalizarEncabezado(String valor) {
        String normalizado = Normalizer.normalize(valor.trim().toUpperCase(), Normalizer.Form.NFD);
        return PATRON_DIACRITICOS.matcher(normalizado).replaceAll("");
    }

    private <T> void validarColumnasRequeridas(Map<String, Integer> mapaColumnas, List<String> columnasRequeridas) {
        if (columnasRequeridas.isEmpty()) {
            return;
        }

        List<String> faltantes = columnasRequeridas.stream()
                .filter(columna -> !mapaColumnas.containsKey(columna))
                .collect(Collectors.toList());

        if (!faltantes.isEmpty()) {
            String mensaje;
            if (faltantes.size() == 1) {
                mensaje = String.format(ERROR_FORMATO_COLUMNA_FALTANTE, faltantes.get(0));
            } else {
                mensaje = String.format(ERROR_FORMATO_COLUMNAS_FALTANTES, String.join("', '", faltantes));
            }
            throw new IllegalArgumentException(mensaje);
        }
    }
}