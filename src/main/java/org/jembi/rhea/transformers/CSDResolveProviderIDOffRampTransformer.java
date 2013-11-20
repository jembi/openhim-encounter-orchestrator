/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.jembi.rhea.transformers;

import java.util.HashMap;
import java.util.Map;

import org.jembi.rhea.transformers.exceptions.InvalidPropertyException;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.api.transport.PropertyScope;
import org.mule.transformer.AbstractMessageTransformer;

public class CSDResolveProviderIDOffRampTransformer extends AbstractMessageTransformer {
	
	protected static final String CSD_REQUEST_TEMPLATE =
		"<?xml version=\"1\" encoding=\"utf-8\"?>\n" +
		"<careServicesRequest>\n" +
		"	<function uuid=\"4e8bbeb9-f5f5-11e2-b778-0800200c9a66\">\n" +
		"        <requestParams>\n" +
		"			<id oid=\"%s\">%s</id>\n" +
		"        </requestParams>\n" +
		"    </function>\n" +
		"</careServicesRequest>";
	
	public static final String SESSIONVAR_PROVIDER_TARGETIDTYPE = "provider-targetIdType";
	
	protected static Map<String, String> PROVIDER_ID_TYPE_OIDS = null;
	private String providerIdTypeOIDS;
	

	@SuppressWarnings("unchecked")
	@Override
	public Object transformMessage(MuleMessage msg, String enc)
			throws TransformerException {

		if (PROVIDER_ID_TYPE_OIDS==null) {
			try {
				buildProviderIdTypeOIDSMap();
			} catch (InvalidPropertyException ex) {
				throw new TransformerException(this, ex);
			}
		}
		
		Map<String, String> payload = (Map<String, String>) msg.getPayload();
		String id = payload.get("id");
		String idType = payload.get("idType");
		String idTypeOID = PROVIDER_ID_TYPE_OIDS.get(idType);
		String targetIdType = payload.get("targetIdType");
		msg.setProperty(SESSIONVAR_PROVIDER_TARGETIDTYPE, targetIdType, PropertyScope.SESSION);
		
		return String.format(CSD_REQUEST_TEMPLATE, idTypeOID, id);
	}

	private void buildProviderIdTypeOIDSMap() throws InvalidPropertyException {
		PROVIDER_ID_TYPE_OIDS = new HashMap<>();

		if (providerIdTypeOIDS==null)
			return;

		for (String pair : providerIdTypeOIDS.split(",")) {
			String[] components = pair.split(":");
			if (components.length!=2)
				throw new InvalidPropertyException("pr.csd.providerIdTypeOIDS", providerIdTypeOIDS);

			PROVIDER_ID_TYPE_OIDS.put(components[0], components[1]);
		}
	}

	public String getProviderIdTypeOIDS() {
		return providerIdTypeOIDS;
	}

	public void setProviderIdTypeOIDS(String providerIdTypeOIDS) {
		this.providerIdTypeOIDS = providerIdTypeOIDS;
	}
}
