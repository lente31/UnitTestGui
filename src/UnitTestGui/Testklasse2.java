package UnitTestGui;

import org.junit.*;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Testklasse2 {
	public static List<String> lst2Completed = new ArrayList<>();

	String test1;
	String test2;
	String test3;

	@Test
	public void test1() {
		test1 = "FAILED";
		lst2Completed.add(0, test1);
		if (TestRunners.STOP_TEST) {
			return;
		}

		try {
			assertTrue("test1 failed", 1 == 2);
			System.out.println("TSK2 Test 1 abgeschlossen");
			test1 = "PASSED";
			System.out.println(test1);
			lst2Completed.remove(0);
			lst2Completed.add(0, test1);
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test2() {
		test2 = "FAILED";
		lst2Completed.add(1, test2);
		if (TestRunners.STOP_TEST) {
			return;
		}

		try {
			assertTrue("test 2 failed", 1 == 1);
			System.out.println("TSK2 Test 2 abgeschlossen");
			test2 = "PASSED";
			lst2Completed.remove(1);
			lst2Completed.add(1, test2);

			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test3() {
		test3 = "FAILED";
		lst2Completed.add(2, test3);
		if (TestRunners.STOP_TEST) {
			return;
		}

		try {
			assertTrue("test 3 failed", 1 == 1);
			System.out.println("TSK2 Test 3 abgeschlossen");
			test3 = "PASSED";
			lst2Completed.remove(2);
			lst2Completed.add(2, test3);

			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public List<String> getList() {
		return lst2Completed;
	}

	@Ignore
	public void ignoredMethod2() {
		System.out.println("Ignore");
	}

}
