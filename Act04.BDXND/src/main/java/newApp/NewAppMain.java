package newApp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class NewAppMain {
    private static final Logger logger = LoggerFactory.getLogger(NewAppMain.class);

    public static void main(String[] args) {

        try (Scanner scanner = new Scanner(System.in)) {
            GestorBD BD = new GestorBD();
            BD.inicializar();
            int opcion;

            do {
                System.out.println("\n*** Gestión de Empleados e Incidencias ***");
                System.out.println("1. Insertar un empleado nuevo");
                System.out.println("2. Validar la entrada de un empleado");
                System.out.println("3. Modificar el perfil de un empleado");
                System.out.println("4. Cambiar la contraseña de un empleado");
                System.out.println("5. Eliminar un empleado");
                System.out.println("6. Obtener una incidencia por ID");
                System.out.println("7. Listar todas las incidencias");
                System.out.println("8. Insertar una incidencia");
                System.out.println("9. Obtener las incidencias creadas por un empleado");
                System.out.println("10. Obtener las incidencias destinadas a un empleado");
                System.out.println("0. Salir");
                System.out.print("Elige una opción: ");
                opcion = scanner.nextInt();

                switch (opcion) {
                    case 1:
                        insertarEmpleado(scanner, BD);
                        break;

                    case 2:
                        // Llamada al método para validar un empleado
                        break;
                    case 3:
                        // Llamada al método para modificar un empleado
                        break;
                    case 4:
                        // Llamada al método para cambiar contraseña
                        break;
                    case 5:
                        // Llamada al método para eliminar un empleado
                        break;
                    case 6:
                        // Llamada al método para obtener una incidencia por ID
                        break;
                    case 7:
                        // Llamada al método para listar todas las incidencias
                        break;
                    case 8:
                        // Llamada al método para insertar una incidencia
                        break;
                    case 9:
                        // Llamada al método para obtener incidencias creadas por un empleado
                        break;
                    case 10:
                        // Llamada al método para obtener incidencias destinadas a un empleado
                        break;
                    case 0:
                        System.out.println("Saliendo...");
                        break;
                    default:
                        System.out.println("Opción no válida. Por favor, intente de nuevo.");
                }
            } while (opcion != 0);

        } catch (Exception e) {
            logger.error("Error durante la ejecución: ", e);
        }
    }

    private static void insertarEmpleado(Scanner scanner, GestorBD BD) {
        System.out.println("Insertar un nuevo empleado:");
        System.out.print("Usuario: ");
        String usuario = scanner.next();

        System.out.print("Contraseña: ");
        String contrasena = scanner.next();

        System.out.print("Nombre: ");
        String nombre = scanner.next();

        System.out.print("Apellidos: ");
        String apellidos = scanner.next();

        System.out.print("Dirección: ");
        scanner.nextLine();  // Consumir el newline
        String direccion = scanner.nextLine();

        System.out.print("Teléfono: ");
        String telefono = scanner.next();

        Empleado nuevoEmpleado = new Empleado(usuario, contrasena, nombre, apellidos, direccion, telefono);
        try {
            BD.insertarEmpleado(nuevoEmpleado);
            System.out.println("Empleado insertado correctamente.");
        } catch (Exception e) {
            logger.error("Error insertando el empleado: ", e);
        }
    }
}
