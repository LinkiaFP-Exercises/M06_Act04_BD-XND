package newApp;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.modules.CollectionManagementService;
import org.xmldb.api.modules.XMLResource;

import java.io.InputStream;

@SuppressWarnings("deprecation")
@Getter
public class GestorBD {
    private static final Logger logger = LoggerFactory.getLogger(GestorBD.class);
    private final String URI = "xmldb:exist://localhost:8080/exist/xmlrpc";
    private final String collectionPath = "/db/gestion";
    private final String admin = "admin";
    private final String password = "";  // Ajusta si es necesario.

    public void inicializar() throws Exception {
        // Inicializar y registrar la base de datos
        String driver = "org.exist.xmldb.DatabaseImpl";
        Class<?> cl = Class.forName(driver);
        Database database = (Database) cl.newInstance();
        database.setProperty("create-database", "true");
        DatabaseManager.registerDatabase(database);

        // Crear la colección si no existe
        crearColeccionSiNoExiste();

        // Subir archivos XML
        subirArchivoXML("empleados.xml");
        subirArchivoXML("incidencias.xml");
    }

    private void crearColeccionSiNoExiste() throws Exception {
        Collection parent = DatabaseManager.getCollection(URI + "/db", admin, password);
        CollectionManagementService mgt = (CollectionManagementService) parent.getService("CollectionManagementService", "1.0");

        Collection col = DatabaseManager.getCollection(URI + collectionPath, admin, password);
        if (col == null) {
            mgt.createCollection(collectionPath.substring(collectionPath.lastIndexOf("/") + 1));
        }
        if (col != null) {
            col.close();
        }
        parent.close();
    }

    private void subirArchivoXML(String resourceName) throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream(resourceName);
        if (is == null) {
            logger.error("Archivo no encontrado: " + resourceName);
        }
        String xmlContent = new String(is.readAllBytes());
        is.close();

        Collection col = DatabaseManager.getCollection(URI + collectionPath, admin, password);
        XMLResource resource = (XMLResource) col.createResource(resourceName, XMLResource.RESOURCE_TYPE);
        resource.setContent(xmlContent);
        col.storeResource(resource);
        col.close();
    }

    public void insertarEmpleado(Empleado nuevoEmpleado) throws Exception {
        // Obtener la colección donde se almacena el archivo empleados.xml
        Collection col = DatabaseManager.getCollection(URI + collectionPath, admin, password);
        if (col == null) {
            throw new Exception("No se pudo acceder a la colección.");
        }

        // Obtener el recurso empleados.xml
        XMLResource xmlResource = (XMLResource) col.getResource("empleados.xml");
        if (xmlResource == null) {
            throw new Exception("El recurso empleados.xml no se encuentra en la colección.");
        }

        // Convertir el contenido actual del XML en un documento manipulable
        Document doc = (Document) xmlResource.getContentAsDOM();
        Element root = doc.getDocumentElement();

        // Crear un nuevo elemento de empleado y agregarlo al documento
        Element nuevoEmpleadoElement = doc.createElement("empleado");
        nuevoEmpleadoElement.appendChild(createElement(doc, "usuario", nuevoEmpleado.getUsuario()));
        nuevoEmpleadoElement.appendChild(createElement(doc, "password", nuevoEmpleado.getPassword()));
        nuevoEmpleadoElement.appendChild(createElement(doc, "nombre", nuevoEmpleado.getNombre()));
        nuevoEmpleadoElement.appendChild(createElement(doc, "apellidos", nuevoEmpleado.getApellidos()));
        nuevoEmpleadoElement.appendChild(createElement(doc, "direccion", nuevoEmpleado.getDireccion()));
        nuevoEmpleadoElement.appendChild(createElement(doc, "telefono", nuevoEmpleado.getTelefono()));
        root.appendChild(nuevoEmpleadoElement);

        // Guardar el documento actualizado de nuevo en el recurso
        xmlResource.setContentAsDOM(doc);
        col.storeResource(xmlResource);
        col.close();

        logger.info("Empleado insertado con éxito: " + nuevoEmpleado.getUsuario());
    }

    private Element createElement(Document doc, String name, String value) {
        Element elem = doc.createElement(name);
        elem.appendChild(doc.createTextNode(value));
        return elem;
    }


}
