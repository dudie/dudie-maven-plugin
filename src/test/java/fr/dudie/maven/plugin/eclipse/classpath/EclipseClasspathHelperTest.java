package fr.dudie.maven.plugin.eclipse.classpath;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.w3c.dom.NodeList;

@RunWith(MockitoJUnitRunner.class)
public class EclipseClasspathHelperTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();
    private File sampleClasspath;

    private EclipseClasspathHelper helper;
    private NodeList classpathentries;

    @Mock
    private Artifact slf4jApi;
    @Mock
    private Artifact slf4jAndroid;
    @Mock
    private Artifact slf4jExtras;

    @Before
    public void setup() throws Exception {
        sampleClasspath = temp.newFile();
        IOUtils.copy(getClass().getResourceAsStream("classpath-sample.xml"), new FileOutputStream(sampleClasspath));
        helper = new EclipseClasspathHelper(sampleClasspath, new SystemStreamLog());
        classpathentries = helper.getDocument().getDocumentElement().getElementsByTagName("classpathentry");
    }

    @Before
    public void artifacts() {

        when(slf4jApi.getId()).thenReturn("org.slf4j:slf4j-api");
        when(slf4jApi.getArtifactId()).thenReturn("slf4j-api");
        when(slf4jApi.getGroupId()).thenReturn("org.slf4j");
        when(slf4jApi.getVersion()).thenReturn("1.6.4");
        when(slf4jApi.getType()).thenReturn("jar");
        when(slf4jApi.getFile()).thenReturn(new File("/home/user/.m2/repository/org/slf4j/slf4j-api/1.6.4/slf4j-api-1.6.4.jar"));

        when(slf4jAndroid.getId()).thenReturn("org.slf4j:slf4j-android");
        when(slf4jAndroid.getArtifactId()).thenReturn("slf4j-android");
        when(slf4jAndroid.getGroupId()).thenReturn("org.slf4j");
        when(slf4jAndroid.getVersion()).thenReturn("1.6.4");
        when(slf4jAndroid.getType()).thenReturn("jar");
        when(slf4jAndroid.getFile()).thenReturn(new File("/home/user/.m2/repository/org/slf4j/slf4j-android/1.6.4/slf4j-android-1.6.4.jar"));

        when(slf4jExtras.getId()).thenReturn("org.slf4j:slf4j-extras");
        when(slf4jExtras.getArtifactId()).thenReturn("slf4j-extras");
        when(slf4jExtras.getGroupId()).thenReturn("org.slf4j");
        when(slf4jExtras.getVersion()).thenReturn("1.6.4");
        when(slf4jExtras.getType()).thenReturn("jar");
        when(slf4jExtras.getFile()).thenReturn(new File("/home/user/.m2/repository/org/slf4j/slf4j-extras/1.6.4/slf4j-extras-1.6.4.jar"));
    }

    @Test
    public void preserveOriginalAttributesOnUpdate() throws IOException {
        helper.addOrUpdate(slf4jApi);

        assertThat(classpathentries.getLength()).isEqualTo(7);
        assertThat(classpathentries.item(4).getAttributes().getNamedItem("exported").getNodeValue()).isEqualTo("true");
    }

    @Test
    public void canUpdateLibraryVersion() throws IOException {
        assertThat(classpathentries.item(5).getAttributes().getNamedItem("path").getNodeValue()).isEqualTo(
                "/home/user/.m2/repository/org/slf4j/slf4j-android/0.9/slf4j-android-0.9.jar");

        helper.addOrUpdate(slf4jAndroid);

        assertThat(classpathentries.getLength()).isEqualTo(7);
        assertThat(classpathentries.item(5).getAttributes().getNamedItem("path").getNodeValue()).isEqualTo(
                "/home/user/.m2/repository/org/slf4j/slf4j-android/1.6.4/slf4j-android-1.6.4.jar");
    }

    @Test
    public void canAddLibrary() throws IOException {
        helper.addOrUpdate(slf4jExtras);

        assertThat(classpathentries.getLength()).isEqualTo(8);
        assertThat(classpathentries.item(7).getAttributes().getNamedItem("path").getNodeValue()).isEqualTo(
                "/home/user/.m2/repository/org/slf4j/slf4j-extras/1.6.4/slf4j-extras-1.6.4.jar");
    }
}
