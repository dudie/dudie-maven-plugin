package fr.dudie.maven.plugin.eclipse.classpath;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

/**
 * Compares two eclipse .classpath XML files to review changes.
 * 
 * @author Jeremie Huchet
 */
public class ClasspathFileComparator {

    private final List<String> original;
    private final List<String> revised;

    public ClasspathFileComparator(final File original, final File revised) throws IOException {
        this.original = getLines(original);
        this.revised = getLines(revised);
    }

    private List<String> getLines(final File f) throws IOException {
        final List<String> lines = new ArrayList<String>();
        final BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
        try {
            String l = null;
            while ((l = r.readLine()) != null) {
                lines.add(l);
            }
        } finally {
            IOUtils.closeQuietly(r);
        }
        return lines;
    }

    @Override
    public String toString() {
        final Patch p = DiffUtils.diff(original, revised);
        final StringBuilder s = new StringBuilder();
        for (final Delta d : p.getDeltas()) {
            s.append("@@@ original ").append(d.getOriginal().getPosition());
            s.append(" / revised ").append(d.getRevised().getPosition()).append('\n');
            switch (d.getType()) {
            case CHANGE:
                for (final Object line : d.getOriginal().getLines()) {
                    s.append("- ").append(line).append('\n');
                }
                for (final Object line : d.getRevised().getLines()) {
                    s.append("+ ").append(line).append('\n');
                }
                break;
            case DELETE:
                for (final Object line : d.getOriginal().getLines()) {
                    s.append("- ").append(line).append('\n');
                }
                break;
            case INSERT:
                for (final Object line : d.getRevised().getLines()) {
                    s.append("+ ").append(line).append('\n');
                }
                break;
            default:
                break;
            }
        }
        return s.toString();
    }
}
