# 🛍️ SearchAI — Monitor inteligente de produtos

SearchAI é uma aplicação que busca produtos em e-commerces e monitora preços de forma automática.  
O sistema utiliza **LLMs locais (Ollama)** para interpretar consultas em linguagem natural e cria **agentes inteligentes** que realizam:

- 🧠 interpretação do pedido do usuário (NLP)
- 🔍 web scraping em lojas online
- 🪜 Tree-of-Thoughts para seleção de melhores opções
- 🏷️ filtragem por preço, tamanho, gênero, marca e modelo
- 📢 envio de notificações no Telegram quando boas ofertas aparecem

---

## 🚀 Como funciona

1. o usuário descreve o produto desejado em linguagem natural
   > ex: *"quero uma calça slim masculina tamanho 42 por 300 reais"*

2. a LLM converte o texto para JSON estruturado (`ProductQuery`)
3. o agente de busca acessa o e-commerce e coleta os produtos
4. filtros removem itens inconsistentes
5. um **agente ToT** compara opções e escolhe as melhores
6. os resultados são enviados via Telegram automaticamente

---

## 🧠 Tecnologias utilizadas

- Java 17+
- Ollama + LLaMA 3 (LLM local)
- Jsoup (web scraping)
- Telegram Bot API
- Tree-of-Thoughts (lógica manual)
- Mercado Livre (atualmente principal fonte)

---

## 📌 Funcionalidades atuais

✔️ interpretação de texto natural  
✔️ parsing automático para JSON estruturado  
✔️ busca automática no Mercado Livre  
✔️ filtros por título, gênero, preço, tamanho etc.  
✔️ agente ToT para ranking das melhores opções  
✔️ notificações no Telegram  
✔️ scraping tolerante com fallback de URLs  
✔️ execução contínua com monitoramento periódico

---


