package com.tistory.devyongsik.crescent.admin.controller;

import com.tistory.devyongsik.crescent.collection.entity.Collection;
import com.tistory.devyongsik.crescent.collection.entity.Collections;
import com.tistory.devyongsik.crescent.config.CrescentCollectionHandler;
import com.tistory.devyongsik.crescent.search.entity.RequestBuilder;
import com.tistory.devyongsik.crescent.search.entity.SearchRequest;
import com.tistory.devyongsik.crescent.search.entity.SearchResult;
import com.tistory.devyongsik.crescent.search.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class SearchTestMainController {

	private final SearchService searchServiceImpl;
	private final CrescentCollectionHandler collectionHandler;

	public SearchTestMainController(SearchService searchServiceImpl, CrescentCollectionHandler collectionHandler) {
		this.searchServiceImpl = searchServiceImpl;
		this.collectionHandler = collectionHandler;
	}

	@RequestMapping("/searchTestMain")
	public ModelAndView searchTestMain(@RequestParam(value = "col_name", required = false) String selectedCollectionName) {
		Collections collections = collectionHandler.getCrescentCollections();

		if (selectedCollectionName == null) {
			selectedCollectionName = collections.getCrescentCollections().get(0).getName();
		}

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("selectedCollectionName", selectedCollectionName);
		
		List<Collection> collectionList = collections.getCrescentCollections();
		
		modelAndView.addObject("collectionList", collectionList);
		modelAndView.addObject("selectedCollection", collections.getCrescentCollection(selectedCollectionName));
		modelAndView.setViewName("/admin/searchTestMain");
		
		log.debug("search Test main");

		return modelAndView;
	}

	@RequestMapping("/searchTest")
	public ModelAndView searchTest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		RequestBuilder<SearchRequest> requestBuilder = new RequestBuilder<SearchRequest>();
		SearchRequest searchRequest = requestBuilder.mappingRequestParam(request, SearchRequest.class);
		
		Map<String, Object> userRequest = new HashMap<String, Object>();
		userRequest.put("collectionName",searchRequest.getCollectionName());

		userRequest.put("customQuery", searchRequest.getCustomQuery());
		userRequest.put("keyword", searchRequest.getKeyword());
		userRequest.put("searchField", searchRequest.getSearchField());
		userRequest.put("sort", searchRequest.getSort());
		userRequest.put("pageNum", searchRequest.getPageNum());
		userRequest.put("pageSize", searchRequest.getPageSize());
		userRequest.put("ft", searchRequest.getFilter());
		userRequest.put("rq", searchRequest.getRegexQuery());

		SearchResult searchResult = searchServiceImpl.search(searchRequest);

		Collections collections = collectionHandler.getCrescentCollections();

		String selectedCollectionName = request.getParameter("col_name");
		if (selectedCollectionName == null) {
			selectedCollectionName = collections.getCrescentCollections().get(0).getName();
		}

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("selectedCollectionName", selectedCollectionName);
		
		List<Collection> collectionList = collections.getCrescentCollections();
		
		modelAndView.addObject("collectionList", collectionList);
		modelAndView.addObject("selectedCollection", collections.getCrescentCollection(selectedCollectionName));
		
		modelAndView.addObject("searchResult", searchResult);
		modelAndView.addObject("USER_REQUEST", userRequest);
		modelAndView.setViewName("/admin/searchTestMain");

		return modelAndView;
	}

}
