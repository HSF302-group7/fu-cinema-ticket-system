package com.fu.cinema.util;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public final class PageUtils {

    public static final int DEFAULT_SIZE = 10;

    private PageUtils() {
    }

    public static String currentUrlWithoutPage() {
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .replaceQueryParam("page")
                .build()
                .toUriString();
    }
}
