package newApp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase que representa a un empleado dentro del sistema.
 * Contiene detalles personales y de acceso del empleado, como nombre de usuario, contraseña, nombre,
 * apellidos, dirección y teléfono.
 *
 * Esta clase utiliza anotaciones de Lombok para reducir el código repetitivo y mejorar la legibilidad.
 * {@link lombok.Data} genera automáticamente métodos getter y setter para cada campo,
 * además de los métodos {@code equals}, {@code canEqual}, {@code hashCode}, {@code toString}.
 * {@link lombok.NoArgsConstructor} y {@link lombok.AllArgsConstructor} generan constructores sin argumentos y con todos los argumentos,
 * respectivamente, facilitando la creación e inicialización de instancias de esta clase.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 */
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
