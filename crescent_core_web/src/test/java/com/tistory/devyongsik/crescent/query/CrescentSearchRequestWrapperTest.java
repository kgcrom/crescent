package com.tistory.devyongsik.crescent.query;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.tistory.devyongsik.crescent.collection.entity.CollectionField;
import com.tistory.devyongsik.crescent.search.entity.SearchRequest;
import com.tistory.devyongsik.utils.CrescentTestCaseUtil;
import java.util.List;
import javax.annotation.PostConstruct;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class CrescentSearchRequestWrapperTest extends CrescentTestCaseUtil {

	@PostConstruct
	public void init() throws Exception {
		super.init();
	}

	private static SearchRequest searchRequest;

	@BeforeEach
	public void initParamMap() {
		searchRequest = new SearchRequest();
		searchRequest.setCollectionName("sample");
	}

	@Test
	public void getStartOffset() {
		CrescentSearchRequestWrapper searchRequestWrapper = new CrescentSearchRequestWrapper(searchRequest, this.collectionHandler);
		assertEquals(0, searchRequestWrapper.getStartOffSet());

		searchRequest.setPageNum("10");
		assertEquals(180, searchRequestWrapper.getStartOffSet());
	}

	@Test
	public void getHitsForPage() {
		CrescentSearchRequestWrapper searchRequestWrapper = new CrescentSearchRequestWrapper(searchRequest, this.collectionHandler);
		assertEquals(20, searchRequestWrapper.getHitsForPage());

		searchRequest.setPageSize("30");
		assertEquals(30, searchRequestWrapper.getHitsForPage());
	}

	@Test
	public void getSearchFieldNames() {
		CrescentSearchRequestWrapper searchRequestWrapper = new CrescentSearchRequestWrapper(searchRequest, this.collectionHandler);
		List<CollectionField> searchFields = searchRequestWrapper.getTargetSearchFields();

		String result = "[";
		for(CollectionField f : searchFields) {
			result += f.getName() + ", ";
		}
		result += "]";
		assertEquals("[title, dscr, ]", result);
	}

	@Test
	public void getSort() {
		searchRequest.setSort("title_sort desc, board_id_sort asc");
		CrescentSearchRequestWrapper searchRequestWrapper = new CrescentSearchRequestWrapper(searchRequest, this.collectionHandler);

		Sort sort = searchRequestWrapper.getSort();
		assertEquals("<string: \"title_sort\">!,<long: \"board_id_sort\">", sort.toString());

		searchRequest.setSort("score desc, title_sort desc");
		sort = searchRequestWrapper.getSort();

		assertEquals("<score>,<string: \"title_sort\">!", sort.toString());
	}

	@Test
	public void getKeyword() {
		searchRequest.setKeyword("청바지");
		CrescentSearchRequestWrapper searchRequestWrapper = new CrescentSearchRequestWrapper(searchRequest, this.collectionHandler);

		assertEquals("청바지", searchRequestWrapper.getKeyword());
	}

	@Disabled
	@Test
	public void getFilter() throws Exception {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setCollectionName("sample");
		searchRequest.setFilter("title:\"파이썬 프로그래밍 공부\" +dscr:\"자바 병렬 프로그래밍\"");

		CrescentSearchRequestWrapper csrw
			= new CrescentSearchRequestWrapper(searchRequest, this.collectionHandler);

		Filter filter = csrw.getFilter();

		System.out.println(filter);

		assertEquals("QueryWrapperFilter(title:파이썬^2.0 title:파이^2.0 title:프로그래밍^2.0 title:공부^2.0 +dscr:자바 +dscr:병렬 +dscr:프로그래밍)", filter.toString());
	}

	@Disabled
	@Test
	public void getQuery() throws Exception {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setCollectionName("sample");
		searchRequest.setCustomQuery("title:\"파이썬 프로그래밍 공부\" +dscr:\"자바 병렬 프로그래밍\"");

		CrescentSearchRequestWrapper csrw
			= new CrescentSearchRequestWrapper(searchRequest, this.collectionHandler);

		Query query = csrw.getQuery();

		System.out.println(query);

		assertEquals("title:파이썬^2.0 title:파이^2.0 title:프로그래밍^2.0 title:공부^2.0 +dscr:자바 +dscr:병렬 +dscr:프로그래밍", query.toString());
	}
}
