package org.jembi.rhea.transformers;

import org.jembi.rhea.RestfulHttpResponse;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;

public class HttpResponseToRestfulHttpResponseTransformer extends
		AbstractMessageTransformer {

	@Override
	public Object transformMessage(MuleMessage msg, String enc) throws TransformerException {
		
		RestfulHttpResponse restRes = new RestfulHttpResponse();
		
		int status = Integer.valueOf((String) msg.getInboundProperty("http.status"));
		restRes.setHttpStatus(status);
		
		try {
			String body = msg.getPayloadAsString();
			restRes.setBody(body);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return restRes;
	}

}
