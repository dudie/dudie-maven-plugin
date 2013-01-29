package fr.dudie.maven.plugin.eclipse.classpath;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

public class ClasspathFileComparatorTest {

    private ClasspathFileComparator comparator;
    
    @Before
    public void setup() throws URISyntaxException, IOException {
        final File original = new File(getClass().getResource("original.txt").toURI());
        final File revised = new File(getClass().getResource("revised.txt").toURI());
        comparator=new ClasspathFileComparator(original, revised);
    }
    
    @Test
    public void test() {
        System.out.println(comparator.toString());
    }
}
