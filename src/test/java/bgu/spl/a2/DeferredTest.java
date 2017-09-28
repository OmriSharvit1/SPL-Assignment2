package bgu.spl.a2;

import static org.junit.Assert.*;

import java.util.ArrayList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class DeferredTest {
	private Deferred<Integer> def;

	@Before
	public void setUp() throws Exception {
		def=new Deferred<Integer>();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGet1() {
		boolean passed=false;
		try{
			def.get();
		}
		catch (IllegalStateException e){
			passed=true;
		}
		catch (Exception e){
			passed=false;
		}
		assertTrue(passed==true);
		
		Integer i= new Integer(1);
		def.resolve(i);
		assertEquals (i, def.get());		
	}
	
	@Test
	public void testIsResolved() {
		Integer i= new Integer(1);
		assertFalse(def.isResolved());
		
		def.resolve(i);
		assertTrue(def.isResolved());
	}

	@Test
	public void testResolve() {
		Integer i= new Integer(1);
		def.resolve(i);
		assertEquals (i, def.get());
		assertTrue (def.isResolved());
		
		Integer j= new Integer(2);
		boolean passed=false;
		try{
			def.resolve(j);
		}
		catch (IllegalStateException e){
			passed=true;
		}
		catch (Exception e){
			passed=false;
		}
		assertTrue(passed==true);
		
		
	}

	@Test
	public void testWhenResolved() {	
		Integer i= new Integer (1);
		int []a=new int[1];
		def.whenResolved(()-> {
			a[0]=1;
		});
		ArrayList<Runnable> callsList=def.getCallsList();
		assertTrue (!callsList.isEmpty());		
		
		def.resolve(i);
		assertEquals(a[0],1);
		assertTrue (callsList.isEmpty());
		
		def.whenResolved(()-> {
			a[0]=2;
		});
		assertTrue (callsList.isEmpty());
		assertEquals(a[0],2);
	}


}
