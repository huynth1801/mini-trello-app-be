package com.huydev.skipli_be.constant;

public interface SecurityConstants {
    String[] IGNORING_API_PATHS = {
            "/auth/**"
    };

    String[] USERS_API_PATHS = {
            "/boards/**"
    };
}
