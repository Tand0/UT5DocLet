package jp.ruru.park.ando.ut5.sample;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * This is setup and tear down test.
 * see the following methid
 * - setUpBeforeClass()
 * - tearDownAfterClass()
 * - setUp()
 * - tearDown()
 */
@RunWith(JUnit4.class)
public class SetupTest {

	/**
	 * set up before class
	 * @throws Exception Got the exception.
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * tear down after class
	 * @throws Exception Got the exception.
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * set up
	 * @throws Exception Got the exception.
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * tear down
	 * @throws Exception Got the exception.
	 */
	@After
	public void tearDown() throws Exception {
	}

	/** one test */
	@Test
	public void testOne() {
		assertNull(null);
	}
	/** tow test */
	@Test
	public void testTwo() {
		assertTrue(true);
	}
}
