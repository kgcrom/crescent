package org.crescent.admin.controller;

import org.crescent.collection.entity.Collection;
import org.crescent.config.CollectionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CollectionCotroller {

    private final CollectionHandler collectionHandler;

    public CollectionCotroller(CollectionHandler collectionHandler) {
        this.collectionHandler = collectionHandler;
    }

    @GetMapping("/collections")
    public List<Collection> showCollectionList() {
        return collectionHandler.getCollections();
    }

    // TODO: Create, Update, Delete collection API
}
