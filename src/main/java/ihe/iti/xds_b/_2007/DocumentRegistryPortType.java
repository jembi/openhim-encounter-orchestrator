
package ihe.iti.xds_b._2007;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;
import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryRequest;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;
import org.hl7.v3.MCCIIN000002UV01;
import org.hl7.v3.PRPAIN201301UV;
import org.hl7.v3.PRPAIN201302UV;
import org.hl7.v3.PRPAIN201304UV;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.4-b01
 * Generated source version: 2.2
 * 
 */
@WebService(name = "DocumentRegistry_PortType", targetNamespace = "urn:ihe:iti:xds-b:2007")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
    oasis.names.tc.ebxml_regrep.xsd.lcm._3.ObjectFactory.class,
    oasis.names.tc.ebxml_regrep.xsd.query._3.ObjectFactory.class,
    oasis.names.tc.ebxml_regrep.xsd.rim._3.ObjectFactory.class,
    oasis.names.tc.ebxml_regrep.xsd.rs._3.ObjectFactory.class,
    org.hl7.v3.ObjectFactory.class
})
public interface DocumentRegistryPortType {


    /**
     * 
     * @param body
     * @return
     *     returns oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType
     */
    @WebMethod(operationName = "DocumentRegistry_RegisterDocumentSet-b", action = "urn:ihe:iti:2007:RegisterDocumentSet-b")
    @WebResult(name = "RegistryResponse", targetNamespace = "urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0", partName = "body")
    public RegistryResponseType documentRegistryRegisterDocumentSetB(
        @WebParam(name = "SubmitObjectsRequest", targetNamespace = "urn:oasis:names:tc:ebxml-regrep:xsd:lcm:3.0", partName = "body")
        SubmitObjectsRequest body);

    /**
     * 
     * @param body
     * @return
     *     returns oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse
     */
    @WebMethod(operationName = "DocumentRegistry_RegistryStoredQuery", action = "urn:ihe:iti:2007:RegistryStoredQuery")
    @WebResult(name = "AdhocQueryResponse", targetNamespace = "urn:oasis:names:tc:ebxml-regrep:xsd:query:3.0", partName = "body")
    public AdhocQueryResponse documentRegistryRegistryStoredQuery(
        @WebParam(name = "AdhocQueryRequest", targetNamespace = "urn:oasis:names:tc:ebxml-regrep:xsd:query:3.0", partName = "body")
        AdhocQueryRequest body);

}
