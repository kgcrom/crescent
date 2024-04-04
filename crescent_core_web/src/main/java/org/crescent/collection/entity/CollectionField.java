package org.crescent.collection.entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
@XStreamAlias("field")
public class CollectionField implements Cloneable {
	@XStreamAsAttribute
	private String name;
	
	@XStreamAsAttribute
	private boolean stored;
	
	@XStreamAsAttribute
	private boolean indexed;
	
	@XStreamAsAttribute
	private String type;
	
	@XStreamAsAttribute
	private boolean analyze;

	@XStreamAsAttribute
	private boolean removeHtmlTag;

	// TODO: implement termxxx component
	// ref url: https://solr.apache.org/guide/7_6/the-term-vector-component.html

	@Override
	public Object clone() throws CloneNotSupportedException {
		Object o = super.clone();
		return o;
	}

	public String getName() {
		return name;
	}

	public boolean isStored() {
		return stored;
	}

	public boolean isIndexed() {
		return indexed;
	}

	public String getType() {
		return type;
	}

	public boolean isAnalyze() {
		return analyze;
	}

	public boolean isRemoveHtmlTag() {
		return removeHtmlTag;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setStored(boolean stored) {
		this.stored = stored;
	}

	public void setIndexed(boolean indexed) {
		this.indexed = indexed;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setAnalyze(boolean analyze) {
		this.analyze = analyze;
	}

	public void setRemoveHtmlTag(boolean removeHtmlTag) {
		this.removeHtmlTag = removeHtmlTag;
	}
}
