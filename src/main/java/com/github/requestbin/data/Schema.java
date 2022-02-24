package com.github.requestbin.data;

import com.github.requestbin.YamlPropertyReader;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Getter
@NoArgsConstructor
@ToString
@Configuration
@ConfigurationProperties(prefix = "schema")
@PropertySource(value = "classpath:schema-attributes.yaml", factory = YamlPropertyReader.class)
public class Schema implements Serializable {
    private static final long serialVersionUID = 7091622742916502825L;
    private List<Attribute> attributes = new LinkedList<>();
}
