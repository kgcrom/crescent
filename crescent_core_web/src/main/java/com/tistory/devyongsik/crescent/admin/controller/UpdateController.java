package com.tistory.devyongsik.crescent.admin.controller;

import com.tistory.devyongsik.crescent.collection.entity.CrescentCollection;
import com.tistory.devyongsik.crescent.config.CrescentCollectionHandler;
import com.tistory.devyongsik.crescent.data.handler.Handler;
import com.tistory.devyongsik.crescent.data.handler.JsonDataHandler;
import com.tistory.devyongsik.crescent.index.entity.IndexingRequestForm;
import com.tistory.devyongsik.crescent.index.indexer.CrescentIndexerExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

@Slf4j
@Controller
public class UpdateController {
	
	@Autowired
	@Qualifier("crescentCollectionHandler")
	private CrescentCollectionHandler collectionHandler;
	
	@Autowired
	@Qualifier("crescentIndexerExecutor")
	private CrescentIndexerExecutor crescentIndexerExecutor;
	
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
				//logger.info(tmp);
			}
			
			reader.close();
			IndexingRequestForm indexingRequestForm = handler.handledData(text.toString());
			
//			CrescentCollectionHandler collectionHandler 
//				= SpringApplicationContext.getBean("crescentCollectionHandler", CrescentCollectionHandler.class);
			
			CrescentCollection collection = collectionHandler.getCrescentCollections().getCrescentCollection(collectionName);
			//CrescentIndexerExecutor executor = new CrescentIndexerExecutor(collection, indexingRequestForm);
			
			String message = crescentIndexerExecutor.indexing(collection, indexingRequestForm);
			
			Writer writer = null;
			
			response.setContentType("text/html;  charset=UTF-8");
			writer = response.getWriter();
			writer.write(message);
			writer.flush();
			writer.close();
			
		} catch (IOException e) {
			log.error("error : ", e);
		}
	}
}
