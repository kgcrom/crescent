package org.crescent.search.entity;

import java.security.InvalidParameterException;
import java.util.Map;
import org.crescent.collection.entity.Collection;
import org.crescent.collection.entity.CollectionField;
import org.crescent.utils.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchRequestValidator {

	private static final Logger logger = LoggerFactory.getLogger(SearchRequestValidator.class);

	// TODO Solr standard query parser와 비슷하게 구현, valid와 parser 합칠 수 있음
	// https://solr.apache.org/guide/8_9/the-standard-query-parser.html
	public boolean valid(SearchRequest searchRequest, Collection collection) throws InvalidParameterException {
		if (collection == null) {
			logger.error("invalid collection name: {}", searchRequest.getCollectionName());
			throw new InvalidParameterException("doesn't exist colleciton. name: " + searchRequest.getCollectionName());
		}

		validSearchField(searchRequest, collection);
		validSortField(searchRequest, collection);
		// TODO validation check query and filter query

		//page num
		if (searchRequest.getPageNum() != null && !StringUtils.isNumeric(searchRequest.getPageNum())) {
			logger.error("invalid page number: {}", searchRequest.getPageNum());
			throw new InvalidParameterException();
		}

		return true;
	}

	private void validSortField(SearchRequest searchRequest, Collection collection) throws InvalidParameterException {
		String sortQueryString = searchRequest.getSort();
		if (sortQueryString == null || "".equals(sortQueryString) || "null".equals(sortQueryString)) {
			return;
		}

		String[] parts = sortQueryString.split(",");
		if (parts.length == 0) {
			logger.error("invalid sortquery syntax: {}", sortQueryString);
			throw new IllegalArgumentException("");
		}

		for (int i = 0; i < parts.length; i++) {
			String part = parts[i].trim(); //part = field desc

			int idx = part.indexOf(' ');

			if (idx <= 0) {
				logger.error("has no order condition: {}", sortQueryString);
				throw new InvalidParameterException();
			}

			part = part.substring(0, idx).trim(); //part = field
			Map<String, CollectionField> collectionFields = collection.getCrescentFieldByName();

			CollectionField f = collectionFields.get(part);

			if (f == null) {
				logger.error("don't exist field: {}", String.join(", ", parts));
				throw new InvalidParameterException();
			}
			// TODO docValue 옵션 추가되면 if 조건문도 변경 필요
			if (f.isAnalyze()) {
				logger.error("analyzed filed are not sortable, {}", String.join(", ", parts));
				throw new InvalidParameterException();
			}
		}
	}

	private void validSearchField(SearchRequest searchRequest, Collection collection) throws InvalidParameterException {
		if (searchRequest.getSearchField() == null) {
			return;
		}
		String[] requestSearchFieldNames = searchRequest.getSearchField().split(",");

		if (requestSearchFieldNames == null || requestSearchFieldNames[0].length() == 0) {
			return;
		}

		for (String requestFieldName : requestSearchFieldNames) {
			if (!collection.getCrescentFieldByName().containsKey(requestFieldName)) {
				logger.error("invalid search field: ", searchRequest.getSearchField());
				throw new InvalidParameterException(collection.getName() + "has no " + requestFieldName + "field.");
			}
		}
	}
}
