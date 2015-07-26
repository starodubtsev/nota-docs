package org.romastar.notary.controller;

import org.romastar.notary.model.AppModel;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by roman on 26.07.14.
 */
public class SerializerController {

    private static SerializerController instance = new SerializerController();

    public static SerializerController getInstance() {
        return instance;
    }

    public void saveXML(Object target, String file) throws JAXBException, IOException {
        File outFile = new File(AppModel.getInstance().getPath4File(file));
        OutputStream os = new FileOutputStream(outFile);
        JAXBContext context = JAXBContext.newInstance(target.getClass());
        Marshaller m = context.createMarshaller();
        m.marshal(target, os);
        os.close();
    }

    public <T> Object loadXML(Class<T> _class, String file) throws JAXBException, IOException {
        File inFile = new File(AppModel.getInstance().getPath4File(file));
        JAXBContext context = JAXBContext.newInstance(_class);
        Unmarshaller u = context.createUnmarshaller();
        T result = (T) u.unmarshal(inFile);
        return result;
    }
}
