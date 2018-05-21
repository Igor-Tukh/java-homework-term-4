package ru.spbau.mit.tukh.hw05;

import org.junit.Test;
import ru.spbau.mit.tukh.hw05.annotations.AfterClass;
import ru.spbau.mit.tukh.hw05.annotations.BeforeClass;

import static org.junit.Assert.*;

public class TestLauncherTest {
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
}