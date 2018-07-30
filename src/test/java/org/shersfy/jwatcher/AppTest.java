package org.shersfy.jwatcher;

import java.util.LinkedList;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    public void test01() throws Exception {
    	LinkedList<String> list = new LinkedList<>();
    	list.add("A");
    	list.add("B");
    	list.add("C");
    	
    	for(int i=0; i<10; i++){
    		System.out.println("================");
    		list.forEach(System.out::println);
    		list.poll();
    		list.add("D"+i);
    	}
    	
    }
}
