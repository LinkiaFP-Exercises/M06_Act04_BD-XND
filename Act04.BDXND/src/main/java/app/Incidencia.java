package app;

public class Incidencia {
    private int id;
    private String origen;
    private String destino;
    private String tipo;
    private String detalle;
    private String fechahora;

    public Incidencia() {
    }

    public Incidencia(int id, String origen, String destino, String tipo, String detalle, String fechahora) {
        this.id = id;
        this.origen = origen;
        this.destino = destino;
        this.tipo = tipo;
        this.detalle = detalle;
        this.fechahora = fechahora;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public String getFechahora() {
        return fechahora;
    }

    public void setFechahora(String fechahora) {
        this.fechahora = fechahora;
    }

    @Override
    public String toString() {
        return "Incidencia{" +
                "id=" + id +
                ", origen='" + origen + '\'' +
                ", destino='" + destino + '\'' +
                ", tipo='" + tipo + '\'' +
                ", detalle='" + detalle + '\'' +
                ", fechahora='" + fechahora + '\'' +
                '}';
    }
}
