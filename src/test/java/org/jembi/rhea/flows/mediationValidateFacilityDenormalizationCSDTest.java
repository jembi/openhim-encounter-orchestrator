package org.jembi.rhea.flows;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

public class mediationValidateFacilityDenormalizationCSDTest extends
		FunctionalTestCase {

	static final String RESPONSE =
		"<CSD xmlns=\"urn:ihe:iti:csd:2013\" xmlns:csd=\"urn:ihe:iti:csd:2013\">\n" +
		"  <organizationDirectory/>\n" +
		"  <serviceDirectory/>\n" +
		"  <facilityDirectory>\n" +
		"    <facility>\n" +
		"      <id oid=\"1234\"/>\n" +
		"    </facility>\n" +
		"  </facilityDirectory>\n" +
		"  <providerDirectory/>\n" +
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
		return "src/main/app/validatefacility-denormalization-csd.xml";
	}
	
	@Test
	public void testValidRequest() throws Exception {
		log.info("Starting test");
		
		setupWebserviceStub(200, RESPONSE);
		
		MuleClient client = new MuleClient(muleContext);
	    MuleMessage result = client.send("vm://validateFacility-csd", "1234", null);

	    assertNotNull(result.getPayload());
	    String success = result.getProperty("success", PropertyScope.INBOUND);
	    assertEquals("true", success);

	    log.info("Test completed");
	}
	
	@Test
	public void testUnknown() throws Exception {
		log.info("Starting test");
		
		setupWebserviceStub(200, RESPONSE_EMPTY);
		
		MuleClient client = new MuleClient(muleContext);
	    MuleMessage result = client.send("vm://validateFacility-csd", "1234", null);

	    assertNotNull(result.getPayload());
	    String success = result.getProperty("success", PropertyScope.INBOUND);
	    assertEquals("false", success);

	    log.info("Test completed");
	}
	
	@Test
	public void testError() throws Exception {
		log.info("Starting test");
		
		setupWebserviceStub(500, "");
		
		MuleClient client = new MuleClient(muleContext);
	    MuleMessage result = client.send("vm://validateFacility-csd", "1234", null);

	    assertNotNull(result.getPayload());
	    String success = result.getProperty("success", PropertyScope.INBOUND);
	    assertEquals("false", success);

	    log.info("Test completed");
	}
}
