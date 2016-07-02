package jp.ruru.park.ando.ut5.sample;

import java.io.IOException;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Before;
import org.junit.Ignore;

/**
 * Unit test from sample base.
 * this is multi-line message1
 * this is multi-line message2
 * this is multi-line message3
 * @author t-ando ton
 */
@RunWith(JUnit4.class)
public abstract class SampleBase {
	/**
	 * set up
	 * (SampleBase)
	 * @throws Exception Got the exception.
	 */
	@Before
	public void setUp() throws Exception {
	}
	
	
	/**This is normal test.
	 * @author t-ando kan
	 */
	@Test
	public void testSample1() {
		assertTrue(true);
	}
	/**
	 * This is abnormal test(Fail).
	 */
	@Test
	public void testFail() {
		assertTrue(false);
	}
	/**
	 *  This is abnormal test(Error).
	 *  @throws IOException Got the IOException, it will be get.
	 */
	@Test
	public void testError() throws IOException {
		throw new IOException("test");
	}
	/**
	 * This is abnormal test(Skip).
	 */
	@Ignore("skip message")
	@Test
	public void testSkip() {
	}

}
