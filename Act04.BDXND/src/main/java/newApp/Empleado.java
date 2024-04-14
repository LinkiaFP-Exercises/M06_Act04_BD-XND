package newApp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Empleado {
    private String usuario;
    private String password;
    private String nombre;
    private String apellidos;
    private String direccion;
    private String telefono;
}
