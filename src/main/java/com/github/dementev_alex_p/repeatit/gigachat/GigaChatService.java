package com.github.dementev_alex_p.repeatit.gigachat;

import chat.giga.client.GigaChatClient;
import chat.giga.client.auth.AuthClient;
import chat.giga.client.auth.AuthClientBuilder;
import chat.giga.model.ModelName;
import chat.giga.model.Scope;
import chat.giga.model.completion.ChatMessageRole;
import chat.giga.model.completion.CompletionRequest;
import chat.giga.model.completion.CompletionResponse;
import chat.giga.model.completion.ChatMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GigaChatService {

    private final GigaChatClient client;

    public GigaChatService(@Value("${gigachat.auth-key}") String authKey) {
        this.client = GigaChatClient.builder()
                .authClient(AuthClient.builder()
                        .withOAuth(AuthClientBuilder.OAuthBuilder
                                .builder()
                                .scope(Scope.GIGACHAT_API_PERS)
                                .authKey(authKey)
                                .build())
                        .build())
                .verifySslCerts(false)
                .build();
    }

    /**
     * Отправляет запрос к GigaChat и возвращает ответ.
     *
     * @param userPrompt Текст запроса от пользователя
     * @return Ответ, сгенерированный нейросетью
     */
    public String send(String userPrompt) {
        final CompletionRequest request = CompletionRequest.builder()
                .model(ModelName.GIGA_CHAT_2)
                .message(ChatMessage.builder()
                        .content(userPrompt)
                        .role(ChatMessageRole.USER)
                        .build())
                .build();

        final CompletionResponse response = client.completions(request);
        if (response != null && response.choices() != null && !response.choices().isEmpty()) {
            // Возвращаем текст ответа от нейросети
            return response.choices().get(0).message().content();
        }
        return "Извините, я не смог сгенерировать ответ.";
    }
}