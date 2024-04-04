package org.crescent.admin.controller;

import org.crescent.admin.entity.IndexInfo;
import org.crescent.admin.service.IndexFileManageService;
import org.crescent.collection.entity.Collection;
import org.crescent.config.CollectionHandler;
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
	private IndexFileManageService indexFileManageService;

	@Autowired
	private CollectionHandler collectionHandler;

	@RequestMapping("/indexFileManageMain")
	public ModelAndView indexFileManageMain(@RequestParam(value = "selectCollectionName", required = false) String selectCollectionName
			, @RequestParam(value = "selectTopField", required = false) String selectTopField) throws Exception {

		// TODO collection name과 top field를 parameter로 받아서 indexFileManageService.getIndexInfo 리턴값을 response로 내준다.
		ModelAndView modelAndView = new ModelAndView();
		Map<String, Object> result = new HashMap<>();
		String topField = selectTopField;

		modelAndView.setViewName("/admin/indexFileManageMain");

		List<String> collectionNames = new ArrayList<>();

		for (Collection collection : collectionHandler.getCollections()) {
			collectionNames.add(collection.getName());
		}

		String collectionName = selectCollectionName;
		if (collectionName == null) {
			collectionName = collectionNames.get(0);
		}
		Collection selectCollection = collectionHandler.getCollection(collectionName);
		if (topField == null) {
			topField = selectCollection.getFields().get(0).getName();
		}
		IndexInfo indexInfo = indexFileManageService.getIndexInfo(selectCollection, selectTopField);

		result.put("collectionNames", collectionNames);
		result.put("selectCollectionName", collectionName);

		if (indexInfo != null) {
			result.put("hasIndexInfo", true);
			result.put("indexName", indexInfo.getIndexName());
			result.put("topRankingFields", indexInfo.getFieldNames());
			result.put("selectTopField", topField);
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

}
