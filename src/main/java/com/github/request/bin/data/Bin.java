package com.github.request.bin.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.Map;

@ToString
@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Bin implements Serializable {
    private static final long serialVersionUID = -814513216559017209L;
    private final String name;
    private final String url;
    private Map<String, Object> schema;
    private transient Page<Ops> ops = Page.empty();

    public Bin(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public Bin(String name, String url, Map<String, Object> schema) {
        this.name = name;
        this.url = url;
        this.schema = schema;
    }

    public Bin(String name, String url, Page<Ops> ops) {
        this.name = name;
        this.url = url;
        this.ops = ops;
    }

    public void setSchema(Map<String, Object> schema) {
        this.schema = schema;
    }

    public void setOps(Page<Ops> ops) {
        this.ops = ops;
    }
}
