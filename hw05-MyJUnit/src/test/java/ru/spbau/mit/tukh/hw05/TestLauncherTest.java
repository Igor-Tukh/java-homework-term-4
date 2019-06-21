package ru.spbau.mit.tukh.hw05;

import org.junit.Test;
import ru.spbau.mit.tukh.hw05.annotations.AfterClass;
import ru.spbau.mit.tukh.hw05.annotations.BeforeClass;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;

public class TestLauncherTest {
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @org.junit.Before
    public void setUpStream() {
        System.setErr(new PrintStream(errContent));
    }

    @Test
    public void testTestingBeforeAndAfter() {
        TestLauncher testLauncher = new TestLauncher(BeforeAndAfter.class);
        testLauncher.start();
    }

    @Test
    public void testIgnore() {
        TestLauncher testLauncher = new TestLauncher(Ignore.class);
        testLauncher.start();
    }

    @Test
    public void testBeforeClassAndAfterClass() {
        TestLauncher testLauncher = new TestLauncher(BeforeClassAndAfterClass.class);
        testLauncher.start();
        assertEquals("Before class\n" + "1\n" + "2\n" +
                "After class\n", errContent.toString());
    }

    public static class BeforeAndAfter {
        private int beforeCount = 0;
        private int afterCount = 0;
        private int testCounter = 0;
        BeforeAndAfter() { }

        @BeforeClass
        public void beforeClass() {
            assertEquals(0, beforeCount);
            assertEquals(0, afterCount);
        }

        @ru.spbau.mit.tukh.hw05.annotations.Before
        public void firstBefore() {
            beforeCount++;
        }

        @ru.spbau.mit.tukh.hw05.annotations.Before
        public void SecondBefore() {
            beforeCount++;
        }

        @ru.spbau.mit.tukh.hw05.annotations.Test
        public void firstTest() {
            testCounter++;
        }

        @ru.spbau.mit.tukh.hw05.annotations.Test
        public void secondTest() {
            testCounter++;
        }

        @ru.spbau.mit.tukh.hw05.annotations.After
        public void firstAfter() {
            afterCount++;
        }

        @ru.spbau.mit.tukh.hw05.annotations.After
        public void SecondAfter() {
            afterCount++;
        }

        @AfterClass
        public void afterClass() {
            assertEquals(testCounter * 2, beforeCount);
            assertEquals(testCounter * 2, afterCount);
        }
    }

    public static class Ignore {
        private int state;

        @ru.spbau.mit.tukh.hw05.annotations.Test(ignore = "1")
        public void testIgnore() {
            state = 1;
        }

        @AfterClass
        public void afterClass() {
            assertEquals(0, state);
        }
    }

    public static class BeforeClassAndAfterClass {
        private int testCount;
        @BeforeClass
        public void beforeClass() {
            System.err.println("Before class");
        }

        @ru.spbau.mit.tukh.hw05.annotations.Test(ignore = "1")
        public void testIgnore() {
            testCount++;
        }

        @ru.spbau.mit.tukh.hw05.annotations.Test
        public void firstTest() {
            System.err.println("1");
            testCount++;
        }

        @ru.spbau.mit.tukh.hw05.annotations.Test
        public void secondTest() {
            System.err.println("2");
            testCount++;
        }


        @AfterClass
        public void afterClass() {
            System.err.println("After class");
            assertEquals(2, testCount);
        }
    }
}