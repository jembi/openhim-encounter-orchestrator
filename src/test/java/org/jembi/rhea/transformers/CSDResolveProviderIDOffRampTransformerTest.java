/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.jembi.rhea.transformers;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.api.transport.PropertyScope;

public class CSDResolveProviderIDOffRampTransformerTest {
	private static final String EXPECTED =
		"<?xml version=\"1\" encoding=\"utf-8\"?>\n" +
		"<careServicesRequest>\n" +
		"	<function uuid=\"4e8bbeb9-f5f5-11e2-b778-0800200c9a66\">\n" +
		"        <requestParams>\n" +
		"			<id oid=\"%s\">1234567890</id>\n" +
		"        </requestParams>\n" +
		"    </function>\n" +
		"</careServicesRequest>";

	@Test
	public void testTransformMessageMuleMessageString() throws TransformerException {
		MuleMessage mockMessage = buildMockMessage("NID");
		CSDResolveProviderIDOffRampTransformer transformer = new CSDResolveProviderIDOffRampTransformer();
		CSDResolveProviderIDOffRampTransformer.PROVIDER_ID_TYPE_OIDS = null;

		transformer.setProviderIdTypeOIDS("NID:1234");
		Object res = transformer.transform(mockMessage, "");

		assertNotNull(res);
		assertTrue(res instanceof String);
		assertEquals(String.format(EXPECTED, "1234"), (String)res);
		verify(mockMessage).setProperty(CSDResolveProviderIDOffRampTransformer.SESSIONVAR_PROVIDER_TARGETIDTYPE, "EPID", PropertyScope.SESSION);
	}

	@Test
	public void testIdTypeOIDSProperties() throws TransformerException {
		MuleMessage mockMessage = buildMockMessage("OTHER-ID");
		CSDResolveProviderIDOffRampTransformer transformer = new CSDResolveProviderIDOffRampTransformer();
		CSDResolveProviderIDOffRampTransformer.PROVIDER_ID_TYPE_OIDS = null;

		transformer.setProviderIdTypeOIDS("NID:1234,OTHER-ID:2345,THIRD-ID:3456");
		Object res = transformer.transform(mockMessage, "");
		assertNotNull(res);
		assertTrue(res instanceof String);
		assertEquals(String.format(EXPECTED, "2345"), (String)res);
	}

	@Test
	public void testIdTypeOIDSProperties_invalid() throws TransformerException {
		MuleMessage mockMessage = buildMockMessage("NID");
		CSDResolveProviderIDOffRampTransformer transformer = new CSDResolveProviderIDOffRampTransformer();
		CSDResolveProviderIDOffRampTransformer.PROVIDER_ID_TYPE_OIDS = null;

		transformer.setProviderIdTypeOIDS("This is invalid");
		try {
			transformer.transform(mockMessage, "");
		} catch (TransformerException ex) {
			//expected
		}
	}
	
	private MuleMessage buildMockMessage(String idType) {
		MuleMessage mockMessage = mock(MuleMessage.class);
		Map<String, String> params = new HashMap<>();
		
		params.put("id", "1234567890");
		params.put("idType", idType);
		params.put("targetIdType", "EPID");
		when(mockMessage.getPayload()).thenReturn(params);
		return mockMessage;
	}
}
