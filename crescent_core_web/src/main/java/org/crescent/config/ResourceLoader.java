package org.crescent.config;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

public class ResourceLoader {

    private static final Logger logger = LoggerFactory.getLogger(ResourceLoader.class);
    private final ClassLoader classLoader;
    private Document document = null;
    private final String name;
    private InputStream inputStream = null;
    private URL url = null;

    public ResourceLoader(String name) {

        logger.info("ResourceLoader init..");

        this.name = name;
        this.classLoader = Thread.currentThread().getContextClassLoader();
        initInputStream();
    }

    private void initInputStream() {

        try {
            inputStream = this.classLoader.getResourceAsStream(name);

            if (inputStream == null) {
                inputStream = new FileInputStream(name);
            }

            if (inputStream == null) {
                logger.error("inputStream {} 를 지정된 경로에서 찾을 수 없습니다.", name);
            }

            url = this.classLoader.getResource(name);

            if (url == null) {
                url = new File(name).toURI().toURL();
            }

            if (url == null) {
                logger.error("url {} 를 지정된 경로에서 찾을 수 없습니다.", name);
            }

        } catch (Exception e) {
            logger.error("{}에 대한 resource를 찾지 못 했습니다.", name);
            throw new IllegalStateException(name + " 에 대한 resource를 찾지 못 했습니다.");
        }
    }

    protected InputStream getInputStream() {
        return inputStream;
    }

    protected URL getURL() {

        return url;
    }

    private void buildDocument(InputStream is) throws DocumentException {
        SAXReader saxReader = new SAXReader();

        try {
            document = saxReader.read(is);
        } catch (DocumentException e) {
            logger.error("build document {}.xml", name, e);
            throw e;
        }
    }

    public Document getDocument() throws DocumentException {
        if (document == null) {
            buildDocument(inputStream);
        }

        return document;
    }

}
