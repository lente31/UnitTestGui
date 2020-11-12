package UnitTestGui;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

//Tests die Vorgänge unabhängig von der Gui
public class TestRunners {

	public static volatile boolean STOP_TEST = false;

	public void startTest1() {
		Result result = JUnitCore.runClasses(Testklasse1.class);

		for (Failure failure : result.getFailures()) {
			System.out.println(failure.toString());

			
			System.out.println("TestSetClass: " + failure.getDescription().getClassName() + " ; TestCase-Method:"
					+ failure.getDescription().getMethodName());
		}

		
	}

	public void startTest2() {

		Result result = JUnitCore.runClasses(Testklasse2.class);
		if (result.wasSuccessful()) {
			System.out.println("funktioniert");
		}
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.toString());

		
			System.out.println("TestSetClass: " + failure.getDescription().getClassName() + " ; TestCase-Method:"
					+ failure.getDescription().getMethodName());
		}

	
	}

	public static void main(String[] args) {
		TestRunners tsr = new TestRunners();
		tsr.startTest1();
		tsr.startTest2();

	}
//
}
