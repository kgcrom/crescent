package com.tistory.devyongsik.crescent.admin.controller;

import com.tistory.devyongsik.crescent.collection.entity.CrescentCollection;
import com.tistory.devyongsik.crescent.config.CrescentCollectionHandler;
import com.tistory.devyongsik.crescent.data.handler.Handler;
import com.tistory.devyongsik.crescent.data.handler.JsonDataHandler;
import com.tistory.devyongsik.crescent.index.entity.IndexingRequestForm;
import com.tistory.devyongsik.crescent.index.indexer.CrescentIndexerExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.Writer;

@Slf4j
@Controller
public class UpdateController {
	
	private final CrescentCollectionHandler collectionHandler;
	private final CrescentIndexerExecutor crescentIndexerExecutor;

	public UpdateController(CrescentCollectionHandler collectionHandler, CrescentIndexerExecutor crescentIndexerExecutor) {
		this.collectionHandler = collectionHandler;
		this.crescentIndexerExecutor = crescentIndexerExecutor;
	}

	@RequestMapping("/update")
	public void updateDocument(HttpServletRequest request, HttpServletResponse response) {
		
		String contentsType = request.getHeader("Content-type");
		
		//TODO contentsType별로 핸들러 분리
		//TODO 일단 json만..
		Handler handler = null;
		if("application/json".equals(contentsType)) {
			handler = new JsonDataHandler();
		}
		
		String collectionName = request.getParameter("collection_name");
		
		log.info("collection name : {}", collectionName);
		
		StringBuilder text = new StringBuilder();
		
		try {
			
			BufferedReader reader = request.getReader();
			String tmp = "";
			while((tmp = reader.readLine()) != null) {
				text.append(tmp);
			}
			
			reader.close();
			IndexingRequestForm indexingRequestForm = handler.handledData(text.toString());
			
			CrescentCollection collection = collectionHandler.getCrescentCollections().getCrescentCollection(collectionName);
			String message = crescentIndexerExecutor.indexing(collection, indexingRequestForm);
			
			Writer writer = null;
			
			response.setContentType("text/html;  charset=UTF-8");
			writer = response.getWriter();
			writer.write(message);
			writer.flush();
			writer.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
