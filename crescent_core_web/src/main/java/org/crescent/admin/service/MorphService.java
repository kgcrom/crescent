package org.crescent.admin.service;

import org.crescent.admin.entity.MorphToken;
import org.crescent.collection.entity.Collection;
import org.crescent.config.CollectionHandler;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class MorphService {

	private static final Logger logger = LoggerFactory.getLogger(MorphService.class);
	private final CollectionHandler collectionHandler;

	public MorphService(CollectionHandler collectionHandler) {
		this.collectionHandler = collectionHandler;
	}

	public List<MorphToken> getTokens(String keyword, boolean isIndexingMode, String collectionName) throws IOException {
		StringReader reader = new StringReader(keyword);
		
		Collection collection = collectionHandler.getCollection(collectionName);
		Analyzer analyzer;
		
		if(isIndexingMode) {
			analyzer = collection.getIndexingModeAnalyzer();
		} else {
			analyzer = collection.getSearchModeAnalyzer();
		}
		
		TokenStream stream = analyzer.tokenStream("dummy", reader);
		stream.reset();
		
		CharTermAttribute charTermAtt = stream.getAttribute(CharTermAttribute.class);
		OffsetAttribute offSetAtt = stream.getAttribute(OffsetAttribute.class);
		TypeAttribute typeAtt = stream.getAttribute(TypeAttribute.class);

		List<MorphToken> resultTokenList = new ArrayList<MorphToken>();
		
		while(stream.incrementToken()) {
			Token t = new Token(charTermAtt.toString(), offSetAtt.startOffset(), offSetAtt.endOffset());
			
			logger.debug("termAtt : {}, startOffset : {}, endOffset : {}, typeAtt : {}",
					new Object[] {charTermAtt.toString(),offSetAtt.startOffset(), offSetAtt.endOffset(), typeAtt.type()});
			
			MorphToken mt = new MorphToken();
			mt.setTerm(t.toString());
			mt.setType(t.type());
			mt.setStartOffset(t.startOffset());
			mt.setEndOffset(t.endOffset());
			
			resultTokenList.add(mt);
		}
	
		stream.close();

		return resultTokenList;
	}

}
