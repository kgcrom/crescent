package com.tistory.devyongsik.crescent.admin.controller;

import com.tistory.devyongsik.crescent.admin.entity.IndexInfo;
import com.tistory.devyongsik.crescent.admin.service.IndexFileManageService;
import com.tistory.devyongsik.crescent.collection.entity.Collection;
import com.tistory.devyongsik.crescent.config.CrescentCollectionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class IndexFileManageController {

	@Autowired
	private IndexFileManageService indexFileManageServiceImpl;

	@Autowired
	private CrescentCollectionHandler collectionHandler;

	@RequestMapping("/indexFileManageMain")
	public ModelAndView indexFileManageMain(@RequestParam(value = "selectCollectionName", required = false) String selectCollectionName
			, @RequestParam(value = "selectTopField", required = false) String selectTopField) throws Exception {

		ModelAndView modelAndView = new ModelAndView();
		Map<String, Object> result = new HashMap<String, Object>();

		modelAndView.setViewName("/admin/indexFileManageMain");

		List<String> collectionNames = new ArrayList<String>();

		for (Collection collection : collectionHandler.getCrescentCollections().getCrescentCollections()) {
			collectionNames.add(collection.getName());
		}

		if (selectCollectionName == null) {
			selectCollectionName = collectionNames.get(0);
		}
		Collection selectCollection = collectionHandler
				.getCrescentCollections()
				.getCrescentCollection(selectCollectionName);

		if (selectTopField == null) {
			selectTopField = selectCollection.getFields().get(0).getName();
		}
		IndexInfo indexInfo = indexFileManageServiceImpl.getIndexInfo(selectCollection, selectTopField);

		result.put("collectionNames", collectionNames);
		result.put("selectCollectionName", selectCollectionName);

		if (indexInfo != null) {
			result.put("hasIndexInfo", true);
			result.put("indexName", indexInfo.getIndexName());
			result.put("topRankingFields", indexInfo.getFieldNames());
			result.put("selectTopField", selectTopField);
			result.put("numOfField", indexInfo.getNumOfField());
			result.put("numOfDoc", indexInfo.getNumOfDoc());
			result.put("hasDel", indexInfo.isHasDel());
			result.put("indexVersion", indexInfo.getIndexVersion());
			result.put("termCountByFieldName", indexInfo.getTermCountByFieldNameMap());
			result.put("numOfTerm", indexInfo.getTotalTermCount());
			result.put("termStatsList", indexInfo.getCrescentTermStatsList());
		}

		modelAndView.addObject("RESULT", result);
		return modelAndView;
	}

	@RequestMapping("/indexFileManageDoc")
	public ModelAndView indexFileManageDoc() {
		ModelAndView modelAndView = new ModelAndView();

		return modelAndView;
	}
}
