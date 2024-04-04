package org.crescent.collection.entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("sortField")
public class SortField {

	@XStreamAsAttribute
	private String source;
	
	@XStreamAsAttribute
	private String dest;

	public String getSource() {
		return source;
	}

	public String getDest() {
		return dest;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public void setDest(String dest) {
		this.dest = dest;
	}
}
