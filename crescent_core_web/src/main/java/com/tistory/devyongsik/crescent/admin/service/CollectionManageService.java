package com.tistory.devyongsik.crescent.admin.service;

import javax.servlet.http.HttpServletRequest;

import com.tistory.devyongsik.crescent.collection.entity.Collection;

public interface CollectionManageService {
	
	Collection updateCollectionInfo(HttpServletRequest request);
	Collection addCollectionInfo(HttpServletRequest request);
	void deleteCollectionInfo(String collectionName);
}
