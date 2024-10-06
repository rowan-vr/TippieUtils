package me.tippie.tippieutils.configuration;

import lombok.Getter;
import me.tippie.tippieutils.functions.TextUtil;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class Lang {
    private static final HashMap<Tuple<String, String>, String> ALL = new HashMap<>();
    private TextComponent prefix;
    private JavaPlugin plugin;
    private HashMap<String, YMLStorage> languages = new HashMap<>();
    private List<String> supportedLanguages;

    /**
     * This constructor creates a new Lang object that allows plugins to easily manage language files.
     * Internally, it uses {@link YMLStorage} to handle the language files.
     * @param plugin The JavaPlugin instance
     * @param options The options for the Lang object <br>
     *                The first option is the prefix for the messages <br>
     *                all remaining options are the supported languages
     */
    public Lang(@NotNull JavaPlugin plugin, String... options) {
        this.plugin = plugin;
        this.prefix = TextUtil.parse((options.length > 0) ? options[0] : "&7[&6" + plugin.getName() + "&7] ");
        this.supportedLanguages = new ArrayList<>();
        if(options.length > 1) supportedLanguages.addAll(Arrays.asList(options).subList(1, options.length));
        else supportedLanguages.add("en");
        supportedLanguages.forEach(lang
          -> languages.put(lang, new YMLStorage(plugin,  "messages_" + lang + ".yml", "messages")));
    }




    public static void set(@NotNull String key, @NotNull String msg, String... languages) {
        if(languages.length > 0) for(String lang : languages) ALL.put(new Tuple<>(key, lang), msg);
        else ALL.put(new Tuple<>(key, "en"), msg);
    }

    @NotNull
    public TextComponent get(String key, Object... lang) {
        String l = (lang.length > 0) ? lang[0] + "" : "en";
        String message = languages.get(l).getConfig().getString(key);
        if (message != null && lang.length > 1)
            message = MessageFormat.format(message, Arrays.copyOfRange(lang, 1, lang.length));
        return TextUtil.parse(message);
    }

    @Getter
    public static class Tuple<F, S> {
        public F first;
        public S second;

        public Tuple(F first, S second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Tuple<?, ?> tuple = (Tuple<?, ?>) obj;
            return first.equals(tuple.first) && second.equals(tuple.second);
        }
    }
}

