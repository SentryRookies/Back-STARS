package com.example.placeservice.controller;

import com.example.placeservice.dto.PlaceDocument;
import com.example.placeservice.service.PlaceDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
public class PlaceSearchController {

    private final PlaceDocumentService placeSearchService;

    @GetMapping("/{keyword}")
    public List<PlaceDocument> searchByName(@PathVariable String keyword) {
        return placeSearchService.searchPlacesByName(keyword);
    }

    @GetMapping("/address/{keyword}")
    public List<PlaceDocument> searchByAddress(@PathVariable String keyword) {
        return placeSearchService.searchPlacesByAddress(keyword);
    }
}
