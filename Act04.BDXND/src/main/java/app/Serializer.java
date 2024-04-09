package app;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.io.StringReader;
import java.io.StringWriter;

public class Serializer {

    public static String serializar(Empleados empleados) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Empleados.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            StringWriter writer = new StringWriter();
            marshaller.marshal(empleados, writer);
            return writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Empleados toEmpleados(String xml) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Empleados.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            return (Empleados) unmarshaller.unmarshal(new StringReader(xml));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}

