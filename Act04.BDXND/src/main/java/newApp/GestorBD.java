package newApp;

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

/**
 * Gestiona las operaciones de base de datos para la aplicación, incluyendo la creación de colecciones,
 * inserción y recuperación de incidencias y empleados.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 */
@SuppressWarnings("deprecation")
public class GestorBD {
    private static final Logger logger = LoggerFactory.getLogger(GestorBD.class);
    private final String URI = "xmldb:exist://localhost:8080/exist/xmlrpc";
    private final String collectionPath = "/db/gestion";
    private final String admin = "admin";
    private final String password = "";
    private static final String EMPLEADOS_XML = "empleados.xml";
    private static final String INCIDENCIAS_XML = "incidencias.xml";

    /**
     * Constructor que inicializa la base de datos y carga los recursos XML.
     * @throws Exception Si falla alguna operación de la base de datos.
     */
    public GestorBD() throws Exception {
        String driver = "org.exist.xmldb.DatabaseImpl";
        registerDatabase(driver);
        crearColeccionSiNoExiste();
        subirArchivoXML(EMPLEADOS_XML);
        subirArchivoXML(INCIDENCIAS_XML);
    }

    /**
     * Registra la base de datos eXist en el sistema.
     * @throws ClassNotFoundException Si la clase del driver no se encuentra.
     * @throws IllegalAccessException Si el acceso a la clase del driver está restringido.
     * @throws InstantiationException Si falla la instancia del driver.
     */
    private void registerDatabase(String driver) throws Exception {
        Class<?> cl = Class.forName(driver);
        Database database = (Database) cl.newInstance();
        database.setProperty("create-database", "true");
        DatabaseManager.registerDatabase(database);
    }

    /**
     * Crea la colección en la base de datos si no existe.
     * @throws Exception Si hay un error al acceder a la base de datos.
     */
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

    /**
     * Sube un archivo XML a la base de datos.
     * @param resourceName El nombre del recurso XML a subir.
     * @throws Exception Si falla la carga del archivo.
     */
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

    /**
     * Obtiene una referencia a la colección de la base de datos configurada.
     * Este método garantiza que se acceda a la colección deseada y maneja
     * la excepción en caso de que la colección no sea accesible.
     *
     * @return Una instancia de {@link Collection} conectada a la colección especificada.
     * @throws Exception Si no se puede acceder a la colección.
     */
    private Collection getCollection() throws Exception {
        Collection col = DatabaseManager.getCollection(URI + collectionPath, admin, password);
        if (col == null) {
            throw new Exception("No se pudo acceder a la colección.");
        }
        return col;
    }

    /**
     * Obtiene un recurso XML específico de una colección dada.
     * Este método se utiliza para recuperar un recurso XML por su nombre de una colección
     * especificada y maneja la excepción si el recurso no está disponible.
     *
     * @param col La colección de la cual recuperar el recurso.
     * @param resourceName El nombre del recurso XML a recuperar.
     * @return Una instancia de {@link XMLResource} que representa el recurso solicitado.
     * @throws Exception Si el recurso no se encuentra en la colección.
     */
    private XMLResource getXMLResource(Collection col, String resourceName) throws Exception {
        XMLResource xmlResource = (XMLResource) col.getResource(resourceName);
        if (xmlResource == null) {
            throw new Exception("El recurso " + resourceName + " no se encuentra en la colección.");
        }
        return xmlResource;
    }

    /**
     * Almacena un recurso XML en la colección y cierra la colección.
     * Este método guarda un recurso modificado o nuevo en la colección de la base de datos
     * y asegura que la colección se cierre correctamente después de la operación.
     *
     * @param col La colección en la que se debe almacenar el recurso.
     * @param resource El recurso XML a almacenar.
     * @throws Exception Si ocurre un error al almacenar el recurso o al cerrar la colección.
     */
    private void storeResource(Collection col, XMLResource resource) throws Exception {
        col.storeResource(resource);
        col.close();
    }

    /**
     * Inserta un nuevo empleado en la base de datos XML.
     * Este método crea un nuevo elemento XML para el empleado y lo añade al documento XML de empleados.
     * Luego guarda este documento modificado en la base de datos.
     *
     * @param nuevoEmpleado El objeto Empleado que contiene los datos a insertar.
     * @throws Exception Si hay un error al obtener la colección, al acceder al recurso XML,
     *                   o al guardar el recurso modificado.
     */
    public void insertarEmpleado(Empleado nuevoEmpleado) throws Exception {
        Collection col = getCollection();
        XMLResource xmlResource = getXMLResource(col, EMPLEADOS_XML);
        Document doc = (Document) xmlResource.getContentAsDOM();
        Element root = doc.getDocumentElement();
        // Crear y añadir el elemento empleado al documento XML
        Element nuevoEmpleadoElement = doc.createElement("empleado");
        nuevoEmpleadoElement.appendChild(createElement(doc, "usuario", nuevoEmpleado.getUsuario()));
        nuevoEmpleadoElement.appendChild(createElement(doc, "password", nuevoEmpleado.getPassword()));
        nuevoEmpleadoElement.appendChild(createElement(doc, "nombre", nuevoEmpleado.getNombre()));
        nuevoEmpleadoElement.appendChild(createElement(doc, "apellidos", nuevoEmpleado.getApellidos()));
        nuevoEmpleadoElement.appendChild(createElement(doc, "direccion", nuevoEmpleado.getDireccion()));
        nuevoEmpleadoElement.appendChild(createElement(doc, "telefono", nuevoEmpleado.getTelefono()));
        root.appendChild(nuevoEmpleadoElement);

        xmlResource.setContentAsDOM(doc);
        storeResource(col, xmlResource);
        logger.info("Empleado insertado con éxito: " + nuevoEmpleado.getUsuario());
    }

    /**
     * Crea un nuevo elemento XML con un valor de texto específico.
     * Este método facilita la creación de elementos XML individuales para ser usados en documentos XML.
     *
     * @param doc El documento al que pertenecerá el nuevo elemento.
     * @param name El nombre del elemento a crear.
     * @param value El valor de texto del elemento.
     * @return El elemento XML creado con el valor de texto incorporado.
     */
    private Element createElement(Document doc, String name, String value) {
        Element elem = doc.createElement(name);
        elem.appendChild(doc.createTextNode(value));
        return elem;
    }

    /**
     * Valida si existe un empleado con el nombre de usuario y contraseña especificados.
     * Este método busca en el archivo XML de empleados para encontrar un elemento que coincida
     * con los criterios de usuario y contraseña dados. Utiliza XPath para realizar la consulta.
     *
     * @param usuario El nombre de usuario del empleado a validar.
     * @param password La contraseña del empleado a validar.
     * @return true si se encuentra un empleado que coincida con el nombre de usuario y la contraseña,
     *         false en caso contrario.
     * @throws Exception Si hay un error al acceder a la colección o al evaluar la expresión XPath.
     */
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

    /**
     * Modifica los detalles de un empleado existente en la base de datos XML.
     * Este método busca un empleado específico por su nombre de usuario y actualiza sus datos.
     * Si el empleado no se encuentra, lanza una excepción.
     *
     * @param empleado El objeto Empleado con los datos actualizados.
     * @throws Exception Si el empleado no se encuentra o si hay un error al acceder al recurso o guardar los cambios.
     */
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

    /**
     * Actualiza o crea un nuevo nodo dentro de un nodo padre en un documento DOM.
     * Si el nodo hijo especificado ya existe bajo el nodo padre, actualiza su contenido de texto.
     * Si no existe, crea un nuevo nodo con el nombre y valor especificados y lo añade al nodo padre.
     *
     * @param doc El documento DOM donde se realiza la actualización.
     * @param parentNode El nodo padre al que se añadirá o en el que se actualizará el nodo hijo.
     * @param childNodeName El nombre del nodo hijo a actualizar o crear.
     * @param newValue El nuevo valor de texto para el nodo hijo.
     */
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

    /**
     * Cambia la contraseña de un empleado existente en la base de datos XML.
     * Este método primero verifica que la contraseña actual proporcionada sea correcta. Si la verificación es exitosa,
     * procede a actualizar la contraseña del empleado con la nueva contraseña proporcionada.
     * Si la contraseña actual no coincide o si el empleado no se encuentra, se lanzará una excepción.
     *
     * @param usuario El nombre de usuario del empleado cuya contraseña se va a cambiar.
     * @param contrasenaActual La contraseña actual del empleado, que debe ser verificada antes de realizar el cambio.
     * @param nuevaContrasena La nueva contraseña que se asignará al empleado.
     * @throws Exception Si la contraseña actual es incorrecta, si el empleado no se encuentra,
     *                   o si ocurre algún error durante la actualización del documento XML.
     */
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

    /**
     * Elimina un empleado de la base de datos XML.
     * Este método busca a un empleado por su nombre de usuario y, si lo encuentra, elimina su nodo correspondiente
     * del documento XML. Si el empleado no se encuentra, se lanza una excepción indicando que no se pudo localizar.
     *
     * @param usuario El nombre de usuario del empleado a eliminar.
     * @throws Exception Si el empleado no se encuentra en la base de datos o si hay un error al acceder a la base de datos
     *                   o al modificar el documento XML.
     */
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

    /**
     * Recupera una incidencia específica por su ID desde la base de datos XML.
     * Este método utiliza XPath para buscar una incidencia en el documento XML basado en el ID proporcionado.
     * Si la incidencia se encuentra, se extraen sus detalles y se devuelve un objeto Incidencia.
     * Si no se encuentra ninguna incidencia con el ID dado, se lanza una excepción.
     *
     * @param id El ID de la incidencia que se desea recuperar.
     * @return Un objeto Incidencia que contiene todos los detalles de la incidencia encontrada.
     * @throws Exception Si la incidencia con el ID especificado no existe o si hay un problema al acceder a la base de datos.
     */
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

    /**
     * Obtiene el valor de texto de un elemento específico dentro de un elemento padre dado.
     * Este método busca nodos con un nombre de etiqueta específico dentro del elemento proporcionado
     * y devuelve el contenido de texto del primer nodo encontrado. Si no se encuentran nodos
     * con el nombre de etiqueta dado, retorna {@code null}.
     *
     * @param elem El elemento padre en el que buscar el nodo hijo.
     * @param tagName El nombre de la etiqueta del nodo hijo cuyo valor de texto se desea recuperar.
     * @return El valor de texto del primer nodo hijo encontrado con la etiqueta especificada,
     *         o {@code null} si no se encuentra ningún nodo con esa etiqueta.
     */
    private String getTextValue(Element elem, String tagName) {
        NodeList nl = elem.getElementsByTagName(tagName);
        if (nl.getLength() > 0) {
            Node node = nl.item(0);
            return node.getTextContent();
        }
        return null;
    }

    /**
     * Recupera todas las incidencias registradas en la base de datos XML.
     * Este método ejecuta una consulta XPath para obtener todos los nodos de incidencia en el documento XML.
     * Cada nodo es convertido en un objeto Incidencia utilizando el método auxiliar {@code getIncidenciasFromNodeList}.
     * La lista completa de incidencias es entonces retornada.
     *
     * @return Una lista de objetos Incidencia que representan todas las incidencias almacenadas en la base de datos.
     * @throws Exception Si ocurre un error al acceder a la colección, al ejecutar la consulta XPath,
     *                   o al procesar los nodos de incidencias.
     */
    public List<Incidencia> listarTodasLasIncidencias() throws Exception {
        Collection col = getCollection();
        XMLResource xmlResource = getXMLResource(col, INCIDENCIAS_XML);
        Document doc = (Document) xmlResource.getContentAsDOM();
        XPath xpath = XPathFactory.newInstance().newXPath();

        String expression = "/incidencias/incidencia";
        NodeList incidenciaNodes = (NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET);
        final List<Incidencia> incidencias = getIncidenciasFromNodeList(incidenciaNodes);

        col.close();
        return incidencias;
    }

    /**
     * Inserta una nueva incidencia en la base de datos XML.
     * Este método calcula automáticamente el próximo ID disponible basándose en el ID máximo existente en
     * las incidencias actuales, y luego crea un nuevo elemento XML para la incidencia con todos los detalles proporcionados.
     * Finalmente, guarda este nuevo elemento en el documento XML de incidencias.
     *
     * @param nuevaIncidencia El objeto Incidencia que contiene los datos de la nueva incidencia a insertar.
     * @throws Exception Si ocurre un error al acceder a la colección, al leer o modificar el documento XML,
     *                   o al guardar el recurso modificado.
     */
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

    /**
     * Recupera todas las incidencias de la base de datos XML que fueron creadas por un empleado específico.
     * Este método utiliza una consulta XPath para filtrar las incidencias basadas en el campo 'origen'.
     * Devuelve una lista de incidencias que coincide con el origen especificado.
     *
     * @param origen El nombre de usuario del empleado que originó las incidencias.
     * @return Una lista de objetos Incidencia que representan todas las incidencias originadas por el empleado especificado.
     * @throws Exception Si ocurre un error al acceder a la colección, al ejecutar la consulta XPath,
     *                   o al procesar los nodos de incidencias.
     */
    public List<Incidencia> obtenerIncidenciasPorOrigen(String origen) throws Exception {
        Collection col = getCollection();
        XMLResource xmlResource = getXMLResource(col, INCIDENCIAS_XML);
        Document doc = (Document) xmlResource.getContentAsDOM();
        XPath xpath = XPathFactory.newInstance().newXPath();

        String expression = String.format("/incidencias/incidencia[origen='%s']", origen);
        NodeList incidenciaNodes = (NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET);
        final List<Incidencia> incidencias = getIncidenciasFromNodeList(incidenciaNodes);

        col.close();
        return incidencias;
    }

    /**
     * Recupera todas las incidencias dirigidas a un empleado específico desde la base de datos XML.
     * Este método utiliza una consulta XPath para filtrar las incidencias por el campo 'destino'.
     * Devuelve una lista de incidencias cuyo campo 'destino' coincide con el nombre de usuario proporcionado.
     *
     * @param destino El nombre de usuario del empleado destinatario de las incidencias.
     * @return Una lista de objetos Incidencia que representan todas las incidencias destinadas al empleado especificado.
     * @throws Exception Si hay un error al acceder a la colección, durante la ejecución de la consulta XPath,
     *                   o al procesar los nodos de incidencias.
     */
    public List<Incidencia> obtenerIncidenciasPorDestino(String destino) throws Exception {
        Collection col = getCollection();
        XMLResource xmlResource = getXMLResource(col, INCIDENCIAS_XML);
        Document doc = (Document) xmlResource.getContentAsDOM();
        XPath xpath = XPathFactory.newInstance().newXPath();

        String expression = String.format("/incidencias/incidencia[destino='%s']", destino);
        NodeList incidenciaNodes = (NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET);
        final List<Incidencia> incidencias = getIncidenciasFromNodeList(incidenciaNodes);

        col.close();
        return incidencias;
    }

    /**
     * Convierte una NodeList de nodos XML en una lista de objetos Incidencia.
     * Este método recorre cada nodo en la NodeList, extrayendo los datos necesarios para
     * crear un nuevo objeto Incidencia. Utiliza el método {@link #getTextValue(Element, String)}
     * para obtener los valores de texto de cada atributo requerido del nodo.
     *
     * @param incidenciaNodes La NodeList que contiene nodos XML de incidencias.
     * @return Una lista de objetos Incidencia poblada con los datos extraídos de los nodos XML.
     */
    private List<Incidencia> getIncidenciasFromNodeList(NodeList incidenciaNodes) {
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
        return incidencias;
    }

}
