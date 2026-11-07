package com.fu.cinema.desktop.ai;

import java.util.List;

public record ChatMessage(boolean fromUser, String text, List<ShowtimeAction> actions) {
    public static ChatMessage user(String text) {
        return new ChatMessage(true, text, List.of());
    }

    public static ChatMessage bot(String text, List<ShowtimeAction> actions) {
        return new ChatMessage(false, text, actions != null ? actions : List.of());
    }
}

