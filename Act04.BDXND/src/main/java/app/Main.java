package app;

import net.xqj.exist.ExistXQDataSource;
import javax.xml.xquery.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.URISyntaxException;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        try {
            String uri = "xmldb:exist://localhost:8080/exist/xmlrpc/db";
            String user = "admin";
            String password = "";

            // 1. Conexión a eXist-db
            ExistXQDataSource dataSource = new ExistXQDataSource();
            dataSource.setProperty("serverName", "localhost");
            dataSource.setProperty("port", "8080");
            dataSource.setUser(user);
            dataSource.setPassword(password);
            XQConnection conn = dataSource.getConnection();

            // 2. Cargar los Archivos XML en la Base de Datos
            // Asegúrate de que los archivos están en src/main/resources
            String empleadosXMLPath = Paths.get(Objects.requireNonNull(Main.class.getClassLoader().getResource("empleados.xml")).toURI()).toString();
            String incidenciasXMLPath = Paths.get(Objects.requireNonNull(Main.class.getClassLoader().getResource("incidencias.xml")).toURI()).toString();

            // Lee los archivos XML como Strings
            String empleadosXML = new String(Files.readAllBytes(Paths.get(empleadosXMLPath)));
            String incidenciasXML = new String(Files.readAllBytes(Paths.get(incidenciasXMLPath)));

            // Utiliza XQJ para cargar los archivos en eXist-db
            loadXML(conn, "/db/empleados/empleados.xml", empleadosXML);
            loadXML(conn, "/db/incidencias/incidencias.xml", incidenciasXML);

            System.out.println("Archivos XML cargados exitosamente.");

            // 3. Leer los Datos y Mostrarlos en la Consola
            readAndPrintXML(conn, "for $x in doc('/db/empleados/empleados.xml')//empleado return $x");

            // Cerrar la conexión
            conn.close();

        } catch (URISyntaxException | IOException | XQException e) {
            e.printStackTrace();
        }
    }
    private static void loadXML(XQConnection conn, String docPath, String xmlContent) throws XQException {
        XQPreparedExpression expr = conn.prepareExpression("let $doc := " + xmlContent + " return xmldb:store('" + docPath + "', 'mydoc.xml', $doc)");
        expr.executeQuery().close();
    }

    private static void readAndPrintXML(XQConnection conn, String xquery) throws XQException {
        XQPreparedExpression expr = conn.prepareExpression(xquery);
        XQResultSequence result = expr.executeQuery();
        while (result.next()) {
            System.out.println(result.getItemAsString(null));
        }
    }
}
