package newApp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class NewAppMain {
    private static final Logger logger = LoggerFactory.getLogger(NewAppMain.class);

    public static void main(String[] args) {

        try (Scanner scanner = new Scanner(System.in)) {
            GestorBD BD = new GestorBD();
            int opcion;

            do {
                printMenu();
                opcion = scanner.nextInt();

                switch (opcion) {
                    case 1: insertarEmpleado(scanner, BD); break;
                    case 2: validarEmpleado(scanner, BD); break;
                    case 3: modificarEmpleado(scanner, BD); break;
                    case 4: cambiarPassEmpleado(scanner, BD); break;
                    case 5: eliminarEmpleado(scanner, BD); break;
                    case 6: getIncidenciaById(scanner, BD); break;
                    case 7: listAllIncidencias(BD); break;
                    case 8: insertIncidencia(scanner, BD); break;
                    case 9: listIncideciaByOrgin(scanner, BD); break;
                    case 10: listIncidenciasByDestiny(scanner, BD); break;
                    case 0: System.out.println("Saliendo..."); break;
                    default: System.out.println("Opción no válida. Por favor, intente de nuevo.");
                }
            } while (opcion != 0);

        } catch (Exception e) {
            logger.error("Error durante la ejecución: ", e);
        }
    }

    private static void listIncidenciasByDestiny(Scanner scanner, GestorBD BD) {
        scanner.nextLine();
        System.out.println("Obtener las incidencias destinadas a un empleado:");
        System.out.print("Ingrese el nombre de usuario del empleado destinatario: ");
        String usuarioDestino = scanner.nextLine();

        try {
            List<Incidencia> incidencias = BD.obtenerIncidenciasPorDestino(usuarioDestino);
            if (incidencias.isEmpty()) {
                System.out.println("No hay incidencias destinadas al usuario: " + usuarioDestino);
            } else {
                incidencias.forEach(Incidencia::print);
            }
        } catch (Exception e) {
            logger.error("Error al obtener las incidencias por destino: ", e);
            System.out.println(e.getMessage());
        }
    }

    private static void listIncideciaByOrgin(Scanner scanner, GestorBD BD) {
        scanner.nextLine();
        System.out.println("Obtener las incidencias creadas por un empleado:");
        System.out.print("Ingrese el nombre de usuario del empleado: ");
        String usuarioOrigen = scanner.nextLine();

        try {
            List<Incidencia> incidencias = BD.obtenerIncidenciasPorOrigen(usuarioOrigen);
            if (incidencias.isEmpty()) {
                System.out.println("No hay incidencias registradas por el usuario: " + usuarioOrigen);
            } else {
                incidencias.forEach(Incidencia::print);
            }
        } catch (Exception e) {
            logger.error("Error al obtener las incidencias por origen: ", e);
            System.out.println(e.getMessage());
        }
    }

    private static void insertIncidencia(Scanner scanner, GestorBD BD) {
        scanner.nextLine(); // Esto consume el '\n' que queda pendiente en el buffer
        System.out.println("Insertar una nueva incidencia:");
        System.out.print("Origen (Usuario): ");
        String origen = scanner.nextLine();
        System.out.print("Destino (Usuario): ");
        String destino = scanner.nextLine();
        System.out.print("Tipo (Urgente/Normal): ");
        String tipo = scanner.nextLine().toUpperCase().substring(0,1);
        if (!tipo.matches("^[UN]$")) {
            tipo = "N";  // Asigna 'N' si el tipo no es 'U' o 'N'
        }
        System.out.print("Detalle de la incidencia: ");
        String detalle = scanner.nextLine();

        try {
            Incidencia nuevaIncidencia = new Incidencia();
            nuevaIncidencia.setOrigen(origen);
            nuevaIncidencia.setDestino(destino);
            nuevaIncidencia.setTipo(tipo);
            nuevaIncidencia.setDetalle(detalle);
            nuevaIncidencia.setFechahora(LocalDateTime.now());
            BD.insertarIncidencia(nuevaIncidencia);
            System.out.println("Incidencia insertada correctamente.");
        } catch (Exception e) {
            logger.error("Error al insertar la incidencia: ", e);
            System.out.println(e.getMessage());
        }
    }

    private static void listAllIncidencias(GestorBD BD) {
        System.out.println("Listar todas las incidencias:");
        try {
            List<Incidencia> incidencias = BD.listarTodasLasIncidencias();
            if (incidencias.isEmpty()) {
                System.out.println("No hay incidencias registradas.");
            } else {
                incidencias.forEach(Incidencia::print);
            }
        } catch (Exception e) {
            logger.error("Error al listar todas las incidencias: ", e);
            System.out.println(e.getMessage());
        }
    }

    private static void getIncidenciaById(Scanner scanner, GestorBD BD) {
        System.out.println("Obtener una incidencia por ID:");
        System.out.print("Ingrese el ID de la incidencia a obtener: ");
        String incidenciaId = scanner.next();

        try {
            Incidencia incidencia = BD.obtenerIncidenciaPorId(incidenciaId);
            System.out.println("Detalles de la incidencia:");
            incidencia.print();
        } catch (Exception e) {
            logger.error("Error al obtener la incidencia: ", e);
            System.out.println(e.getMessage());
        }
    }

    private static void eliminarEmpleado(Scanner scanner, GestorBD BD) {
        System.out.println("Eliminar un empleado:");
        System.out.print("Ingrese el nombre de usuario del empleado a eliminar: ");
        String usernameToDelete = scanner.next();

        try {
            BD.eliminarEmpleado(usernameToDelete);
            System.out.println("Empleado eliminado correctamente.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void cambiarPassEmpleado(Scanner scanner, GestorBD BD) {
        System.out.println("Cambiar la contraseña de un empleado:");
        System.out.print("Usuario: ");
        String user = scanner.next();

        System.out.print("Contraseña actual: ");
        String currentPassword = scanner.next();

        System.out.print("Nueva contraseña: ");
        String newPassword = scanner.next();

        try {
            BD.cambiarContrasenaEmpleado(user, currentPassword, newPassword);
            System.out.println("La contraseña ha sido cambiada exitosamente.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void modificarEmpleado(Scanner scanner, GestorBD BD) {
        System.out.println("Modificar el perfil de un empleado:");
        System.out.print("Usuario (identificador único): ");
        String modUsuario = scanner.next();

        System.out.print("Nueva Contraseña: ");
        String modContrasena = scanner.next();

        System.out.print("Nuevo Nombre: ");
        String modNombre = scanner.next();

        System.out.print("Nuevos Apellidos: ");
        String modApellidos = scanner.next();

        System.out.print("Nueva Dirección: ");
        scanner.nextLine();  // Consumir el newline
        String modDireccion = scanner.nextLine();

        System.out.print("Nuevo Teléfono: ");
        String modTelefono = scanner.next();

        Empleado modEmpleado = new Empleado(modUsuario, modContrasena, modNombre, modApellidos, modDireccion, modTelefono);
        try {
            BD.modificarEmpleado(modEmpleado);
            System.out.println("Empleado modificado correctamente.");
        } catch (Exception e) {
            logger.error("Error al modificar el empleado: ", e);
        }
    }

    private static void validarEmpleado(Scanner scanner, GestorBD BD) {
        System.out.println("Validar la entrada de un empleado:");
        System.out.print("Usuario: ");
        String usuario = scanner.next();

        System.out.print("Contraseña: ");
        String contrasena = scanner.next();

        try {
            boolean esValido = BD.validarEmpleado(usuario, contrasena);
            if (esValido) {
                System.out.println("Validación exitosa: el usuario y contraseña son correctos.");
            } else {
                System.out.println("Error: usuario o contraseña incorrectos.");
            }
        } catch (Exception e) {
            logger.error("Error al validar el empleado: ", e);
        }
    }

    private static void printMenu() {
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
