package org.mule.modules.gref;

import org.mule.api.annotations.Config;
import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Processor;

import java.util.List;

import org.mule.api.annotations.lifecycle.Start;
import org.mule.api.annotations.param.Default;
import org.mule.modules.gref.client.GrefClient;
import org.mule.modules.gref.config.ConnectorConfig;
import org.mule.modules.gref.entities.GrefInfo;
import org.mule.modules.gref.exception.GrefConnectorAccessDeniedException;
import org.mule.modules.gref.exception.GrefConnectorException;

@Connector(name="gref", schemaVersion = "1.0", friendlyName="Gref")
public class GrefConnector {

    @Config
    ConnectorConfig config;
 
    @Configurable
    @Default(value="localhost")
    private String host;

    @Configurable
    @Default(value="9494")
    private String port;
    
    private GrefClient client;
    
    @Start
    public void init() {
        setClient(new GrefClient(this));
    }
    
	/**
     * Get Canonical ID for a given object and its source system Id
     * @param sourceSystem Name of the source system whose ID is being passed
     * @param sourceSystemId Source system ID of the object
     * @param objectName Object name e.g. product, feature etc.
     * @return Canonical ID populated in Canonical object (GrefInfo POJO)
     * @throws Gref custom exceptions
     */
	@Processor
	public GrefInfo getCanonicalId(String sourceSystem, String sourceSystemId, String objectName) throws GrefConnectorAccessDeniedException, GrefConnectorException {
		return getClient().getCanonicalId(sourceSystem, sourceSystemId, objectName);
	}

	/**
     * Get Canonical Object(GrefInfo POJO) given its canonical ID
     * @param canonicalId Canonical ID for the given object e.g. b4f669e7-7c5e-420c-9f26-5f06318955a1
     * @param objectName Object name e.g. product, feature etc.
     * @return Canonical object (GrefInfo POJO)
     * @throws Gref custom exceptions
     */
	@Processor
	public List<GrefInfo> getCanonicalObject(String objectName, String canonicalId)
			throws GrefConnectorAccessDeniedException, GrefConnectorException {
		return getClient().getCanonicalObjectList(objectName, canonicalId);
	}
	
	/**
     * Get System Details given its canonical ID
     * @param canonicalId Canonical ID for the given object e.g. b4f669e7-7c5e-420c-9f26-5f06318955a1
     * @param objectName Object name e.g. product, feature etc.
     * @param systemName 
     * @return System details (GrefInfo POJO)
     * @throws Gref custom exceptions
     */
	@Processor
	public GrefInfo getSystemDetailsForCanonicalId(String objectName, String canonicalId, String systemName) 
			throws GrefConnectorAccessDeniedException, GrefConnectorException {
		return getClient().getCanonicalObject(objectName, canonicalId, systemName);
	}

	/**
     * Create Canonical Object given the object and source system details
     * @param sourceSystem Name of the source system whose ID is being passed
     * @param sourceSystemId Source system ID of the object
     * @param objectName Object name e.g. product, feature etc.
     * @return Canonical object (GrefInfo POJO)
     * @throws Gref custom exceptions
     */
	@Processor
	public GrefInfo createCanonicalObject(String sourceSystem, String objectName, String sourceSystemId)
			throws GrefConnectorAccessDeniedException, GrefConnectorException {
		return getClient().createCanonicalObject(sourceSystem, objectName, sourceSystemId);
	}
	
	/**
     * Modify Canonical Object - Add another source system for the canonical object
     * @param sourceSystem Name of the source system whose ID is being passed
     * @param sourceSystemId Source system ID of the object
     * @param objectName Object name e.g. product, feature etc.
     * @param canonicalId Canonical ID for the given object e.g. b4f669e7-7c5e-420c-9f26-5f06318955a1
     * @return Object reference populated in Canonical object(GrefInfo POJO)
     * @throws Gref custom exceptions
     */
	@Processor
	public GrefInfo modifyCanonicalObject(String sourceSystem, String sourceSystemId, String objectName, String canonicalId)
			throws GrefConnectorAccessDeniedException, GrefConnectorException {
		return getClient().modifyCanonicalObject(sourceSystem, sourceSystemId, objectName, canonicalId);
	}

	public GrefClient getClient() {
		return client;
	}

	public void setClient(GrefClient client) {
		this.client = client;
	}

    public ConnectorConfig getConfig() {
        return config;
    }

    public void setConfig(ConnectorConfig config) {
        this.config = config;
    }
    
    public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}
}