package app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmldb.api.base.Collection;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(GestorBD.class);
    public static void main(String[] args) {
        try {
            Collection colEmpleados = new GestorBD().conectarColeccion("empleados");
            String xmlEmpleados = GestorBD.getResourceContent(colEmpleados, "empleados.xml");
            Empleados empleados = Serializer.toEmpleados(xmlEmpleados);
            if (empleados != null && empleados.getEmpleados() != null) {
                for (Empleado empleado : empleados.getEmpleados()) {
                    System.out.println(empleado);
                }
            }
        } catch (Exception e) {
            logger.warn("Stacktrace: ", e);
        }
    }
}

