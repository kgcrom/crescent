package com.tistory.devyongsik.crescent.collection.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("collections")
public class Collections {

	@XStreamImplicit(itemFieldName="collection")
	private List<Collection> collections = null;

	@XStreamOmitField
	private Map<String, Collection> crescentCollectionsMap = null;
	
	private void lazyLoadMap() {
		if(crescentCollectionsMap == null) {
			crescentCollectionsMap = new HashMap<String, Collection>();
			
			for(Collection c : collections) {
				crescentCollectionsMap.put(c.getName(), c);
			}
		}
	}
	
	public Collection getCrescentCollection(String name) {
		lazyLoadMap();
		
		return crescentCollectionsMap.get(name); 
	}
	
	public Map<String, Collection> getCrescentCollectionsMap() {
		lazyLoadMap();
		
		return crescentCollectionsMap;
	}

	public void setCrescentCollectionsMap(
			Map<String, Collection> crescentCollectionsMap) {
		this.crescentCollectionsMap = crescentCollectionsMap;
	}

	public List<Collection> getCrescentCollections() {
		return collections;
	}

	public void setCrescentCollections(List<Collection> collections) {
		this.collections = collections;
	}

	@Override
	public String toString() {
		return "CrescentCollections [crescentCollections="
				+ collections + "]";
	}
}
