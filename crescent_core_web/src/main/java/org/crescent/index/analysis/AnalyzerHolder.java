package org.crescent.index.analysis;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("analyzer")
public class AnalyzerHolder {

	@XStreamAsAttribute
	private String type;
	
	@XStreamAlias("className")
	@XStreamAsAttribute
	private String className;

	public String getType() {
		return type;
	}

	public String getClassName() {
		return className;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setClassName(String className) {
		this.className = className;
	}
}
