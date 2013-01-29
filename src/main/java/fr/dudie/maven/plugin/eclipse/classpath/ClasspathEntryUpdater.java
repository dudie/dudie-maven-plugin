package fr.dudie.maven.plugin.eclipse.classpath;

import org.apache.maven.artifact.Artifact;
import org.w3c.dom.Element;

/**
 * Updates a <classpath/> element of an eclipse .classpath XML document.
 * 
 * Updates the path to the up to date artifact.
 * 
 * @author Jeremie Huchet
 */
public class ClasspathEntryUpdater {

    public void handle(final Artifact artifact, final Element classpathEntry) {
        classpathEntry.setAttribute("path", artifact.getFile().getAbsolutePath());
    }
}
