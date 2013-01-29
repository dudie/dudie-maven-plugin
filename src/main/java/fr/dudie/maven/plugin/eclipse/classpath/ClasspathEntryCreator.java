package fr.dudie.maven.plugin.eclipse.classpath;

import org.apache.maven.artifact.Artifact;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Adds a <classpath/> element to an eclipse .classpath XML document.
 * 
 * @author Jeremie Huchet
 */
public class ClasspathEntryCreator {

    private final Document classpathDocument;

    public ClasspathEntryCreator(final Document classpathDocument) {
        this.classpathDocument = classpathDocument;
    }

    public void handle(final Artifact dependency) {
        final Element newCpEntry = classpathDocument.createElement("classpathentry");
        newCpEntry.setAttribute("kind", "lib");
        newCpEntry.setAttribute("path", dependency.getFile().getAbsolutePath());
        classpathDocument.getDocumentElement().appendChild(newCpEntry);
    }

}
