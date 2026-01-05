package org.searchAI.Services;

import org.searchAI.entity.Product;
import org.searchAI.entity.TelegramBot;

import java.util.List;
import java.util.Set;

public class TelegramNotifier {

    private final TelegramBot bot;

    public TelegramNotifier(TelegramBot bot){
        this.bot = bot;
    }

    public void notificar(List<Product> produtosEncontrados){

        for (Product prod : produtosEncontrados) {

            String message = """
                    🔔 Produto encontrado!
                    
                    📦 %s
                    💵 R$ %s
                    🔗 %s
                    """.formatted(
                    prod.getTitle(),
                    prod.getPrice(),
                    prod.getLink()
            );

            TelegramBot.enviarMensagem(message);
        }
    }

}
