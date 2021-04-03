package com.tistory.devyongsik.crescent.admin.controller;

import com.tistory.devyongsik.crescent.admin.service.CollectionManageService;
import com.tistory.devyongsik.crescent.collection.entity.Collection;
import com.tistory.devyongsik.crescent.collection.entity.Collections;
import com.tistory.devyongsik.crescent.config.CrescentCollectionHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class CollectionManageMainCotroller {

	private final CollectionManageService collectionManageServiceImpl;
	private final CrescentCollectionHandler collectionHandler;

	public CollectionManageMainCotroller(CollectionManageService collectionManageServiceImpl, CrescentCollectionHandler collectionHandler) {
		this.collectionManageServiceImpl = collectionManageServiceImpl;
		this.collectionHandler = collectionHandler;
	}

	@RequestMapping("/collectionManageMain")
	public ModelAndView collectionManageMain(@RequestParam(value="collectionName", required=false) String selectedCollectionName) {
		
		Collections collections = collectionHandler.getCrescentCollections();
		
		if(selectedCollectionName == null) {
			selectedCollectionName = collections.getCrescentCollections().get(0).getName();
		}
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("selectedCollectionName", selectedCollectionName);
		
		List<Collection> collectionList = collections.getCrescentCollections();
		
		modelAndView.addObject("collectionList", collectionList);
		modelAndView.addObject("selectedCollection", collections.getCrescentCollection(selectedCollectionName));
		modelAndView.setViewName("/admin/collectionManageMain");
		
		
		return modelAndView;
	}
	
	@RequestMapping("/collectionUpdate")
	public ModelAndView collectionUpdate(HttpServletRequest request, HttpServletResponse response) {

		Collections collections = collectionHandler.getCrescentCollections();
		
		Collection selectedCollection = collectionManageServiceImpl.updateCollectionInfo(request);
		
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("collectionList", collections.getCrescentCollections());
		modelAndView.addObject("selectedCollection", collections.getCrescentCollection(selectedCollection.getName()));
		modelAndView.addObject("selectedCollectionName", selectedCollection.getName());
		
		modelAndView.setViewName("/admin/collectionManageMain");
		
		
		return modelAndView;
		
	}
	
	@RequestMapping("/addNewCollection")
	public ModelAndView addNewCollection() {
		
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/admin/addNewCollectionForm");
		
		
		return modelAndView;
	}
	
	@RequestMapping("/collectionAdd")
	public ModelAndView collectionAdd(HttpServletRequest request, HttpServletResponse response) {
		
		Collection selectedCollection = collectionManageServiceImpl.addCollectionInfo(request);
		Collections collections = collectionHandler.getCrescentCollections();
	
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("collectionList", collections.getCrescentCollections());
		modelAndView.addObject("selectedCollection", collections.getCrescentCollection(selectedCollection.getName()));
		modelAndView.addObject("selectedCollectionName", selectedCollection.getName());
		
		
		modelAndView.setViewName("/admin/collectionManageMain");
		
		
		return modelAndView;
	}
	
	@RequestMapping("/deleteCollection")
	public ModelAndView deleteCollection(@RequestParam(value="collectionName") String collectionName ) {
		
		collectionManageServiceImpl.deleteCollectionInfo(collectionName);
		
		ModelAndView modelAndView = new ModelAndView();
		
		Collections collections = collectionHandler.getCrescentCollections();
		String selectedCollectionName = collections.getCrescentCollections().get(0).getName();
		
		modelAndView.addObject("selectedCollectionName", selectedCollectionName);
		modelAndView.addObject("collectionList", collections.getCrescentCollections());
		modelAndView.addObject("selectedCollection", collections.getCrescentCollection(selectedCollectionName));
		
		modelAndView.setViewName("/admin/collectionManageMain");
		
		
		return modelAndView;
	}
}
