package org.searchAI.Services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class LlmClient {

    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";

    public static String gerarQuery(String userMessage) throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        ObjectNode root = mapper.createObjectNode();
        root.put("model", "llama3");
        root.put("stream", false);

        root.put(
                "system",
                "Você extrai informações de consultas de compra de produtos.\n\n" +
                        "Sua resposta deve ser SEMPRE exatamente este JSON:\n\n" +
                        "{\n" +
                        " \"title\": string|null,\n" +
                        " \"brand\": string|null,\n" +
                        " \"model\": string|null,\n" +
                        " \"size\": number|null,\n" +
                        " \"genre\": string|null,\n" +
                        " \"price\": number|null\n" +
                        "}\n\n" +
                        "REGRAS:\n" +
                        "- NÃO traduza nada\n" +
                        "- NÃO invente marca\n" +
                        "- NÃO invente modelo\n" +
                        "- NÃO devolva lista\n" +
                        "- NÃO devolva currency\n" +
                        "- NÃO devolva objetos aninhados\n" +
                        "- NÃO devolva texto fora do JSON\n" +
                        "- Se não souber, use null\n" +
                        "- preço deve ser somente número, sem R$, sem texto\n" +
                        "- trate title como a categoria do produto"
        );



        root.put("prompt", userMessage);

        String body = mapper.writeValueAsString(root);

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OLLAMA_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }


}
