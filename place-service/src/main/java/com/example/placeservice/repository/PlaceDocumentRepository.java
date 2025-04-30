package com.example.placeservice.repository;

import com.example.placeservice.dto.PlaceDocument;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PlaceDocumentRepository {

    private final List<PlaceDocument> placeDocuments = new ArrayList<>();

    public List<PlaceDocument> findAll() {
        return placeDocuments;
    }

    public void saveAll(List<PlaceDocument> documents) {
        placeDocuments.clear();
        placeDocuments.addAll(documents);
    }
}
