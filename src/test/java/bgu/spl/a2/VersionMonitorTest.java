package bgu.spl.a2;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class VersionMonitorTest {
	private VersionMonitor _vm;

	@Before
	public void setUp() throws Exception {
		_vm=new VersionMonitor();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetVersion() {
		assertEquals(0,_vm.getVersion());
	}

	@Test
	public void testInc() {
		int oldVer=_vm.getVersion();
		_vm.inc();
		int newVer=_vm.getVersion();
		assertEquals(newVer-1,oldVer);
	}

	@Test
	public void testAwait() {
		int startVer=_vm.getVersion();
		Thread t1=new Thread(() -> {
			try{
				_vm.await(startVer);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		});
		t1.start();
		_vm.inc();
		try{
			t1.join();
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
		assertTrue(startVer!=_vm.getVersion());
	}

}
