package newApp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Clase que representa una incidencia dentro del sistema.
 * Cada incidencia registra un evento o situación que involucra a empleados, como remitente y destinatario,
 * y tiene asociada una fecha y hora específicas, además de un tipo y detalle que describen la naturaleza del evento.
 *
 * Utiliza anotaciones de Lombok para reducir la cantidad de código boilerplate:
 * {@link lombok.Data} - Genera automáticamente los métodos getter, setter, {@code equals}, {@code hashCode} y {@code toString}.
 * {@link lombok.NoArgsConstructor} - Genera un constructor sin argumentos.
 * {@link lombok.AllArgsConstructor} - Genera un constructor que acepta un argumento para cada campo en la clase.
 *
 * También incluye métodos personalizados para manejar la conversión de la fecha y hora desde y hacia formatos de cadena.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Incidencia {

    private int id;
    private String origen;
    private String destino;
    private String tipo;
    private String detalle;
    private LocalDateTime fechahora;

    /**
     * Convierte un String de fecha y hora al formato LocalDateTime usando un patrón específico.
     * @param fechahora Cadena que representa la fecha y hora en formato 'yyyy/MM/dd HH:mm:ss'.
     */
    public void setFechahoraByString(String fechahora) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        this.fechahora = LocalDateTime.parse(fechahora, formatter);
    }

    /**
     * Obtiene la representación de cadena de la fecha y hora de la incidencia en formato 'yyyy/MM/dd HH:mm:ss'.
     * @return Cadena que representa la fecha y hora de la incidencia.
     */
    public String getFechahoraAsString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        return this.fechahora.format(formatter);
    }

    /**
     * Imprime en consola la información de la incidencia formateada.
     */
    public void print() {
        System.out.println("ID: " + this.getId() +
                ", Origen: " + this.getOrigen() +
                ", Destino: " + this.getDestino() +
                ", Tipo: " + this.getTipo() +
                ", Detalle: " + this.getDetalle() +
                ", Fecha y Hora: " + this.getFechahoraAsString());
    }
}
