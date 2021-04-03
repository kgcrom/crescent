package com.tistory.devyongsik.crescent.admin.controller;

import com.tistory.devyongsik.crescent.admin.entity.MorphResult;
import com.tistory.devyongsik.crescent.admin.entity.MorphToken;
import com.tistory.devyongsik.crescent.admin.service.MorphService;
import com.tistory.devyongsik.crescent.collection.entity.Collection;
import com.tistory.devyongsik.crescent.collection.entity.Collections;
import com.tistory.devyongsik.crescent.config.CrescentCollectionHandler;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class MorphAdminMainController {

	private MorphService morphServiceImpl;
	private CrescentCollectionHandler collectionHandler;

	public MorphAdminMainController(MorphService morphServiceImpl, CrescentCollectionHandler collectionHandler) {
		this.morphServiceImpl = morphServiceImpl;
		this.collectionHandler = collectionHandler;
	}

	@RequestMapping("/morphMain")
	public ModelAndView morphMain(@RequestParam(value="col_name", required=false) String selectedCollectionName) {
		
		Collections collections = collectionHandler.getCrescentCollections();
		
		if(selectedCollectionName == null) {
			selectedCollectionName = collections.getCrescentCollections().get(0).getName();
		}
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("selectedCollectionName", selectedCollectionName);
		
		List<Collection> collectionList = collections.getCrescentCollections();
		
		modelAndView.addObject("collectionList", collectionList);
		modelAndView.addObject("selectedCollection", collections.getCrescentCollection(selectedCollectionName));
		
		modelAndView.setViewName("/admin/morphMain");

		return modelAndView;
	}
	
	@RequestMapping("/doMorphTest")
	public ModelAndView morphTest(@RequestParam(value="keyword") String keyword
								, @RequestParam(value="col_name", required=false) String selectedCollectionName) throws Exception {
		Collections collections = collectionHandler.getCrescentCollections();
		
		if(selectedCollectionName == null) {
			selectedCollectionName = collections.getCrescentCollections().get(0).getName();
		}
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("selectedCollectionName", selectedCollectionName);
		
		List<Collection> collectionList = collections.getCrescentCollections();
		
		modelAndView.addObject("collectionList", collectionList);
		modelAndView.addObject("selectedCollection", collections.getCrescentCollection(selectedCollectionName));
		
		
		log.debug("keyword : {}, collectionName : {}", keyword, selectedCollectionName);
		
		List<MorphToken> resultTokenListIndexingMode = morphServiceImpl.getTokens(keyword, true, selectedCollectionName);
		List<MorphToken> resultTokenListQueryMode = morphServiceImpl.getTokens(keyword, false, selectedCollectionName);
		
		modelAndView.setViewName("/admin/morphMain");

		modelAndView.addObject("indexingModeList", resultTokenListIndexingMode);
		modelAndView.addObject("queryModeList", resultTokenListQueryMode);
		
		return modelAndView;
	}
	
	@RequestMapping("/doMorphTestAjax")
	public void morphTestAjax(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String keyword = request.getParameter("keyword");
		String selectedCollectionName = request.getParameter("collectionName");
		
		log.debug("keyword : {}, collectionName : {}", keyword, selectedCollectionName);
		
		Collections collections = collectionHandler.getCrescentCollections();
		
		if(selectedCollectionName == null) {
			selectedCollectionName = collections.getCrescentCollections().get(0).getName();
		}
		
		List<MorphToken> resultTokenListIndexingMode = morphServiceImpl.getTokens(keyword, true, selectedCollectionName);
		List<MorphToken> resultTokenListQueryMode = morphServiceImpl.getTokens(keyword, false, selectedCollectionName);
		
		List<MorphResult> morphIndexingTestResult = new ArrayList<MorphResult>();
		List<MorphResult> morphQueryTestResult = new ArrayList<MorphResult>();
		
		Map<String, List<MorphResult>> morphTestResultSet = new HashMap<String, List<MorphResult>>();
		
		for(MorphToken token : resultTokenListIndexingMode) {
			MorphResult morphResult = new MorphResult();
			morphResult.setWord(token.getTerm());
			morphResult.setType(token.getType());
			morphResult.setStartOffset(token.getStartOffset());
			morphResult.setEndOffset(token.getEndOffset());
			
			morphIndexingTestResult.add(morphResult);
		}
		
		for(MorphToken token : resultTokenListQueryMode) {
			MorphResult morphResult = new MorphResult();
			morphResult.setWord(token.getTerm());
			morphResult.setType(token.getType());
			morphResult.setStartOffset(token.getStartOffset());
			morphResult.setEndOffset(token.getEndOffset());
			
			morphQueryTestResult.add(morphResult);
		}
		
		morphTestResultSet.put("indexResult", morphIndexingTestResult);
		morphTestResultSet.put("queryResult", morphQueryTestResult);
		
		ObjectMapper mapper = new ObjectMapper();
		String morphResult = mapper.writeValueAsString(morphTestResultSet);
		
		log.info("morphResult : {}", morphResult);
		
		response.setContentType("application/json;  charset=UTF-8");
		PrintWriter writer = response.getWriter();
		writer.write(morphResult);
		writer.flush();
		writer.close();
	}
}
