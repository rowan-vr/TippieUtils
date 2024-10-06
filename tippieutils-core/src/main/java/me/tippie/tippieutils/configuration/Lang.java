package me.tippie.tippieutils.configuration;

import me.tippie.tippieutils.functions.TextUtil;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class Lang {
    private TextComponent prefix;
    private JavaPlugin plugin;
    private HashMap<String, YMLStorage> languages = new HashMap<>();
    private String name;
    private List<String> supportedLanguages;


    public Lang(JavaPlugin plugin, String name, String... options) {
        this.prefix = TextUtil.parse((options.length > 0) ? options[0] : "&7[&6" + name + "&7] ");
        this.plugin = plugin;
        this.name = name;
        this.supportedLanguages = new ArrayList<>();
        if(options.length > 1) supportedLanguages.addAll(Arrays.asList(options).subList(1, options.length));
        else supportedLanguages.add("en");
        supportedLanguages.forEach(lang
          -> languages.put(lang, new YMLStorage(plugin, name + "_" + lang + ".yml")));
    }

    @Nullable
    public String get(String key, String... lang) {
        String l = (lang.length > 0) ? lang[0] : "en";
        return languages.get(l).getConfig().getString(key);
        return null;
    }
}
