package org.crescent.collection.entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

@XStreamAlias("collections")
public class Collections {

	@XStreamImplicit(itemFieldName="collection")
	private List<Collection> collections = null;

	public List<Collection> getCollections() {
		return collections;
	}
}
