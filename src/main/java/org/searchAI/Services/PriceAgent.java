    package org.searchAI.Services;

    import org.searchAI.entity.Product;
    import org.searchAI.entity.ProductQuery;

    import java.net.URLEncoder;
    import java.nio.charset.StandardCharsets;
    import java.text.Normalizer;
    import java.util.Collections;
    import java.util.HashSet;
    import java.util.List;
    import java.util.Set;

    public class PriceAgent {

        private final ProductSearch scraper;
        private final ProductQuery query;
        private final ProductFilter filter;

        public PriceAgent(ProductQuery query) {
            this.query = query;
            this.scraper = new ProductSearch();
            this.filter = new ProductFilter();
        }

        public List<Product> executar() {

            String queryBusca = buildSearchText();

            Set<Product> result = new HashSet<>();
            if (queryBusca.isBlank()) {
                System.out.println("Nenhum termo informado — impossível buscar.");
                return Collections.emptyList();
            }

            // URL principal (slug com hífens)
            String slug = buildSlug(queryBusca);
            String listaUrl = "https://lista.mercadolivre.com.br/" + slug;

            // fallback com search?q=
            String searchUrl = "https://www.mercadolivre.com.br/search?q=" +
                    URLEncoder.encode(queryBusca, StandardCharsets.UTF_8);

            System.out.println("Tentando URL SEO: " + listaUrl);

            Set<Product> products = scraper.procurar(listaUrl);

            // fallback automático
            if (products == null || products.isEmpty()) {
                System.out.println("Nenhum resultado na URL SEO — tentando busca normal...");
                System.out.println("Fallback: " + searchUrl);
                products = scraper.procurar(searchUrl);
            }

            if (products == null || products.isEmpty()) {
                System.out.println("Nenhum produto encontrado em nenhuma URL.");
                return Collections.emptyList();
            }

            for (Product prod : products) {
                if (filter.matches(prod, query)) {
                    //System.out.println(prod.getTitle() + " - R$ " + prod.getPrice() + " - " + prod.getLink());
                    result.add(prod);
                }
            }

            System.out.println("Aprovados pelo filtro simples: " + result.size());

            ToTAgent totAgent = new ToTAgent();
            List<Product> finais = totAgent.pensar(result, filter, query);
            System.out.println("Após ToT: " + finais.size());

            return finais;

        }

        private Set<Product> sugerir() {
            return new HashSet<>();
        }

        private void notificar() {
            // enviar alerta telegram / email aqui depois :)

        }


        private String buildSearchText() {

            StringBuilder sb = new StringBuilder();

            if (query.getTitle() != null) sb.append(query.getTitle()).append(" ");
            if (query.getBrand() != null) sb.append(query.getBrand()).append(" ");
            if (query.getModel() != null) sb.append(query.getModel()).append(" ");
            if (query.getGenre() != null) sb.append(query.getGenre()).append(" ");
            if (query.getSize() != null) sb.append(query.getSize()).append(" ");

            return sb.toString().trim();
        }

        private String buildSlug(String text) {

            if (text == null) return "";

            String normalized = Normalizer.normalize(text, Normalizer.Form.NFD)
                    .replaceAll("[^\\p{ASCII}]", "")   // remove acentos
                    .toLowerCase()
                    .trim();

            // troca espaços múltiplos por hífen
            return normalized.replaceAll("\\s+", "-");
        }
    }
