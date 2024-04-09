package app;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Paths;
import java.util.Objects;

public class Other {
    public static void main(String[] args) throws URISyntaxException {
        String empleadosXMLPath = Paths.get(Objects.requireNonNull(Main.class.getClassLoader().getResource("incidencias.xml")).toURI()).toString();
        String dbUri = "http://localhost:8080/exist/rest/db/incidencias/incidencias.xml";
        String user = "admin";
        String password = ""; // Usar la contraseña correcta o dejar vacío si no hay

        try {
            URI uri = URI.create(dbUri);
            HttpRequest request = HttpRequest.newBuilder(uri)
                    .header("Content-Type", "application/xml")
                    .header("Authorization", "Basic " + java.util.Base64.getEncoder().encodeToString((user + ":" + password).getBytes()))
                    .PUT(HttpRequest.BodyPublishers.ofFile(Paths.get(empleadosXMLPath)))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Status Code: " + response.statusCode());
            System.out.println("Response: " + response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
