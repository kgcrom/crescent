package com.tistory.devyongsik.crescent.admin.service;

import com.tistory.devyongsik.crescent.admin.entity.IndexInfo;
import com.tistory.devyongsik.crescent.collection.entity.Collection;

import java.io.IOException;

public interface IndexFileManageService {
	IndexInfo getIndexInfo(Collection selectCollection, String selectTopField) throws IOException;
}
