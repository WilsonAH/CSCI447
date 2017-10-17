


import static org.junit.Assert.*;

import org.junit.Test;

public class RBFTest {
	@Test
	public void testEuclid1() {

	int [] p = {2};
	int [] q = {0};
	
		RBF testInstance = new RBF(p,q);
		double returnedInstance =  testInstance.euclid(p, q);
		assertEquals( 2, returnedInstance, 0.0);
	} 

	@Test
	public void testEuclid2() {

	int [] p = {2,0};
	int [] q = {0,2};
	
		RBF testInstance = new RBF(p,q);
		double returnedInstance =  testInstance.euclid(p, q);
		assertEquals( 2.828, returnedInstance, 0.005);

	}
	
	@Test
	public void testEuclid3() {

	int [] p = {2,0,1};
	int [] q = {0,2,2};
	
		RBF testInstance = new RBF(p,q);
		double returnedInstance =  testInstance.euclid(p, q);
		assertEquals( 3, returnedInstance, 0.0);

	}
	@Test
	public void testEuclid4() {

	int [] p = {2,0,1,3};
	int [] q = {0,2,2,3};
	
		RBF testInstance = new RBF(p,q);
		double returnedInstance =  testInstance.euclid(p, q);
		assertEquals( 3, returnedInstance, 0.0);

	}
	
//	@Test
//	public void testGausFunction() {
		
//	}
	
	
}
