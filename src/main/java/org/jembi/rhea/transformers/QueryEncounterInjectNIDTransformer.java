/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.jembi.rhea.transformers;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jembi.rhea.Constants;
import org.jembi.rhea.RestfulHttpResponse;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.api.transport.PropertyScope;
import org.mule.module.client.MuleClient;
import org.mule.transformer.AbstractMessageTransformer;

import ca.uhn.hl7v2.model.v25.datatype.CX;
import ca.uhn.hl7v2.model.v25.datatype.XCN;
import ca.uhn.hl7v2.model.v25.group.ORU_R01_PATIENT_RESULT;
import ca.uhn.hl7v2.model.v25.message.ORU_R01;
import ca.uhn.hl7v2.model.v25.segment.OBR;
import ca.uhn.hl7v2.model.v25.segment.PID;
import ca.uhn.hl7v2.parser.GenericParser;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.validation.impl.DefaultValidation;

public class QueryEncounterInjectNIDTransformer extends
		AbstractMessageTransformer {
	
	Log log = LogFactory.getLog(this.getClass());

	@Override
	public Object transformMessage(MuleMessage msg, String enc)
			throws TransformerException {
		
		try {
			MuleClient client = new MuleClient(muleContext);
			
			RestfulHttpResponse res = (RestfulHttpResponse) msg.getPayload();
			
			String ORU_R01_str = res.getBody();
			
			if (ORU_R01_str == null || ORU_R01_str.equals("")) {
				return msg;
			}
			
			Parser parser = new GenericParser();
			DefaultValidation defaultValidation = new DefaultValidation();
			parser.setValidationContext(defaultValidation);
			
			ca.uhn.hl7v2.model.Message hl7_msg = parser.parse(ORU_R01_str);
			
			ORU_R01 oru_r01 = (ORU_R01) hl7_msg;
			
			// Replace ECID with original client ID
			PID pid = oru_r01.getPATIENT_RESULT().getPATIENT().getPID();
			CX idCX = pid.getPatientIdentifierList(0);
			String id = msg.getProperty("id", PropertyScope.SESSION);
			String idType = msg.getProperty("idType", PropertyScope.SESSION);
			
			idCX.getIdentifierTypeCode().setValue(idType);
			idCX.getIDNumber().setValue(id);
			
			// Validate and replace provider id's in each obr
			for (int i = 0 ; i < oru_r01.getPATIENT_RESULTReps() ; i++) {
				ORU_R01_PATIENT_RESULT patient_RESULT = oru_r01.getPATIENT_RESULT(i);
				
				for (int j = 0 ; j < patient_RESULT.getORDER_OBSERVATIONReps(); j++) {
					OBR obr = patient_RESULT.getORDER_OBSERVATION(j).getOBR();
					
					// Validate provider ID
					XCN orderingProvider = obr.getObr16_OrderingProvider(0);
					String proID = orderingProvider.getIDNumber().getValue();
					String proIDType = orderingProvider.getIdentifierTypeCode().getValue();
					
					// if this is an obs grouping OBR
					if (proID == null && proIDType == null) {
						continue;
					}
					
					// if one of the id values is missing
					if (proID == null || proIDType == null) {
						throw new Exception("Invalid Provider: id or id type is null");
					}
					
					Map<String, String> ProIdMap = new HashMap<String, String>();
					ProIdMap.put("id", proID);
					ProIdMap.put("idType", proIDType);
					
					MuleMessage responce = client.send("vm://getnid-openldap", ProIdMap, null, 5000);
					
					String nid = null;
					String success = responce.getInboundProperty("success");
					if (success.equals("true")) {
						nid = responce.getPayloadAsString();
						
						// Enrich message
						orderingProvider.getIDNumber().setValue(nid);
						orderingProvider.getIdentifierTypeCode().setValue(Constants.NID_ID_TYPE);
					} else {
						throw new Exception("Invalid Provider: NID for EPID:" + proID + " could not be found in Provier Registry");
					}
				}
			}
			
			String new_hl7_msg = parser.encode(oru_r01, "XML");
			res.setBody(new_hl7_msg);
			
		} catch (Exception e) {
			throw new TransformerException(this, e);
		}
		
		return msg;
	}
}
