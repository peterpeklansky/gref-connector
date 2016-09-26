package org.mule.modules.gref.entities;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Generated;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonAnyGetter;
import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "canonical",
    "source",
    "sourceid",
    "object",
    "object_type",
    "ref"
})
public class GrefInfo {
	
	@JsonProperty("canonical")
	private String canonical;
	
	@JsonProperty("source")
	private String source;
	
	@JsonProperty("sourceid")
	private String sourceid;
	
	@JsonProperty("object")
	private String object;
	
	@JsonProperty("object_type")
	private String object_type;
	
	@JsonProperty("ref")
	private String ref;
	
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("canonical")
	public String getCanonical() {
		return canonical;
	}

	@JsonProperty("canonical")
	public void setCanonical(String canonical) {
		this.canonical = canonical;
	}

	@JsonProperty("source")
	public String getSource() {
		return source;
	}

	@JsonProperty("source")
	public void setSource(String source) {
		this.source = source;
	}

	@JsonProperty("sourceid")
	public String getSourceid() {
		return sourceid;
	}

	@JsonProperty("sourceid")
	public void setSourceid(String sourceid) {
		this.sourceid = sourceid;
	}

	@JsonProperty("object")
	public String getObject() {
		return object;
	}

	@JsonProperty("object")
	public void setObject(String object) {
		this.object = object;
	}

	@JsonProperty("object_type")
	public String getObject_type() {
		return object_type;
	}

	@JsonProperty("object_type")
	public void setObject_type(String object_type) {
		this.object_type = object_type;
	}
	
	@JsonProperty("ref")
	public String getRef() {
		return ref;
	}

	@JsonProperty("ref")
	public void setRef(String ref) {
		this.ref = ref;
	}

	@Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
    @Override
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }
    
    @JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperties(String name, Object value) {
		this.additionalProperties.put(name, value);
	}
}