package app;

import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.modules.XMLResource;

@SuppressWarnings("deprecation")
public class GestorBD {
    private static final String URI = "xmldb:exist://localhost:8080/exist/xmlrpc/db/";
    private static final String driver = "org.exist.xmldb.DatabaseImpl";
    private final String user;
    private final String pass;

    public GestorBD()  throws Exception {
            Class<?> cl = Class.forName(driver);
            Database database = (Database) cl.newInstance();
            DatabaseManager.registerDatabase(database);
            user = "admin";
            pass = "";
    }

    public Collection conectarColeccion(String nombreColeccion) throws Exception {
        return DatabaseManager.getCollection(URI + nombreColeccion, user, pass);
    }

    public static String getResourceContent(Collection col, String resourceName) throws Exception {
        XMLResource resource = (XMLResource) col.getResource(resourceName);
        if (resource == null) {
            System.out.println(resourceName + " was not found in the collection.");
            return null;
        } else {
            return resource.getContent().toString();
        }
    }

}
