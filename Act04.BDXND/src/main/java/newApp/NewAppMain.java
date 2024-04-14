package newApp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NewAppMain {
    private static final Logger logger = LoggerFactory.getLogger(NewAppMain.class);

    public static void main(String[] args) {
        try {
            final GestorBD BD = new GestorBD();
            BD.inicializar();
        } catch (Exception e) {
            logger.error("Stacktrace: ", e);
        }
    }
}
