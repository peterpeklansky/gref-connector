/**
 * 
 */
package org.mule.modules.gref.client;

import java.util.Iterator;
import java.util.List;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.*;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;

import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.mule.modules.gref.GrefConnector;
import org.mule.modules.gref.entities.GrefInfo;
import org.mule.modules.gref.exception.GrefConnectorAccessDeniedException;
import org.mule.modules.gref.exception.GrefConnectorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author arpansakala
 *
 */
public class GrefClient {
	
	private transient final Logger LOG = LoggerFactory.getLogger(this.getClass());
	private Client client;
	private WebResource apiResource;
	private GrefConnector connector;
	
	/**
	 * Constructor - Setup Jersey Client and WebResource objects
	 * @param connector
	 */
	public GrefClient (GrefConnector connector) {
		setConnector(connector);
		
		ClientConfig clientConfig = new DefaultClientConfig();
		clientConfig.getClasses().add(JacksonJaxbJsonProvider.class);
		clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        this.client = Client.create(clientConfig);
        this.apiResource = this.client.resource(getApiUrl());
	}
	
	/**
	 * Construct base URI for Gref ReST APIs
	 * @return Base URI
	 */
	private String getApiUrl() {
		return (new String("http://" + getConnector().getHost() + ":" + getConnector().getPort()));
	}

	/**
     * Client method - Get Canonical ID for a given object and its source system Id
     * @param sourceSystem Name of the source system whose ID is being passed
     * @param sourceSystemId Source system ID of the object
     * @param objectName Object name e.g. product, feature etc.
     * @return Canonical ID populated in Canonical object (GrefInfo POJO)
     * @throws Gref custom exceptions
     */
	public GrefInfo getCanonicalId(String sourceSystem, String sourceSystemId, String objectName) 
			throws GrefConnectorAccessDeniedException, GrefConnectorException {
		WebResource webResource = getApiResource().path("sources").path(sourceSystem).path("objects").path(objectName).path(sourceSystemId);
		GrefInfo grefInfo = execute(webResource, GrefInfo.class);
		LOG.info("Canonical Object {}",grefInfo.toString());
		return grefInfo;
	}
	
	/**
     * Client method - Get System Details given its canonical ID
     * @param canonicalId Canonical ID for the given object e.g. b4f669e7-7c5e-420c-9f26-5f06318955a1
     * @param objectName Object name e.g. product, feature etc.
     * @param systemName 
     * @return System details (GrefInfo POJO)
     * @throws Gref custom exceptions
     */
	public GrefInfo getCanonicalObject(String objectName, String canonicalId, String systemName) 
			throws GrefConnectorAccessDeniedException, GrefConnectorException {

		List<GrefInfo> grefInfoList = getCanonicalObjectList(objectName, canonicalId);
		
		Iterator<GrefInfo> grefInfoIterator = grefInfoList.iterator();	
		while (grefInfoIterator.hasNext())
		{
			GrefInfo grefInfo = grefInfoIterator.next();
			if (systemName.equalsIgnoreCase(grefInfo.getSource())) {
				return grefInfo;
			}	
		}
		throw new GrefConnectorException(
				String.format("ERROR - statusCode: 401 - message: canonical object not "
						+ "found for the system : %s given canonical ID : %s", systemName, canonicalId));
	}
	/**
     * Client method - Get Canonical Object(GrefInfo POJO) given its canonical ID
     * @param canonicalId Canonical ID for the given object e.g. b4f669e7-7c5e-420c-9f26-5f06318955a1
     * @param objectName Object name e.g. product, feature etc.
     * @return Canonical object (GrefInfo POJO)
     * @throws Gref custom exceptions
     */
	public List<GrefInfo> getCanonicalObjectList(String objectName, String canonicalId)
			throws GrefConnectorAccessDeniedException, GrefConnectorException {
		WebResource webResource = getApiResource().path("objects").path(objectName).path(canonicalId);
		return execute(webResource);
	}
	
	/**
     * Client method - Create Canonical Object given the object and source system details
     * @param sourceSystem Name of the source system whose ID is being passed
     * @param sourceSystemId Source system ID of the object
     * @param objectName Object name e.g. product, feature etc.
     * @return Canonical object (GrefInfo POJO)
     * @throws Gref custom exceptions
     */
	public GrefInfo createCanonicalObject(String sourceSystem, String objectName, String sourceId)
		throws GrefConnectorAccessDeniedException, GrefConnectorException {
		WebResource webResource = getApiResource().path("sources").path(sourceSystem).path("objects").path(objectName);
		GrefInfo grefInfo = executePost(webResource, GrefInfo.class, new String("{\"sourceid\":" + "\"" + sourceId + "\"}"));
		LOG.info("Canonical Object {}",grefInfo.toString());
		return grefInfo;
	}
	
	/**
     * Client method - Modify Canonical Object - Add another source system for the canonical object
     * @param sourceSystem Name of the source system whose ID is being passed
     * @param sourceSystemId Source system ID of the object
     * @param objectName Object name e.g. product, feature etc.
     * @param canonicalId Canonical ID for the given object e.g. b4f669e7-7c5e-420c-9f26-5f06318955a1
     * @return Object reference populated in Canonical object(GrefInfo POJO)
     * @throws Gref custom exceptions
     */
	public GrefInfo modifyCanonicalObject(String sourceSystem, String sourceId, String objectName, String canonicalId)
			throws GrefConnectorAccessDeniedException, GrefConnectorException {
			WebResource webResource = getApiResource().path("objects").path(objectName).path(canonicalId);
			String inputMessage = new String("{\"source\":" + "\"" + sourceSystem + "\",\"sourceid\":\"" + sourceId + "\"}");
			GrefInfo grefInfo = executePut(webResource, GrefInfo.class, inputMessage);
			LOG.info("Canonical Object {}",grefInfo.toString());
			return grefInfo;
	}
	
	/**
     * Executes PUT HTTP method
     * @param webResource
     * @param returnClass Entity POJO
     * @param inputMessage Request body
     * @return API response populated into GrefInfo object
     * @throws Gref custom exceptions
     */
	private <T> T executePut(WebResource webResource, Class<T> returnClass, String inputMessage) throws GrefConnectorAccessDeniedException, GrefConnectorException {
		ClientResponse clientResponse = webResource.accept(MediaType.APPLICATION_JSON).put(ClientResponse.class, inputMessage);
		
		if(clientResponse.getStatus() == 200) {
			return clientResponse.getEntity(returnClass);
		}else if(clientResponse.getStatus() == 401) {
			throw new GrefConnectorAccessDeniedException("Unauthorised access" + clientResponse.getEntity(String.class));
		}else {
			throw new GrefConnectorException(
					String.format("ERROR - statusCode: %d - message: %s",
                            clientResponse.getStatus(), clientResponse.getEntity(String.class)));
		}
	}

	/**
     * Executes POST HTTP method
     * @param webResource
     * @param returnClass Entity POJO
     * @param inputMessage Request body
     * @return API response populated into GrefInfo object
     * @throws Gref custom exceptions
     */
	private <T> T executePost(WebResource webResource, Class<T> returnClass, String inputMessage) throws GrefConnectorAccessDeniedException, GrefConnectorException {
		ClientResponse clientResponse = webResource.accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, inputMessage);
		
		if(clientResponse.getStatus() == 201) {
			return clientResponse.getEntity(returnClass);
		}else if(clientResponse.getStatus() == 401) {
			throw new GrefConnectorAccessDeniedException("Unauthorised access" + clientResponse.getEntity(String.class));
		}else {
			throw new GrefConnectorException(
					String.format("ERROR - statusCode: %d - message: %s",
                            clientResponse.getStatus(), clientResponse.getEntity(String.class)));
		}
	}

	/**
     * Executes GET HTTP method
     * @param webResource
     * @param method GET method
     * @param returnClass Entity POJO
     * @return API response populated into GrefInfo object
     * @throws Gref custom exceptions
     */
	private <T> T execute(WebResource webResource, Class<T> returnClass) throws GrefConnectorAccessDeniedException, GrefConnectorException {
		ClientResponse clientResponse = webResource.accept(MediaType.APPLICATION_JSON).method("GET",ClientResponse.class);
		
		if(clientResponse.getStatus() == 200) {
			return clientResponse.getEntity(returnClass);
		}else if(clientResponse.getStatus() == 401) {
			throw new GrefConnectorAccessDeniedException("Unauthorised access" + clientResponse.getEntity(String.class));
		}else {
			throw new GrefConnectorException(
					String.format("ERROR - statusCode: 404 - message: %s",
                            clientResponse.getStatus(), clientResponse.getEntity(String.class)));
		}
	}
	
	/**
     * Executes GET HTTP method - Overloaded method to return GrefInfo List
     * @param webResource
     * @return API response populated into GrefInfo List object
     * @throws Gref custom exceptions
     */
	private List<GrefInfo> execute(WebResource webResource) throws GrefConnectorAccessDeniedException, GrefConnectorException {
		ClientResponse clientResponse = webResource.accept(MediaType.APPLICATION_JSON).method("GET",ClientResponse.class);
		
		if(clientResponse.getStatus() == 200) {
			List<GrefInfo> grefInfo = clientResponse.getEntity(new GenericType<List<GrefInfo>>() {});
			LOG.info("Canonical Object {}",grefInfo.toString());
			return grefInfo;
		}else if(clientResponse.getStatus() == 401) {
			throw new GrefConnectorAccessDeniedException("Unauthorised access" + clientResponse.getEntity(String.class));
		}else {
			throw new GrefConnectorException(
					String.format("ERROR - statusCode: %d - message: %s",
                            clientResponse.getStatus(), clientResponse.getEntity(String.class)));
		}
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public WebResource getApiResource() {
		return apiResource;
	}

	public void setApiResource(WebResource apiResource) {
		this.apiResource = apiResource;
	}

	public GrefConnector getConnector() {
		return connector;
	}

	public void setConnector(GrefConnector connector) {
		this.connector = connector;
	}
}