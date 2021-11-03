package settings;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class Settings {
    private static final String SETTINGS_FILE = "src\\main\\resources\\settings.xml";

    private static final String TAG_NAME = "chatSettings";
    private static final String PORT = "port";
    private static final String HOST = "host";
    private static final String LOG = "logFile";

    private Element set;

    private static Settings settings;

    public static Settings getInstance() {
        if (settings == null) {
            settings = new Settings();
        }
        return settings;
    }

    private Settings() {
        try {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(SETTINGS_FILE));
            Node settings = doc.getElementsByTagName(TAG_NAME).item(0);
            set = (Element) settings;
        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public String getPORT() {
        return set.getAttribute(PORT);
    }

    public String getHOST() {
        return set.getAttribute(HOST);
    }

    public String getLOG() {
            return set.getAttribute(LOG);
    }
}
