package org.searchAI;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.searchAI.Services.LlmClient;
import org.searchAI.Services.PriceAgent;
import org.searchAI.Services.TelegramNotifier;
import org.searchAI.entity.LLmResponse;
import org.searchAI.entity.Product;
import org.searchAI.entity.ProductQuery;
import org.searchAI.entity.TelegramBot;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Main {

    public static void main(String[] args) throws Exception {

        Scanner scanner = new Scanner(System.in);

        System.out.println("===========================");
        System.out.println(" 🛍️  SearchAI - Monitor de Produtos");
        System.out.println("===========================");

        System.out.print("Descreva o produto desejado: ");

        String natural = scanner.nextLine();

        String llmRaw = LlmClient.gerarQuery(natural);

        System.out.println("LLM JSON: " + llmRaw);

        ObjectMapper mapper = new ObjectMapper();

        // parse JSON externo da ollama
        JsonNode root = mapper.readTree(llmRaw);

        // somente o campo `response`
        String innerJson = root.get("response").asText();

        // converte para ProductQuery
        ProductQuery query = mapper.readValue(innerJson, ProductQuery.class);

        System.out.println("\nQuery interpretada pela IA:");
        System.out.println(query);

        System.out.println("\n🔎 Iniciando agente de busca no Mercado Livre...");
        PriceAgent agent = new PriceAgent(query);

        TelegramNotifier telegram = new TelegramNotifier(new TelegramBot());

        while (true) {
            List<Product> produtos = agent.executar();
            if (!produtos.isEmpty()) {
                telegram.notificar(produtos);
                for (Product prod: produtos) System.out.println(prod.getTitle() + " - R$ " + prod.getPrice() + " - " + prod.getLink());
            }
            System.out.println("⏳ Aguardando próxima verificação...");
            Thread.sleep(60_000);
        }
    }
}
