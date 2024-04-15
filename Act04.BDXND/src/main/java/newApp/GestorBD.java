package newApp;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.modules.CollectionManagementService;
import org.xmldb.api.modules.XMLResource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
@Getter
public class GestorBD {
    private static final Logger logger = LoggerFactory.getLogger(GestorBD.class);
    private final String URI = "xmldb:exist://localhost:8080/exist/xmlrpc";
    private final String collectionPath = "/db/gestion";
    private final String admin = "admin";
    private final String password = "";
    private static final String EMPLEADOS_XML = "empleados.xml";
    private static final String INCIDENCIAS_XML = "incidencias.xml";

    public GestorBD() throws Exception {
        String driver = "org.exist.xmldb.DatabaseImpl";
        registerDatabase(driver);
        crearColeccionSiNoExiste();
        subirArchivoXML(EMPLEADOS_XML);
        subirArchivoXML(INCIDENCIAS_XML);
    }

    private void registerDatabase(String driver) throws Exception {
        Class<?> cl = Class.forName(driver);
        Database database = (Database) cl.newInstance();
        database.setProperty("create-database", "true");
        DatabaseManager.registerDatabase(database);
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
            throw new Exception("Archivo no encontrado: " + resourceName);
        }
        String xmlContent = new String(is.readAllBytes());
        is.close();

        Collection col = getCollection();
        XMLResource resource = (XMLResource) col.createResource(resourceName, XMLResource.RESOURCE_TYPE);
        resource.setContent(xmlContent);
        storeResource(col, resource);
    }

    private Collection getCollection() throws Exception {
        Collection col = DatabaseManager.getCollection(URI + collectionPath, admin, password);
        if (col == null) {
            throw new Exception("No se pudo acceder a la colección.");
        }
        return col;
    }

    private XMLResource getXMLResource(Collection col, String resourceName) throws Exception {
        XMLResource xmlResource = (XMLResource) col.getResource(resourceName);
        if (xmlResource == null) {
            throw new Exception("El recurso " + resourceName + " no se encuentra en la colección.");
        }
        return xmlResource;
    }

    private void storeResource(Collection col, XMLResource resource) throws Exception {
        col.storeResource(resource);
        col.close();
    }

    public void insertarEmpleado(Empleado nuevoEmpleado) throws Exception {
        // Obtener la colección donde se almacena el archivo empleados.xml
        Collection col = getCollection();
        // Obtener el recurso empleados.xml
        XMLResource xmlResource = getXMLResource(col, EMPLEADOS_XML);

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
        storeResource(col, xmlResource);

        logger.info("Empleado insertado con éxito: " + nuevoEmpleado.getUsuario());
    }

    private Element createElement(Document doc, String name, String value) {
        Element elem = doc.createElement(name);
        elem.appendChild(doc.createTextNode(value));
        return elem;
    }

    public boolean validarEmpleado(String usuario, String password) throws Exception {
        Collection col = getCollection();
        XMLResource xmlResource = getXMLResource(col, EMPLEADOS_XML);
        Document doc = (Document) xmlResource.getContentAsDOM();
        XPath xpath = XPathFactory.newInstance().newXPath();
        String expression = String.format("/empleados/empleado[usuario='%s' and password='%s']", usuario, password);
        NodeList result = (NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET);
        col.close();
        // Verificar si encontramos al menos un nodo que coincida
        return result.getLength() > 0;
    }

    public void modificarEmpleado(Empleado empleado) throws Exception {
        Collection col = getCollection();
        XMLResource xmlResource = getXMLResource(col, EMPLEADOS_XML);
        Document doc = (Document) xmlResource.getContentAsDOM();
        XPath xpath = XPathFactory.newInstance().newXPath();

        // Actualizar el documento DOM
        String expression = String.format("/empleados/empleado[usuario='%s']", empleado.getUsuario());
        Node empleadoNode = (Node) xpath.evaluate(expression, doc, XPathConstants.NODE);
        if (empleadoNode == null) {
            throw new Exception("Empleado no encontrado con el usuario: " + empleado.getUsuario());
        }

        // Actualizar los datos del empleado
        updateNodeValue(doc, empleadoNode, "password", empleado.getPassword());
        updateNodeValue(doc, empleadoNode, "nombre", empleado.getNombre());
        updateNodeValue(doc, empleadoNode, "apellidos", empleado.getApellidos());
        updateNodeValue(doc, empleadoNode, "direccion", empleado.getDireccion());
        updateNodeValue(doc, empleadoNode, "telefono", empleado.getTelefono());

        // Guardar los cambios
        xmlResource.setContentAsDOM(doc);
        storeResource(col, xmlResource);

        logger.info("Perfil de empleado actualizado: " + empleado.getUsuario());
    }

    private void updateNodeValue(Document doc, Node parentNode, String childNodeName, String newValue) {
        NodeList nodes = ((Element) parentNode).getElementsByTagName(childNodeName);
        if (nodes.getLength() > 0) {
            Node node = nodes.item(0);
            node.setTextContent(newValue);
        } else {
            // Si el nodo no existe, lo creamos
            Element newElement = doc.createElement(childNodeName);
            newElement.setTextContent(newValue);
            parentNode.appendChild(newElement);
        }
    }

    public void cambiarContrasenaEmpleado(String usuario, String contrasenaActual, String nuevaContrasena) throws Exception {
        if (!validarEmpleado(usuario, contrasenaActual)) {
            throw new Exception("Contraseña actual incorrecta.");
        }

        Collection col = getCollection();
        XMLResource xmlResource = getXMLResource(col, EMPLEADOS_XML);
        Document doc = (Document) xmlResource.getContentAsDOM();
        XPath xpath = XPathFactory.newInstance().newXPath();

        String expression = String.format("/empleados/empleado[usuario='%s']", usuario);
        Node empleadoNode = (Node) xpath.evaluate(expression, doc, XPathConstants.NODE);
        if (empleadoNode == null) {
            throw new Exception("Empleado no encontrado con el usuario: " + usuario);
        }

        updateNodeValue(doc, empleadoNode, "password", nuevaContrasena);
        xmlResource.setContentAsDOM(doc);
        storeResource(col, xmlResource);

        logger.info("Contraseña cambiada con éxito para el usuario: " + usuario);
    }

    public void eliminarEmpleado(String usuario) throws Exception {
        Collection col = getCollection();
        XMLResource xmlResource = getXMLResource(col, EMPLEADOS_XML);
        Document doc = (Document) xmlResource.getContentAsDOM();
        XPath xpath = XPathFactory.newInstance().newXPath();

        String expression = String.format("/empleados/empleado[usuario='%s']", usuario);
        Node empleadoNode = (Node) xpath.evaluate(expression, doc, XPathConstants.NODE);
        if (empleadoNode == null) {
            throw new Exception("Empleado no encontrado con el usuario: " + usuario);
        }

        // Eliminar el nodo del empleado
        empleadoNode.getParentNode().removeChild(empleadoNode);
        xmlResource.setContentAsDOM(doc);
        storeResource(col, xmlResource);

        logger.info("Empleado eliminado con éxito: " + usuario);
    }

    public Incidencia obtenerIncidenciaPorId(String id) throws Exception {
        Collection col = getCollection();
        XMLResource xmlResource = getXMLResource(col, INCIDENCIAS_XML);
        Document doc = (Document) xmlResource.getContentAsDOM();
        XPath xpath = XPathFactory.newInstance().newXPath();

        String expression = String.format("/incidencias/incidencia[id='%s']", id);
        Node incidenciaNode = (Node) xpath.evaluate(expression, doc, XPathConstants.NODE);
        if (incidenciaNode == null) {
            throw new Exception("Incidencia no encontrada con el ID: " + id);
        }

        // Extraer detalles de la incidencia utilizando nombres de etiquetas
        Incidencia incidencia = new Incidencia();
        Element elem = (Element) incidenciaNode;
        incidencia.setId(Integer.parseInt(getTextValue(elem, "id")));
        incidencia.setOrigen(getTextValue(elem, "origen"));
        incidencia.setDestino(getTextValue(elem, "destino"));
        incidencia.setTipo(getTextValue(elem, "tipo"));
        incidencia.setDetalle(getTextValue(elem, "detalle"));
        incidencia.setFechahoraByString(getTextValue(elem, "fechahora"));

        col.close();
        return incidencia;
    }

    private String getTextValue(Element elem, String tagName) {
        NodeList nl = elem.getElementsByTagName(tagName);
        if (nl.getLength() > 0) {
            Node node = nl.item(0);
            return node.getTextContent();
        }
        return null;
    }

    public List<Incidencia> listarTodasLasIncidencias() throws Exception {
        Collection col = getCollection();
        XMLResource xmlResource = getXMLResource(col, INCIDENCIAS_XML);
        Document doc = (Document) xmlResource.getContentAsDOM();
        XPath xpath = XPathFactory.newInstance().newXPath();

        String expression = "/incidencias/incidencia";
        NodeList incidenciaNodes = (NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET);
        List<Incidencia> incidencias = new ArrayList<>();

        for (int i = 0; i < incidenciaNodes.getLength(); i++) {
            Node node = incidenciaNodes.item(i);
            Incidencia incidencia = new Incidencia();
            incidencia.setId(Integer.parseInt(getTextValue((Element)node, "id")));
            incidencia.setOrigen(getTextValue((Element)node, "origen"));
            incidencia.setDestino(getTextValue((Element)node, "destino"));
            incidencia.setTipo(getTextValue((Element)node, "tipo"));
            incidencia.setDetalle(getTextValue((Element)node, "detalle"));
            incidencia.setFechahoraByString(getTextValue((Element)node, "fechahora"));
            incidencias.add(incidencia);
        }

        col.close();
        return incidencias;
    }

    public void insertarIncidencia(Incidencia nuevaIncidencia) throws Exception {
        Collection col = getCollection();
        XMLResource xmlResource = getXMLResource(col, INCIDENCIAS_XML);
        Document doc = (Document) xmlResource.getContentAsDOM();
        Element root = doc.getDocumentElement();

        // Determinar el ID máximo actual recorriendo los nodos
        NodeList incidencias = root.getElementsByTagName("incidencia");
        int maxId = 0;
        for (int i = 0; i < incidencias.getLength(); i++) {
            Element el = (Element) incidencias.item(i);
            int id = Integer.parseInt(el.getElementsByTagName("id").item(0).getTextContent());
            if (id > maxId) {
                maxId = id;
            }
        }
        int nextId = maxId + 1; // Siguiente ID disponible

        // Crear un nuevo elemento de incidencia y agregarlo al documento
        Element incidenciaElement = doc.createElement("incidencia");
        incidenciaElement.appendChild(createElement(doc, "id", String.valueOf(nextId)));
        incidenciaElement.appendChild(createElement(doc, "origen", nuevaIncidencia.getOrigen()));
        incidenciaElement.appendChild(createElement(doc, "destino", nuevaIncidencia.getDestino()));
        incidenciaElement.appendChild(createElement(doc, "tipo", nuevaIncidencia.getTipo()));
        incidenciaElement.appendChild(createElement(doc, "detalle", nuevaIncidencia.getDetalle()));
        incidenciaElement.appendChild(createElement(doc, "fechahora", nuevaIncidencia.getFechahoraAsString()));
        root.appendChild(incidenciaElement);

        // Guardar el documento actualizado de nuevo en el recurso
        xmlResource.setContentAsDOM(doc);
        storeResource(col, xmlResource);

        logger.info("Nueva incidencia insertada con éxito: ID " + nextId);
    }

    public List<Incidencia> obtenerIncidenciasPorOrigen(String origen) throws Exception {
        Collection col = getCollection();
        XMLResource xmlResource = getXMLResource(col, INCIDENCIAS_XML);
        Document doc = (Document) xmlResource.getContentAsDOM();
        XPath xpath = XPathFactory.newInstance().newXPath();

        String expression = String.format("/incidencias/incidencia[origen='%s']", origen);
        NodeList incidenciaNodes = (NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET);
        List<Incidencia> incidencias = new ArrayList<>();

        for (int i = 0; i < incidenciaNodes.getLength(); i++) {
            Node node = incidenciaNodes.item(i);
            Incidencia incidencia = new Incidencia();
            incidencia.setId(Integer.parseInt(getTextValue((Element)node, "id")));
            incidencia.setOrigen(getTextValue((Element)node, "origen"));
            incidencia.setDestino(getTextValue((Element)node, "destino"));
            incidencia.setTipo(getTextValue((Element)node, "tipo"));
            incidencia.setDetalle(getTextValue((Element)node, "detalle"));
            incidencia.setFechahoraByString(getTextValue((Element)node, "fechahora"));
            incidencias.add(incidencia);
        }

        col.close();
        return incidencias;
    }







}
