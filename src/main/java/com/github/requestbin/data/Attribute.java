package com.github.requestbin.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class Attribute implements Serializable {
    private static final long serialVersionUID = 2911494960840423820L;
    private String name;
    private boolean enabled;
    private String usage;
    private String examples;
    private String display;
    private String description;
}
