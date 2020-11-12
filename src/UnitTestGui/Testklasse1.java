package UnitTestGui;

import org.junit.*;
import org.junit.runners.MethodSorters;
import junit.framework.TestResult;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Testklasse1 extends TestResult {


  public static List<String> lstTests = new ArrayList<>();

  String test1;
  String test2;
  String test3;



  @Test
  public void test1() {
    test1 = "FAILED";
    lstTests.add(test1);
    if (TestRunners.STOP_TEST) {
      return;
    }
      try {
        assertTrue("Test nicht funktioniert", 1 == 1);
        System.out.println("TSK1 Test 1 abgeschlossen");
       
        lstTests.remove(test1);
        lstTests.add(test1 = "PASSED");
  
        Thread.sleep(1000);

      } catch (InterruptedException e) {
        e.printStackTrace();


      }
      
  }

  @Test
  public void test2() {
    test2 = "FAILED";
    lstTests.add(1,test2);
    if (TestRunners.STOP_TEST) {
      return;
    }

    try {
      assertTrue("it worked", 2 == 2);

      System.out.println("TSK1 Test 2 abgeschlossen");
      test2 = "PASSED";
      lstTests.remove(1);
      lstTests.add(1,test2);
      Thread.sleep(1000);

    } catch (InterruptedException e) {
      e.printStackTrace();

    }

  }

  @Test
  public void test3() {
    test3 = "FAILED";
    lstTests.add(2,test3);
    if (TestRunners.STOP_TEST) {
      return;
    }

    try {
      assertTrue("it didnt worked", 2 == 2);
      System.out.println("TSK1 Test 3 abgeschlossen");
      test3 = "PASSED";
      lstTests.remove(2);
      lstTests.add(2,test3);
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();


    }

  }

  @Ignore
  public void ignoredMethod() {
    System.out.println("Das sollte man nicht lesen");
  }


  public List<String> getList() {
    return lstTests;
  }


}
