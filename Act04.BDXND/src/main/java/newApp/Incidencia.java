package newApp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    // Método para convertir un String de fecha y hora al formato LocalDateTime
    public void setFechahoraByString(String fechahora) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        this.fechahora = LocalDateTime.parse(fechahora, formatter);
    }

    // Método para formatear la fecha y hora
    public String getFechahoraAsString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        return this.fechahora.format(formatter);
    }

    public void print() {
        System.out.println("ID: " + this.getId() +
                ", Origen: " + this.getOrigen() +
                ", Destino: " + this.getDestino() +
                ", Tipo: " + this.getTipo() +
                ", Detalle: " + this.getDetalle() +
                ", Fecha y Hora: " + this.getFechahoraAsString());
    }
}
