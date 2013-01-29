package fr.dudie.maven.plugin.eclipse.classpath;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.logging.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Opens an eclipse .classpath XML file and manage actions to update the
 * references to the libraries.
 * 
 * @author Jeremie Huchet
 */
public class EclipseClasspathHelper {

    private final Log logger;

    private final Document cpDoc;
    private final List<Element> cpEntries = new ArrayList<Element>();

    private ClasspathEntryUpdater updater;
    private ClasspathEntryCreator creator;

    public EclipseClasspathHelper(final File classpathFile, final Log logger) throws IOException, SAXException, ParserConfigurationException {
        this.logger = logger;

        this.cpDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(classpathFile);

        final NodeList cpElements = cpDoc.getDocumentElement().getElementsByTagName("classpathentry");
        for (int i = 0; i < cpElements.getLength(); i++) {
            final Node node = cpElements.item(i);
            if (Element.ELEMENT_NODE == node.getNodeType()) {
                cpEntries.add((Element) node);
            }
        }
        this.updater = new ClasspathEntryUpdater();
        this.creator = new ClasspathEntryCreator(cpDoc);
    }

    public void addOrUpdate(final Artifact dependency) {
        final ArtifactMatcher m = new ArtifactMatcher(dependency);

        final Iterator<Element> i = cpEntries.iterator();
        boolean updated = false;
        while (!updated && i.hasNext()) {
            final Element cpEntry = i.next();
            if (m.matches(cpEntry.getAttribute("path"))) {
                logger.debug(String.format("UPDATING classpathentry for %s", dependency.getId()));
                updater.handle(dependency, cpEntry);
                updated = true;
            }
        }
        if (!updated) {
            logger.debug(String.format("CREATING classpathentry for %s", dependency.getId()));
            creator.handle(dependency);
        }
    }

    Document getDocument() {
        return cpDoc;
    }

    public void writeTo(final File temporaryFile) throws TransformerFactoryConfigurationError, TransformerException {
        final StreamResult result = new StreamResult(temporaryFile);
        final Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(cpDoc), result);
    }
}
