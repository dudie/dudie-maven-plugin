package fr.dudie.maven.plugin.eclipse.classpath;

import java.util.regex.Pattern;

import org.apache.maven.artifact.Artifact;

/**
 * Find out if a file belongs to an artifact.
 * 
 * @author Jeremie Huchet
 */
public class ArtifactMatcher {

    private final Pattern p;

    public ArtifactMatcher(final Artifact artifact) {
        final String artifactId = artifact.getArtifactId();
        final String groupId = artifact.getGroupId().replace('.', '/');
        final String type = artifact.getType();

        // /home/user/.m2/repository/<groupId>/<artifactId>/<version>/<artifactId>-<version>.<type>
        final String pattern = String.format(".*%s/%s/.*/%s-.*\\.%s", groupId, artifactId, artifactId, type);
        p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
    }

    public boolean matches(final String path) {
        return p.matcher(path).find();
    }

}
