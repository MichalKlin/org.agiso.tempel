/* org.agiso.tempel.convert.ClassToFieldNameConverterUTest (12-11-2012)
 * 
 * ClassToFieldNameConverterUTest.java
 * 
 * Copyright 2012 PPW 'ARAJ' Sp. z o. o.
 */
package org.agiso.tempel.convert;

import org.agiso.tempel.core.convert.ITemplateParamConverter;
import org.testng.annotations.Test;

/**
 * 
 * 
 * @author <a href="mailto:kkopacz@araj.pl">Karol Kopacz</a>
 */
public class ClassToFieldNameConverterUTest {
	private ITemplateParamConverter<String> converter = new ClassToFieldNameConverter();

//	--------------------------------------------------------------------------
	@Test
	public void testConvertNull() throws Exception {
		assert null == converter.convert(null);
	}

	@Test
	public void testConvertEmpty() throws Exception {
		assert "".equals(converter.convert(""));
	}

	@Test
	public void testConvertBlank() throws Exception {
		assert "  ".equals(converter.convert("  "));
	}

	@Test
	public void testConvert() throws Exception {
		assert "sampleClassName".equals(converter.convert("SampleClassName"));
		assert "invalidClassName".equals(converter.convert("invalidClassName"));
	}
}
