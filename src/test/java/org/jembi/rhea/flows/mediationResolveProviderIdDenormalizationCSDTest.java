package org.jembi.rhea.flows;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Rule;
import org.junit.Test;
import org.mule.api.MuleMessage;
import org.mule.api.transport.PropertyScope;
import org.mule.module.client.MuleClient;
import org.mule.tck.junit4.FunctionalTestCase;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class mediationResolveProviderIdDenormalizationCSDTest extends
		FunctionalTestCase {

	static final String RESPONSE =
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
	static final String RESPONSE_EMPTY =
		"<CSD xmlns=\"urn:ihe:iti:csd:2013\" xmlns:csd=\"urn:ihe:iti:csd:2013\">\n" +
		"  <organizationDirectory/>\n" +
		"  <serviceDirectory/>\n" +
		"  <facilityDirectory/>\n" +
		"  <providerDirectory/>\n" +
		"</CSD>";
	
	private final Log log = LogFactory.getLog(this.getClass());
	
	@Rule
	public WireMockRule wireMockRule = new WireMockRule(8080);

	private void setupWebserviceStub(int httpStatus, String responseBody) {
		stubFor(get(urlEqualTo("/CSD/careServicesRequest"))
				.withHeader("Accept", equalTo("application/xml"))
		    	.willReturn(aResponse()
		    		.withStatus(httpStatus)
		    		.withHeader("Content-Type", "application/xml")
	                .withBody(responseBody)));
	}
	
	@Override
	protected void doSetUp() throws Exception {
		Logger.getRootLogger().setLevel(Level.INFO);
		super.doSetUp();
	}

	@Override
	protected void doTearDown() throws Exception {
		Logger.getRootLogger().setLevel(Level.WARN);
		super.doTearDown();
	}

	@Override
	protected String getConfigResources() {
		return "src/main/app/resolveproviderid-denormalization-csd.xml";
	}
	
	@Test
	public void testSendGetepidDenormalizationCSD_validRequest() throws Exception {
		testResolveProviderIdCSD("NID", "3525410", "EPID", "2.25.309768652999692686176651983274504471835.646.1.20240497736571245930865135703050872084035727");
		testResolveProviderIdCSD("EPID", "2.25.309768652999692686176651983274504471835.646.1.20240497736571245930865135703050872084035727", "NID", "3525410");
		testResolveProviderIdCSD("NID", "3525410", null, "2.25.309768652999692686176651983274504471835.646.1.20240497736571245930865135703050872084035727");
		testResolveProviderIdCSD(null, "2.25.309768652999692686176651983274504471835.646.1.20240497736571245930865135703050872084035727", "NID", "3525410");
	}
	
	private void testResolveProviderIdCSD(String fromIdType, String fromId, String toIdType, String toId) throws Exception {
		log.info("Starting test");
		
		setupWebserviceStub(200, RESPONSE);
		
		MuleClient client = new MuleClient(muleContext);
		
		Map<String, String> idMap = new HashMap<String, String>();

	    idMap.put("id", fromId);
	    idMap.put("idType", fromIdType);
	    idMap.put("targetIdType", toIdType);
	    
	    Map<String, Object> properties = null;
	    MuleMessage result = client.send("vm://resolveproviderid-csd", idMap, properties);

	    assertNotNull(result.getPayload());

	    assertEquals(toId, result.getPayloadAsString());
	    
	    String success = result.getProperty("success", PropertyScope.INBOUND);
	    assertEquals("true", success);

	    log.info("Test completed");
	}
	
	@Test
	public void testSendGetepidDenormalizationCSD_unknownProvider() throws Exception {
		testResolveProviderIdCSD_unknown("NID", "1234", "EPID");
		testResolveProviderIdCSD_unknown("EPID", "1234", "NID");
		testResolveProviderIdCSD_unknown("NID", "1234", null);
		testResolveProviderIdCSD_unknown(null, "1234", "NID");
	}
	
	private void testResolveProviderIdCSD_unknown(String fromIdType, String fromId, String toIdType) throws Exception {
		log.info("Starting test");
		
		setupWebserviceStub(200, RESPONSE_EMPTY);
		
		MuleClient client = new MuleClient(muleContext);
		
		Map<String, String> idMap = new HashMap<String, String>();

	    idMap.put("id", fromId);
	    idMap.put("idType", fromIdType);
	    idMap.put("targetIdType", toIdType);
	    
	    Map<String, Object> properties = null;
	    MuleMessage result = client.send("vm://resolveproviderid-csd", idMap, properties);

	    String success = result.getProperty("success", PropertyScope.INBOUND);
	    assertEquals("false", success);

	    log.info("Test completed");
	}
	
	@Test
	public void testSendGetepidDenormalizationCSD_invalidRequest() throws Exception {
		testResolveProviderIdCSD_invalidRequest("NID", "EPID");
		testResolveProviderIdCSD_invalidRequest("EPID", "NID");
		testResolveProviderIdCSD_invalidRequest("NID", null);
		testResolveProviderIdCSD_invalidRequest(null, "NID");
	}

	private void testResolveProviderIdCSD_invalidRequest(String fromIdType, String toIdType) throws Exception {
		log.info("Starting test");
		
		setupWebserviceStub(404, "");
		
		MuleClient client = new MuleClient(muleContext);
		
		Map<String, String> idMap = new HashMap<String, String>();

	    idMap.put("id", "1234567890123456");
	    idMap.put("idType", fromIdType);
	    idMap.put("targetIdType", toIdType);
	    
	    Map<String, Object> properties = null;
	    MuleMessage result = client.send("vm://resolveproviderid-csd", idMap, properties);

	    assertNotNull(result.getPayload());

	    String success = result.getProperty("success", PropertyScope.INBOUND);
	    assertEquals("false", success);

	    log.info("Test completed");
	}

}
