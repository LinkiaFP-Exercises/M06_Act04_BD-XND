package app;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.modules.XMLResource;

public class Main {
    private static final String URI = "xmldb:exist://localhost:8080/exist/xmlrpc/db/";
    private static final String driver = "org.exist.xmldb.DatabaseImpl";

    public static void main(String[] args) {
        try {
            // Initialize database driver
            Class<?> cl = Class.forName(driver);
            Database database = (Database) cl.newInstance();
            DatabaseManager.registerDatabase(database);

            // Get the 'empleados' collection
            Collection empleadosCol = DatabaseManager.getCollection(URI + "empleados", "admin", "");
            if (empleadosCol == null) {
                System.out.println("The 'empleados' collection was not found.");
            } else {
                // Read and print 'empleados.xml'
                System.out.println("Contenido de 'empleados.xml':");
                printResourceContent(empleadosCol, "empleados.xml");
            }

            // Get the 'incidencias' collection
            Collection incidenciasCol = DatabaseManager.getCollection(URI + "incidencias", "admin", "");
            if (incidenciasCol == null) {
                System.out.println("The 'incidencias' collection was not found.");
            } else {
                // Read and print 'incidencias.xml'
                System.out.println("\nContenido de 'incidencias.xml':");
                printResourceContent(incidenciasCol, "incidencias.xml");
            }

        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void printResourceContent(Collection col, String resourceName) throws Exception {
        XMLResource resource = (XMLResource) col.getResource(resourceName);
        if (resource == null) {
            System.out.println(resourceName + " was not found in the collection.");
        } else {
            System.out.println(resource.getContent());
        }
    }
}
