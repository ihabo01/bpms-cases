package org.intalio.tempo.cases.server;

import java.util.Properties;

import org.intalio.tempo.acm.server.PIPALoader;

import junit.framework.Assert;
import junit.framework.TestCase;

public class PIPALoaderTest extends TestCase {

    /**
     * @param args
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(PIPALoaderTest.class);
    }

    public void testParsePipa() throws Exception {
        Properties prop = new Properties();
        prop.load(this.getClass().getResourceAsStream("/AbsenceRequest.pipa"));
        Assert.assertNotNull(PIPALoader.parsePipa(prop));
    }

}
