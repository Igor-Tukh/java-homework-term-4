package ru.spbau.mit.tukh.hw03;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

public class FTPtest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void init() throws IOException, InterruptedException {
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        String className = MyServer.class.getCanonicalName();
        ProcessBuilder builder = new ProcessBuilder(javaBin, "-cp", classpath, className);
        builder.start();
        Thread.sleep(1000);
        System.setOut(new PrintStream(outContent));
    }

    @Test
    public void testOneClientListRequest() throws IOException {
        MyClient myClient = new MyClient("127.0.0.1");
        myClient.requestList("./src");
        assertEquals("2\ntest true\nmain true\n", outContent.toString()); // We know expected order of output
        // Formal, we need map. But in our case, we know what to except
    }

    @Test
    public void testSeveralClientsListRequest() throws IOException {
        MyClient myClient0 = new MyClient("127.0.0.1");
        myClient0.requestList("./src");
        assertEquals("2\ntest true\nmain true\n", outContent.toString());

        MyClient myClient1 = new MyClient("127.0.0.1");
        myClient1.requestList("./src");
        assertEquals("2\ntest true\nmain true\n2\ntest true\nmain true\n", outContent.toString());

        MyClient myClient2 = new MyClient("127.0.0.1");
        myClient2.requestList("./src");
        assertEquals("2\ntest true\nmain true\n2\ntest true\nmain true\n2\ntest true\nmain true\n", outContent.toString());
    }

    @Test
    public void testOneClientGetRequest() throws IOException {
        MyClient myClient = new MyClient("127.0.0.1");
        myClient.requestGet("./src/test/resources/testfile0");
        assertEquals("testfile0\ntestfile1", outContent.toString());
    }

    @Test
    public void testFiveClients() throws IOException {
        MyClient myClient0 = new MyClient("127.0.0.1");
        MyClient myClient1 = new MyClient("127.0.0.1");
        MyClient myClient2 = new MyClient("127.0.0.1");
        MyClient myClient3 = new MyClient("127.0.0.1");
        MyClient myClient4 = new MyClient("127.0.0.1");
        myClient0.requestList("./src");
        myClient1.requestList("./src");
        myClient2.requestList("./src");
        myClient3.requestList("./src");
        myClient4.requestList("./src");
        StringBuilder expectedOutput = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            expectedOutput.append("2\ntest true\nmain true\n");
        }
        assertEquals(expectedOutput.toString(), outContent.toString());
    }

    @Test
    public void testWrongDirListRequest() throws IOException {
        MyClient myClient0 = new MyClient("127.0.0.1");
        myClient0.requestList("./src/test/resources/testfile");
        assertEquals("0\n", outContent.toString());
    }
}