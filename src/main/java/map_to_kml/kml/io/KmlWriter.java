package map_to_kml.kml.io;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import map_to_kml.kml.data.KDocument;

public class KmlWriter {
    public KmlWriter() {
        // ...
    }

    public void writeKmlFile(KDocument doc, File outfile) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(KDocument.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(doc, outfile);
    }
}
