package fr.dudie.maven.plugin.eclipse;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.dependency.AbstractResolveMojo;
import org.apache.maven.plugin.dependency.utils.DependencyStatusSets;
import org.apache.maven.plugin.dependency.utils.filters.ResolveFileFilter;
import org.apache.maven.plugin.dependency.utils.markers.SourcesFileMarkerHandler;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.shared.artifact.filter.collection.ArtifactsFilter;
import org.xml.sax.SAXException;

import fr.dudie.maven.plugin.eclipse.classpath.ClasspathFileComparator;
import fr.dudie.maven.plugin.eclipse.classpath.EclipseClasspathHelper;

/**
 * Goal which updates the .classpath file for eclipse.
 */
@Mojo(name = "update-eclipse-classpath", requiresDependencyResolution = ResolutionScope.TEST, requiresProject = true, requiresDirectInvocation = true)
public class GenerateEclipseClasspathFile extends AbstractResolveMojo {

    /**
     * Eclipse .classpath file.
     */
    @Parameter(defaultValue = ".classpath")
    private File classpathFile;

    /**
     * The working file.
     */
    @Parameter(defaultValue = "${project.build.directory}/eclipse-classpath.xml")
    private File temporaryFile;

    /**
     * Backup of the eclipse .classpath file before erasing.
     */
    @Parameter(defaultValue = "${project.build.directory}/eclipse-classpath-backup.xml")
    private File backupFile;

    @Override
    public void execute() throws MojoExecutionException {
        final DependencyStatusSets results = this.getDependencySets(true);

        try {
            final EclipseClasspathHelper helper = new EclipseClasspathHelper(classpathFile, getLog());

            for (final Artifact dependency : results.getResolvedDependencies()) {
                helper.addOrUpdate(dependency);
            }
            temporaryFile.getParentFile().mkdirs();
            helper.writeTo(temporaryFile);
        } catch (final IOException | SAXException | ParserConfigurationException | TransformerFactoryConfigurationError | TransformerException e) {
            throw new MojoExecutionException(String.format("Unable to update eclipse classpath configuration: %s", e.getMessage()), e);
        }

        try {
            System.out.println(new ClasspathFileComparator(classpathFile, temporaryFile));
        } catch (final IOException e) {
            throw new MojoExecutionException(String.format("Unable to compare original eclipse classpath to the new generated configuration: %s",
                    e.getMessage()), e);
        }

        boolean continueUpdate = false;
        try {
            continueUpdate = askForConfirmation();
        } catch (final IOException e) {
            throw new MojoExecutionException(String.format("Unable to read input: %s", e.getMessage()), e);
        }

        if (!continueUpdate) {
            System.out.println(String.format("Generated classpath file is available at %s", temporaryFile));
        } else {
            try {
                FileUtils.copyFile(classpathFile, backupFile);
            } catch (final IOException e) {
                throw new MojoExecutionException(String.format("Unable to backup original classpath file: %s", e.getMessage()), e);
            }
            try {
                FileUtils.copyFile(temporaryFile, classpathFile);
            } catch (final IOException e) {
                throw new MojoExecutionException(String.format("Unable to set up new classpath file: %s", e.getMessage()), e);
            }
        }

    }

    private boolean askForConfirmation() throws IOException {
        System.out.print("\nDo you want to use the revised classpath file ? (yes/no): ");
        final BufferedReader answer = new BufferedReader(new InputStreamReader(System.in));
        return "yes".equalsIgnoreCase(answer.readLine().trim());
    }

    @Override
    protected ArtifactsFilter getMarkedArtifactFilter() {
        return new ResolveFileFilter(new SourcesFileMarkerHandler(this.markersDirectory));
    }
}
