package com.tistory.devyongsik.crescent.admin.service;

import com.tistory.devyongsik.crescent.admin.entity.MorphToken;
import com.tistory.devyongsik.crescent.collection.entity.CrescentCollection;
import com.tistory.devyongsik.crescent.config.CrescentCollectionHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class MorphServiceImpl implements MorphService {

	private final CrescentCollectionHandler collectionHandler;

	public MorphServiceImpl(CrescentCollectionHandler collectionHandler) {
		this.collectionHandler = collectionHandler;
	}

	@Override
	public List<MorphToken> getTokens(String keyword, boolean isIndexingMode, String collectionName) throws IOException {
		StringReader reader = new StringReader(keyword);
		
		CrescentCollection crescentCollection = collectionHandler.getCrescentCollections().getCrescentCollection(collectionName);
		Analyzer analyzer;
		
		if(isIndexingMode) {
			analyzer = crescentCollection.getIndexingModeAnalyzer();
		} else {
			analyzer = crescentCollection.getSearchModeAnalyzer();
		}
		
		TokenStream stream = analyzer.tokenStream("dummy", reader);
		stream.reset();
		
		CharTermAttribute charTermAtt = stream.getAttribute(CharTermAttribute.class);
		OffsetAttribute offSetAtt = stream.getAttribute(OffsetAttribute.class);
		TypeAttribute typeAtt = stream.getAttribute(TypeAttribute.class);

		List<MorphToken> resultTokenList = new ArrayList<MorphToken>();
		
		while(stream.incrementToken()) {
			Token t = new Token(charTermAtt.toString(), offSetAtt.startOffset(), offSetAtt.endOffset(), typeAtt.type());
			
			log.debug("termAtt : {}, startOffset : {}, endOffset : {}, typeAtt : {}",
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
