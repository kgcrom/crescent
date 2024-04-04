package org.crescent.search.entity;

import java.security.InvalidParameterException;

import org.crescent.search.entity.SearchRequest;
import org.crescent.search.entity.SearchRequestValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.crescent.config.CollectionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles(value = "local")
@SpringBootTest(classes = {
        CollectionHandler.class
})
class SearchRequestValidatorTest {

    @Autowired
    private CollectionHandler collectionHandler;
    private SearchRequestValidator searchRequestValidator;

    @BeforeEach
    void setUp() {
        searchRequestValidator = new SearchRequestValidator();
    }

    @Test
    void test_collectionIsNull() {
        SearchRequest request = SearchRequest.builder()
                .collectionName("sam").build();

        Assertions.assertThrows(InvalidParameterException.class, () -> {
            searchRequestValidator.valid(request, collectionHandler.getCollection(request.getCollectionName()));
        });
    }

    @Test
    void test_valid_doesntExistField() {
        SearchRequest request = SearchRequest.builder()
                .collectionName("sample")
                .sort("tit desc")
                .build();

        Assertions.assertThrows(InvalidParameterException.class, () -> {
            searchRequestValidator.valid(request, collectionHandler.getCollection(request.getCollectionName()));
        });
    }

    @Test
    void test_valid_doesntAnalyzeField() {
        SearchRequest request = SearchRequest.builder()
                .collectionName("sample")
                .sort("title desc")
                .build();

        Assertions.assertThrows(InvalidParameterException.class, () -> {
            searchRequestValidator.valid(request, collectionHandler.getCollection(request.getCollectionName()));
        });
    }

    @Test
    void test_valid_doesntExistSearchField() {
        SearchRequest request = SearchRequest.builder()
                .collectionName("sample")
                .searchField("tit")
                .build();

        Assertions.assertThrows(InvalidParameterException.class, () -> {
            searchRequestValidator.valid(request, collectionHandler.getCollection(request.getCollectionName()));
        });
    }

    @Test
    void test_valid_pageNum() {
        SearchRequest request = SearchRequest.builder()
                .collectionName("sample")
                .pageNum("abc")
                .build();

        Assertions.assertThrows(InvalidParameterException.class, () -> {
            searchRequestValidator.valid(request, collectionHandler.getCollection(request.getCollectionName()));
        });
    }

}
