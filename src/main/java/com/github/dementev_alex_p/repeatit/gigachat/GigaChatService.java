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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class GigaChatService {
    private static final String SYSTEM_PROMPT = """
            Ты — ассистент для генерации учебных карточек для интервального повторения (алгоритм SM-2).
            Твоя задача — создать 20-50 карточек на русском языке на указанную тему. Лицевая сторона карточки - это термин или вопрос. Обратная сторона - это определение или ответ размером до 500 символов.
            Формат ответа - простой текст (без оформления) в JSON формате [{"front":"вопрос", "back":"ответ"}].
            Выводи ТОЛЬКО JSON-массив, без каких-либо комментариев, без markdown, без точек с запятыми после элементов. JSON будет использоваться для парсинга, поэтому он должен быть валидный, в вопросах и ответах избегай двойных кавычек.
            """;

    private static final String USER_PROMPT = """
            Тема для карточек: %s
            """;
    private static final String ERROR_MESSAGE = """
            Твой предыдущий ответ — невалидный JSON. Ошибка при парсинге: %s
            Исправь ошибку. В ответе на это сообщение должен быть валидный JSON в формате [{"front":"вопрос", "back":"ответ"}] без комментариев, без дополнительных слов, без извинений, без выделений, без markdown.
            """;

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
     * @return Ответ, сгенерированный нейросетью
     */
    public String send(final String topic, final String previousResponse, final String error) {
        final List<ChatMessage> messages = new ArrayList<>();
        messages.add(ChatMessage
                .builder()
                .role(ChatMessageRole.SYSTEM)
                .content(SYSTEM_PROMPT)
                .build()
        );
        messages.add(ChatMessage
                .builder()
                .role(ChatMessageRole.USER)
                .content(String.format(USER_PROMPT, topic))
                .build()
        );

        if (previousResponse != null) {
            messages.add(ChatMessage
                    .builder()
                    .role(ChatMessageRole.ASSISTANT)
                    .content(previousResponse)
                    .build()
            );
        }

        if (error != null) {
            messages.add(ChatMessage
                    .builder()
                    .role(ChatMessageRole.USER)
                    .content(String.format(ERROR_MESSAGE, error))
                    .build()
            );
        }

        final CompletionResponse response = client.completions(CompletionRequest.builder()
                .model(ModelName.GIGA_CHAT_2)
                .temperature(0.1F)
                .messages(messages)
                .build()
        );
        if (response != null && response.choices() != null && !response.choices().isEmpty()) {
            return response.choices().get(0).message().content();
        }
        throw new RuntimeException(String.format("Не удалось сгенерировать ответ для темы %s, ответ %s", topic, response));
    }
}