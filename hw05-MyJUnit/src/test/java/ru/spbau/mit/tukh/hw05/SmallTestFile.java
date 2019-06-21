package ru.spbau.mit.tukh.hw05;

import ru.spbau.mit.tukh.hw05.annotations.After;
import ru.spbau.mit.tukh.hw05.annotations.Before;
import ru.spbau.mit.tukh.hw05.annotations.Test;

/**
 * Example of usage.
 */
public class SmallTestFile {
    @Before
    public void firstBefore() { }
    @Before
    public void SecondBefore() { }

    @Test
    public void AcceptingSimpleTest() { }

    @Test
    public void NotAcceptingNoExcpetionsExpectedTest() throws Exception { throw new Exception("C'est La Vie"); }

    @Test(expected = Throwable.class)
    public void NotAcceptingExcpetionExpectedTest() {  }

    @Test(ignore = "C'est La Vie")
    public void IgnoreTest() { }

    @After
    public void firstAfter() { }
    @After
    public void SecondAfter() { }
}
