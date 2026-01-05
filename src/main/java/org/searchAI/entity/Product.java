package org.searchAI.entity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Product {
    public String title;
    public BigDecimal price;
    public String link;
    private BigDecimal rating;


    public Product(){}

    public Product(String title, BigDecimal price, BigDecimal rating){
        this.title=title;
        this.price=price;
        this.rating=rating;
    }

    @Override
    public String toString(){
        return getTitle() + " - R$ " + getPrice();
    }
}
