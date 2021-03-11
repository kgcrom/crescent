package com.tistory.devyongsik.crescent.admin.service;

import com.tistory.devyongsik.crescent.admin.entity.IndexInfo;
import com.tistory.devyongsik.crescent.collection.entity.CrescentCollection;

import java.io.IOException;

public interface IndexFileManageService {
	public IndexInfo getIndexInfo(CrescentCollection selectCollection, String selectTopField) throws IOException;
}
