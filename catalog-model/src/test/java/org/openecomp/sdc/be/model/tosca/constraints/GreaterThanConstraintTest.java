package org.openecomp.sdc.be.model.tosca.constraints;

import javax.annotation.Generated;

import org.junit.Test;
import org.openecomp.sdc.be.model.tosca.ToscaType;

public class GreaterThanConstraintTest {

	private GreaterThanConstraint createTestSubject() {
		return new GreaterThanConstraint("");
	}



	

	
	@Test
	public void testGetGreaterThan() throws Exception {
		GreaterThanConstraint testSubject;
		String result;

		// default test
		testSubject = createTestSubject();
		result = testSubject.getGreaterThan();
	}

	
	@Test
	public void testSetGreaterThan() throws Exception {
		GreaterThanConstraint testSubject;
		String greaterThan = "";

		// default test
		testSubject = createTestSubject();
		testSubject.setGreaterThan(greaterThan);
	}
}