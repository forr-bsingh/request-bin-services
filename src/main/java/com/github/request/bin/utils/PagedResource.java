package com.github.request.bin.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PagedResource<T> extends PageImpl<T> {

    private static final long serialVersionUID = 2411121857389336452L;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public PagedResource(@JsonProperty("content") List<T> content,
                         @JsonProperty("number") int number,
                         @JsonProperty("size") int size,
                         @JsonProperty("totalElements") long totalElements,
                         @JsonProperty("pageable") JsonNode pageable,
                         @JsonProperty("last") boolean last,
                         @JsonProperty("totalPages") int totalPages,
                         @JsonProperty("sort") JsonNode sort,
                         @JsonProperty("first") boolean first,
                         @JsonProperty("numberOfElements") int numberOfElements) {

        super(content, PageRequest.of(number, size), totalElements);
    }

    public PagedResource(List<T> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public PagedResource(List<T> content) {
        super(content);
    }

    public PagedResource() {
        super(new ArrayList<>());
    }
}
