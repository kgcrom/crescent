package org.crescent.index.entity;

import java.util.List;
import java.util.Map;

public class IndexingRequestForm {
	
	private String command;
	private List<Map<String, String>> documentList;
	private String query;
	private String indexingType;

	public String getCommand() {
		return command;
	}

	public List<Map<String, String>> getDocumentList() {
		return documentList;
	}

	public String getQuery() {
		return query;
	}

	public String getIndexingType() {
		return indexingType;
	}
}
