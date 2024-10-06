package me.tippie.tippieutils.functions;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;

public class TextUtil {
  /**
   * Parse a string into a TextComponent
   * It supports color, gradient, rainbow, and decorations
   * @param input The string to parse
   * @return The parsed TextComponent
   */
  public static TextComponent parse(String input) {
    MiniMessage miniMessage = MiniMessage.builder()
      .tags(TagResolver.builder()
        .resolver(StandardTags.color())
        .resolver(StandardTags.gradient())
        .resolver(StandardTags.rainbow())
        .resolver(StandardTags.decorations())
        .build())
      .build();

    Component component = miniMessage.deserialize(input);
    return (TextComponent) component;
  }
  /**
   * Capitalize the first letter of a string
   * @param input The string to capitalize
   * @return The capitalized string
   */
  public static String capitalize(String input) {
    return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
  }
}
