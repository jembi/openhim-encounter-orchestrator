package org.jembi.rhea.transformers;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.jembi.Util;
import org.junit.Test;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.api.transport.PropertyScope;

public class CSDResolveProviderIDResponseTransformerTest {
	private static final String RESPONSE =
		"<CSD xmlns=\"urn:ihe:iti:csd:2013\" xmlns:csd=\"urn:ihe:iti:csd:2013\">\n" +
		"  <organizationDirectory/>\n" +
		"  <serviceDirectory/>\n" +
		"  <facilityDirectory/>\n" +
		"  <providerDirectory>\n" +
		"    <provider xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" oid=\"2.25.309768652999692686176651983274504471835.646.1.20240497736571245930865135703050872084035727\">\n" +
		"      <otherID assigningAuthorityName=\"2.25.309768652999692686176651983274504471835.646.3.4\" code=\"3525410\"/>\n" +
		"      <codedType codingSchema=\"2.25.309768652999692686176651983274504471835.646.2\" code=\"PPS\"/>\n" +
		"      <demographic>\n" +
		"        <name>\n" +
		"          <commonName>John Doctor</commonName>\n" +
		"          <forename>John</forename>\n" +
		"          <surname>Doctor</surname>\n" +
		"        </name>\n" +
		"      </demographic>\n" +
		"      <record status=\"active\" sourceDirectory=\"http://rhea-pr.ihris.org\" updated=\"2013-10-01T14:53:36+00:00\" created=\"2013-10-01T14:53:36+00:00\"/>\n" +
		"    </provider>\n" +
		"  </providerDirectory>\n" +
		"</CSD>";

	@Test
	public void testTransformMessage_TargetECID() throws TransformerException {
		MuleMessage msg = Util.buildMockMuleResponse(true, RESPONSE);
		when(msg.getProperty(CSDResolveProviderIDOffRampTransformer.SESSIONVAR_PROVIDER_TARGETIDTYPE, PropertyScope.SESSION))
			.thenReturn(null);

		Object result = new CSDResolveProviderIDResponseTransformer().transformMessage(msg, "");
		assertNotNull(result);
		assertTrue(result instanceof String);
		assertEquals("2.25.309768652999692686176651983274504471835.646.1.20240497736571245930865135703050872084035727", (String)result);

		verify(msg).setProperty("success", "true", PropertyScope.OUTBOUND);
	}

	@Test
	public void testTransformMessage_TargetNID() throws TransformerException {
		MuleMessage msg = Util.buildMockMuleResponse(true, RESPONSE);
		when(msg.getProperty(CSDResolveProviderIDOffRampTransformer.SESSIONVAR_PROVIDER_TARGETIDTYPE, PropertyScope.SESSION))
			.thenReturn("2.25.309768652999692686176651983274504471835.646.3.4");

		Object result = new CSDResolveProviderIDResponseTransformer().transformMessage(msg, "");
		assertNotNull(result);
		assertTrue(result instanceof String);
		assertEquals("3525410", (String)result);

		verify(msg).setProperty("success", "true", PropertyScope.OUTBOUND);
	}

	@Test
	public void testTransformMessage_TargetNotThere() throws TransformerException {
		MuleMessage msg = Util.buildMockMuleResponse(true, RESPONSE);
		when(msg.getProperty(CSDResolveProviderIDOffRampTransformer.SESSIONVAR_PROVIDER_TARGETIDTYPE, PropertyScope.SESSION))
			.thenReturn("This is not there");

		Object result = new CSDResolveProviderIDResponseTransformer().transformMessage(msg, "");
		assertEquals(null, result);

		verify(msg).setProperty("success", "false", PropertyScope.OUTBOUND);
	}

}
