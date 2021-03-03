package com.tistory.devyongsik.crescent.search.entity;


import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

public class RequestBuilderTest {

	@Test
	public void keyword() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setParameter("keyword", "nike");
		
		RequestBuilder<SearchRequest> builder = new RequestBuilder<SearchRequest>();
		SearchRequest searchRequest = builder.mappingRequestParam(request, SearchRequest.class);
		
		assertEquals("nike", searchRequest.getKeyword());
	}
	
	@Test
	public void collectionName() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setParameter("col_name", "test");
		
		RequestBuilder<SearchRequest> builder = new RequestBuilder<SearchRequest>();
		SearchRequest searchRequest = builder.mappingRequestParam(request, SearchRequest.class);
		
		assertEquals("test", searchRequest.getCollectionName());
	}
	
	@Test
	public void pageSize() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setParameter("page_size", "50");
		
		RequestBuilder<SearchRequest> builder = new RequestBuilder<SearchRequest>();
		SearchRequest searchRequest = builder.mappingRequestParam(request, SearchRequest.class);
		
		assertEquals("50", searchRequest.getPageSize());
	}
	
	@Test
	public void sort() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setParameter("sort", "name desc");
		
		RequestBuilder<SearchRequest> builder = new RequestBuilder<SearchRequest>();
		SearchRequest searchRequest = builder.mappingRequestParam(request, SearchRequest.class);
		
		assertEquals("name desc", searchRequest.getSort());
	}
	
	@Test
	public void searchField() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setParameter("search_field", "title, contents");
		
		RequestBuilder<SearchRequest> builder = new RequestBuilder<SearchRequest>();
		SearchRequest searchRequest = builder.mappingRequestParam(request, SearchRequest.class);
		
		assertEquals("title, contents", searchRequest.getSearchField());
	}
	
	@Test
	public void customQuery() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setParameter("cq", "title:\"jang\"");
		
		RequestBuilder<SearchRequest> builder = new RequestBuilder<SearchRequest>();
		SearchRequest searchRequest = builder.mappingRequestParam(request, SearchRequest.class);
		
		assertEquals("title:\"jang\"", searchRequest.getCustomQuery());
	}
}
