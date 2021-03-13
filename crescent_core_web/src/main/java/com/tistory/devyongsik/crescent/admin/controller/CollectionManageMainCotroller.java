package com.tistory.devyongsik.crescent.admin.controller;

import com.tistory.devyongsik.crescent.admin.service.CollectionManageService;
import com.tistory.devyongsik.crescent.collection.entity.CrescentCollection;
import com.tistory.devyongsik.crescent.collection.entity.CrescentCollections;
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
		
		CrescentCollections crescentCollections = collectionHandler.getCrescentCollections();
		
		if(selectedCollectionName == null) {
			selectedCollectionName = crescentCollections.getCrescentCollections().get(0).getName();
		}
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("selectedCollectionName", selectedCollectionName);
		
		List<CrescentCollection> crescentCollectionList = crescentCollections.getCrescentCollections();
		
		modelAndView.addObject("crescentCollectionList", crescentCollectionList);
		modelAndView.addObject("selectedCollection", crescentCollections.getCrescentCollection(selectedCollectionName));
		modelAndView.setViewName("/admin/collectionManageMain");
		
		
		return modelAndView;
	}
	
	@RequestMapping("/collectionUpdate")
	public ModelAndView collectionUpdate(HttpServletRequest request, HttpServletResponse response) {

		CrescentCollections crescentCollections = collectionHandler.getCrescentCollections();
		
		CrescentCollection selectedCollection = collectionManageServiceImpl.updateCollectionInfo(request);
		
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("crescentCollectionList", crescentCollections.getCrescentCollections());
		modelAndView.addObject("selectedCollection", crescentCollections.getCrescentCollection(selectedCollection.getName()));
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
		
		CrescentCollection selectedCollection = collectionManageServiceImpl.addCollectionInfo(request);
		CrescentCollections crescentCollections = collectionHandler.getCrescentCollections();
	
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("crescentCollectionList", crescentCollections.getCrescentCollections());
		modelAndView.addObject("selectedCollection", crescentCollections.getCrescentCollection(selectedCollection.getName()));
		modelAndView.addObject("selectedCollectionName", selectedCollection.getName());
		
		
		modelAndView.setViewName("/admin/collectionManageMain");
		
		
		return modelAndView;
	}
	
	@RequestMapping("/deleteCollection")
	public ModelAndView deleteCollection(@RequestParam(value="collectionName") String collectionName ) {
		
		collectionManageServiceImpl.deleteCollectionInfo(collectionName);
		
		ModelAndView modelAndView = new ModelAndView();
		
		CrescentCollections crescentCollections = collectionHandler.getCrescentCollections();
		String selectedCollectionName = crescentCollections.getCrescentCollections().get(0).getName();
		
		modelAndView.addObject("selectedCollectionName", selectedCollectionName);
		modelAndView.addObject("crescentCollectionList", crescentCollections.getCrescentCollections());
		modelAndView.addObject("selectedCollection", crescentCollections.getCrescentCollection(selectedCollectionName));
		
		modelAndView.setViewName("/admin/collectionManageMain");
		
		
		return modelAndView;
	}
}
