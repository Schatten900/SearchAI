package org.searchAI.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;


@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Getter
@Setter
public class ProductQuery {

    private String title;
    private String model;
    private String brand;
    private String size;
    private String genre;
    private BigDecimal price;

    public ProductQuery() {}

}
