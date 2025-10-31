package map_to_kml;

import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * Class with the main static method to start an application built by the Spring
 * framework XML passed on the command line
 */
public class MainApp {
    private static final org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager
            .getLogger(MainApp.class);

    public MainApp() {
        // ...
    }

    public static void main(String[] args) {
        String xml = null;

        for (int i = 0; i < args.length; i++) {
            if ("-x".equals(args[i])) {
                xml = args[++i];
            }
        }

        LOGGER.info(">>> Using parameters:");
        LOGGER.info("xml=" + xml);

        try {
            doStuff(xml);
        } catch (Exception e) {
            LOGGER.error("Failed", e);
        }
    }

    private static void doStuff(String xml) throws Exception {

        try (FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext(xml)) {
            LOGGER.debug(xml + " --- STARTING");
            context.start();
            LOGGER.debug(xml + " --- STARTED");

            LOGGER.debug(xml + " --- STOPPING");
            context.stop();

            LOGGER.debug(xml + " --- CLOSING");
            context.close();
            LOGGER.debug(xml + " --- CLOSED");

            // We are done
            System.exit(0);
        }

    }
}
