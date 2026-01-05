package org.searchAI.Services;

import org.searchAI.entity.Product;
import org.searchAI.entity.ProductQuery;

import java.math.BigDecimal;
import java.util.*;

public class ToTAgent {

    public List<Product> pensar(Set<Product> products, ProductFilter productFilter, ProductQuery query) {

        // 1) gerar candidatos
        List<Product> step1 = products.stream()
                .filter(p -> productFilter.matches(p, query))
                .toList();

        // 2) ordenar por prioridade
        List<Product> step2 = new ArrayList<>(step1);
        step2.sort(
                Comparator.comparing(
                                Product::getRating,
                                Comparator.nullsLast(Comparator.naturalOrder())
                        ).reversed()
                        .thenComparing(Product::getPrice, Comparator.nullsLast(Comparator.naturalOrder()))
        );

        // 3) self-reflection
        List<Product> step3 = step2.stream()
                .filter(p -> checagem(p, query))
                .toList();

        // 4) limitar
        List<Product> step4 = step3.stream()
                .limit(5)
                .toList();
        return step4;
    }

    private boolean checagem( Product product, ProductQuery query) {

        System.out.println("\n[Self-Reflection] analisando: " + product.getTitle());

        String title = Optional.ofNullable(product.getTitle()).orElse("").toLowerCase();
        String queryTitle = Optional.ofNullable(query.getTitle()).orElse("").toLowerCase();

        boolean specOk = title.contains(queryTitle);

        boolean ratingOk = product.getRating() != null &&
                product.getRating().compareTo(BigDecimal.valueOf(4.3)) >= 0;

        boolean priceOk = true;

        if (product.getPrice() != null && query.getPrice() != null) {
            priceOk = product.getPrice().compareTo(query.getPrice()) <= 0;
        }

        System.out.println("specOk  = " + specOk);
        System.out.println("ratingOk= " + ratingOk);
        System.out.println("priceOk = " + priceOk);

        return specOk && ratingOk && priceOk;
    }

}
