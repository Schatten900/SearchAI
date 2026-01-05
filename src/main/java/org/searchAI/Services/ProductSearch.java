package org.searchAI.Services;

import lombok.Getter;
import lombok.Setter;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.searchAI.entity.Product;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Getter
@Setter
public class ProductSearch {

    public Set<Product> procurar(String url) {

        Set<Product> products = new TreeSet<>(
                Comparator.comparing(Product::getPrice)
                        .thenComparing(
                                Comparator.comparing(
                                        Product::getRating,
                                        Comparator.nullsLast(Comparator.reverseOrder())
                                )
                        )
                        .thenComparing(Product::getTitle)
        );

        try {
            Document document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120 Safari/537.36")
                    .referrer("https://www.google.com")
                    .timeout(15000)
                    .followRedirects(true)
                    .get();

            // bloco principal de resultados
            Elements items = document.select("li.ui-search-layout__item");

            if (items.isEmpty())
                items = document.select(".ui-search-result__content-wrapper");

            for (Element item : items) {

                String title = pegarTitulo(item);

                BigDecimal price = pegarPreco(item);

                BigDecimal rating = pegarAvaliacao(item);

                String link = item.select("a").attr("href");

                if (title == null || price == null || link == null)
                    continue;

                Product product = new Product(title, price,rating);
                product.setLink(link);

                products.add(product);
            }

        } catch (HttpStatusException e) {
            System.out.println("⚠️ Erro HTTP " + e.getStatusCode() + " na URL: " + url);
            return new HashSet<>();
        } catch (Exception e) {
            System.out.println("⚠️ Erro ao processar resultados da URL: " + url);
            e.printStackTrace();
        }

        return products;
    }

    private BigDecimal pegarPreco(Element item){
        Element fractionEl = item.selectFirst(".andes-money-amount__fraction");
        if (fractionEl == null) return null;

        String priceInteger = fractionEl.text().replace(".", "").trim();

        String cents = "00";
        Element centsEl = item.selectFirst(".andes-money-amount__cents");
        if (centsEl != null) cents = centsEl.text();

        BigDecimal price = new BigDecimal(priceInteger + "." + cents);
        return price;
    }

    private BigDecimal pegarAvaliacao(Element item){
        Element ratingEl = item.selectFirst(".poly-phrase-label");

        BigDecimal rating = null;
        if (ratingEl == null) return null;

        String ratingText = ratingEl.text().replace(",", ".").trim();

        if (ratingText.matches("\\d+(\\.\\d+)?")) {
            rating = new BigDecimal(ratingText);
        }

        return rating;
    }

    private String pegarTitulo(Element item){

        // layout atual do Mercado Livre (o seu)
        String title = item.select("a.poly-component__title").text();

        if (title.isBlank())
            title = item.select("h3.poly-component__title-wrapper a").text();

        // layouts antigos
        if (title.isBlank())
            title = item.select("h2.ui-search-item__title").text();

        if (title.isBlank())
            title = item.select("a.ui-search-item__group__element").text();

        if (title.isBlank())
            title = item.select("a.ui-search-link__title-card").text();

        // atributo title como fallback
        if (title.isBlank())
            title = item.select("a[title]").attr("title");

        return title.trim();
    }


}
