package fr.dudie.maven.plugin.eclipse.classpath;

import static org.mockito.Mockito.*;
import org.apache.maven.artifact.Artifact;
import static org.fest.assertions.Assertions.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fr.dudie.maven.plugin.eclipse.classpath.ArtifactMatcher;

@RunWith(MockitoJUnitRunner.class)
public class ArtifactMatcherTest {

    private ArtifactMatcher m;

    @Mock
    private Artifact artifact;

    @Before
    public void setup() {
        when(artifact.getArtifactId()).thenReturn("dudie-maven-plugin");
        when(artifact.getGroupId()).thenReturn("fr.dudie.maven.plugin");
        when(artifact.getVersion()).thenReturn("2.3.1");
        when(artifact.getType()).thenReturn("jar");

        m = new ArtifactMatcher(artifact);
    }

    @Test
    public void canFindExactSameArtifact() {
        assertThat(m.matches("/repository/fr/dudie/maven/plugin/dudie-maven-plugin/2.3.1/dudie-maven-plugin-2.3.1.jar")).isTrue();
    }

    @Test
    public void canFindWithDifferentVersion() {
        assertThat(m.matches("/repository/fr/dudie/maven/plugin/dudie-maven-plugin/1.0/dudie-maven-plugin-1.0.jar")).isTrue();
    }
    
    @Test
    public void detectWrongArtifactId() {
        assertThat(m.matches("/repository/fr/dudie/maven/plugin/wrong-maven-plugin/2.3.1/wrong-maven-plugin-2.3.1.jar")).isFalse();
    }

    @Test
    public void detectWrongGroupId() {
        assertThat(m.matches("/repository/fr/dudie/wrong/groupId/dudie-maven-plugin/2.3.1/dudie-maven-plugin-2.3.1.jar")).isFalse();
    }
    
    @Test
    public void detectWrongType() {
        assertThat(m.matches("/repository/fr/dudie/maven/plugin/dudie-maven-plugin/2.3.1/dudie-maven-plugin-2.3.1.war")).isFalse();
    }
    
    @Test
    public void detectionIsCaseInsensitive() {
        assertThat(m.matches("/REPOSITORY/FR/DUDIE/MAVEN/PLUGIN/DUDIE-MAVEN-PLUGIN/2.3.1/DUDIE-MAVEN-PLUGIN-2.3.1.JAR")).isTrue();
    }
}
