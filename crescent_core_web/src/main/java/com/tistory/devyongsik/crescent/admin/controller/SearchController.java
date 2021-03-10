package com.tistory.devyongsik.crescent.admin.controller;

import com.tistory.devyongsik.crescent.search.JsonFormConverter;
import com.tistory.devyongsik.crescent.search.entity.RequestBuilder;
import com.tistory.devyongsik.crescent.search.entity.SearchRequest;
import com.tistory.devyongsik.crescent.search.entity.SearchResult;
import com.tistory.devyongsik.crescent.search.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@Controller
public class SearchController {

	@Autowired
	@Qualifier("searchService")
	private SearchService searchService;
	
	@RequestMapping("/search")
	public void searchDocument(HttpServletRequest request, HttpServletResponse response) throws Exception {

		RequestBuilder<SearchRequest> requestBuilder = new RequestBuilder<SearchRequest>();
		SearchRequest searchRequest = requestBuilder.mappingRequestParam(request, SearchRequest.class);
		
		SearchResult searchResult = searchService.search(searchRequest);
		
		log.debug("search result : {}", searchResult.getResultList());
		
		JsonFormConverter converter = new JsonFormConverter();
		PrintWriter writer = null;
		try {
			
			String jsonForm = converter.convert(searchResult.getSearchResult());
			
			log.debug("search result json form : {}", jsonForm);
			
			response.setContentType("application/json;  charset=UTF-8");
			
			writer = response.getWriter();
			writer.write(jsonForm);
			writer.flush();
			writer.close();
			
		} catch (IOException e) {
			log.error("error : ", e);
		}
	}
}
