package me.tippie.tippieutils.storage;

import com.google.gson.*;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
@Builder(toBuilder = true) @EqualsAndHashCode @ToString
public final class StorageCredentials {
    private final @NotNull String address;

    @Builder.Default
    private @NotNull String database = "";
    private final @NotNull String username;
    private final @NotNull String password;

    @Builder.Default
    private int maxPoolSize = 10;

    @Builder.Default
    private int minIdleConnections= 10;

    @Builder.Default
    private int maxLifetime = 1800000;

    @Builder.Default
    private int keepAliveTime = 0;

    @Builder.Default
    private int connectionTimeout = 5000;

    @Singular
    private final @NotNull Map<String, String> properties = new HashMap<>();

    public static class StorageCredentialsBuilder {
        private final Map<String, String> properties = new HashMap<>();

        public StorageCredentialsBuilder propertyIfAbsent(String key, String value) {
            properties.putIfAbsent(key, value);
            return this;
        }
    }
}
