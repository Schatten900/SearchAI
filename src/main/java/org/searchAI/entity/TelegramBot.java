package org.searchAI.entity;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class TelegramBot{

    private static final Dotenv dotenv = Dotenv.load();

    private static final String BOT_TOKEN = dotenv.get("TELEGRAM_TOKEN");
    private static final String CHAT_ID = dotenv.get("TELEGRAM_CHAT_ID");

    public static void enviarMensagem(String message) {
        try {

            if (BOT_TOKEN == null || CHAT_ID == null) {
                throw new RuntimeException("❌ TOKEN ou CHAT_ID não definidos");
            }

            String urlString = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage";
            URL url = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            String payload = "chat_id=" + CHAT_ID + "&text=" + message;

            OutputStream os = conn.getOutputStream();
            os.write(payload.getBytes());
            os.flush();
            os.close();

            conn.getInputStream();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
