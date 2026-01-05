package org.searchAI.Services;

import org.searchAI.entity.Product;
import org.searchAI.entity.ProductQuery;

import java.math.BigDecimal;
import java.text.Normalizer;

public class ProductFilter {

    private static final int DEFAULT_MAX_DISTANCE = 4;

    public boolean matches(Product product, ProductQuery query) {

        if (product == null) return false;

        String title = normalize(product.getTitle());

        boolean priceOK = true;

        if (query.getPrice() != null && product.getPrice() != null) {
            priceOK = product.getPrice().compareTo(query.getPrice()) <= 0;
        }

        // ---- TEXTO FUZZY ----
        boolean brandOK = fuzzyField(title, query.getBrand());
        boolean modelOK = fuzzyField(title, query.getModel());
        boolean nameOK  = fuzzyField(title, query.getTitle());
        boolean genreOK = fuzzyField(title, query.getGenre());
        boolean sizeOK  = fuzzyField(title, query.getSize());
        boolean ratingOK = product.getRating() == null ||
                product.getRating().compareTo(BigDecimal.valueOf(4.3)) >= 0;


        return priceOK && ratingOK && (brandOK || modelOK || nameOK || sizeOK || genreOK);
    }


    // ------------ NORMALIZA TEXTO ------------
    private String normalize(String s) {
        if (s == null) return "";

        return Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .toLowerCase()
                .trim();
    }

    // ------------ APLICA FUZZY SOMENTE SE HOUVER VALOR ------------
    private boolean fuzzyField(String text, String query) {

        if (query == null || query.isBlank())
            return true;

        return fuzzyContains(text, query, DEFAULT_MAX_DISTANCE);
    }


    // ------------ FUZZY MATCH ------------
    private boolean fuzzyContains(String text, String query, int maxDistance) {

        text = normalize(text);
        query = normalize(query);

        if (query.isBlank()) return true;

        if (text.contains(query))
            return true;

        String[] tokens = text.split("\\s+");

        for (String token : tokens) {
            if (levenshtein(token, query) <= maxDistance)
                return true;
        }

        if (query.length() <= text.length()) {
            for (int i = 0; i <= text.length() - query.length(); i++) {
                String sub = text.substring(i, i + query.length());
                if (levenshtein(sub, query) <= maxDistance)
                    return true;
            }
        }

        return false;
    }


    // ------------ LEVENSHTEIN ------------
    private int levenshtein(String a, String b) {

        int[][] dp = new int[a.length() + 1][b.length() + 1];

        for (int i = 0; i <= a.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= b.length(); j++) dp[0][j] = j;

        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {

                int cost = (a.charAt(i - 1) == b.charAt(j - 1)) ? 0 : 1;

                dp[i][j] = Math.min(
                        Math.min(
                                dp[i - 1][j] + 1,
                                dp[i][j - 1] + 1
                        ),
                        dp[i - 1][j - 1] + cost
                );
            }
        }

        return dp[a.length()][b.length()];
    }
}
