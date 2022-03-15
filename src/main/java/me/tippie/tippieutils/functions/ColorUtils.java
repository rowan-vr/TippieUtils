package me.tippie.tippieutils.functions;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Contract;

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ColorUtils {
	static {
		supportHex = Integer.parseInt(Bukkit.getServer().getVersion().split("\\.")[1]) >= 16;
	}

	private static final boolean supportHex;
	private static final String COLOR_CODES = "abcdef1234567890rABCDEFR";
	private static final String FORMAT_CODES = "kmolnKMOLN";
	private static final Predicate<String> HEX_CODES = Pattern.compile("^#[a-fA-F0-9]{6}.*").asPredicate();
	private static final Pattern URL_PATTERN = Pattern.compile("(?i)(?:http(s)?:\\/\\/|www.)[^\\s.]+\\.[^\\s^'\"]+");

	private static Pattern HEX_CODES_BUKKIT(String character){
		return Pattern.compile("("+character+"x((?:"+ character +"[a-fA-F0-9]){6}))");
	}


	/**
	 * Translates color codes in the given string to the given color using text components.
	 * Links are also made clickable and given a hover text message.
	 * This function also supports hex colour if the server running it supports hex colours.
	 * There are also named colours where the colours are as follows:
	 * <ul>
	 *     <li>0 - black</li>
	 *     <li>1 - dark_blue</li>
	 *     <li>2 - dark_green</li>
	 *     <li>3 - dark_aqua</li>
	 *     <li>4 - dark_red</li>
	 *     <li>5 - dark_purple</li>
	 *     <li>6 - gold</li>
	 *     <li>7 - gray</li>
	 *     <li>8 - dark_gray</li>
	 *     <li>9 - blue</li>
	 *     <li>a - green</li>
	 *     <li>b - aqua</li>
	 *     <li>c - red</li>
	 *     <li>d - light_purple</li>
	 *     <li>e - yellow</li>
	 *     <li>f - white</li>
	 *     <li>k - obfuscated</li>
	 *     <li>l - bold</li>
	 *     <li>m - italic</li>
	 *     <li>n - underlined</li>
	 *     <li>o - strikethrough</li>
	 *     <li>r - reset</li>
	 * </ul>
	 *
	 * @param character The character prepended to the colour code.
	 * @param message The message to translate.
	 * @return The array of text components that represent the translated message.
	 */
	public static TextComponent[] translateColorCodes(String character, String message) {
		return translateColorCodes(character, new TextComponent(message),false);
	}

	/**
	 * Translates color codes in the given string to the given color using text components.
	 * Links are also made clickable and given a hover text message.
	 * This function also supports hex colour if the server running it supports hex colours.
	 * There are also named colours where the colours are as follows:
	 * <ul>
	 *     <li>0 - black</li>
	 *     <li>1 - dark_blue</li>
	 *     <li>2 - dark_green</li>
	 *     <li>3 - dark_aqua</li>
	 *     <li>4 - dark_red</li>
	 *     <li>5 - dark_purple</li>
	 *     <li>6 - gold</li>
	 *     <li>7 - gray</li>
	 *     <li>8 - dark_gray</li>
	 *     <li>9 - blue</li>
	 *     <li>a - green</li>
	 *     <li>b - aqua</li>
	 *     <li>c - red</li>
	 *     <li>d - light_purple</li>
	 *     <li>e - yellow</li>
	 *     <li>f - white</li>
	 *     <li>k - obfuscated</li>
	 *     <li>l - bold</li>
	 *     <li>m - italic</li>
	 *     <li>n - underlined</li>
	 *     <li>o - strikethrough</li>
	 *     <li>r - reset</li>
	 * </ul>
	 *
	 * @param character The character prepended to the colour code.
	 * @param message The message to translate.
	 * @param onlyLinkify Whether to only linkify the message.
	 * @return The array of text components that represent the translated message.
	 */
	public static TextComponent[] translateColorCodes(String character, TextComponent message, boolean onlyLinkify) {
		return translateColorCodes(character, message, onlyLinkify, "§7{url}\n§7Click to open");
	}

	/**
	 * Translates color codes in the given string to the given color using text components.
	 * Links are also made clickable and given a hover text message.
	 * This function also supports hex colour if the server running it supports hex colours.
	 * There are also named colours where the colours are as follows:
	 * <ul>
	 *     <li>0 - black</li>
	 *     <li>1 - dark_blue</li>
	 *     <li>2 - dark_green</li>
	 *     <li>3 - dark_aqua</li>
	 *     <li>4 - dark_red</li>
	 *     <li>5 - dark_purple</li>
	 *     <li>6 - gold</li>
	 *     <li>7 - gray</li>
	 *     <li>8 - dark_gray</li>
	 *     <li>9 - blue</li>
	 *     <li>a - green</li>
	 *     <li>b - aqua</li>
	 *     <li>c - red</li>
	 *     <li>d - light_purple</li>
	 *     <li>e - yellow</li>
	 *     <li>f - white</li>
	 *     <li>k - obfuscated</li>
	 *     <li>l - bold</li>
	 *     <li>m - italic</li>
	 *     <li>n - underlined</li>
	 *     <li>o - strikethrough</li>
	 *     <li>r - reset</li>
	 * </ul>
	 *
	 * @param character The character prepended to the colour code.
	 * @param message The message to translate.
	 * @param onlyLinkify Whether to only linkify the message.
	 * @param hoverText The hover text to use for the links, use {@code {url}} to replace it with the url.
	 * @return The array of text components that represent the translated message.
	 */
	public static TextComponent[] translateColorCodes(String character, TextComponent message,
													  boolean onlyLinkify, String hoverText) {
		Queue<String> matchedUrls = new LinkedList<>();
		Matcher m =	URL_PATTERN.matcher(message.getText());
		while (m.find()) {
			matchedUrls.add(m.group());
			message.setText(message.getText().replace(m.group(),
					character + "zLINK-PLACEHOLDER" + character + "z"));
		}
		String messageStr = message.getText();
		Matcher matcher = HEX_CODES_BUKKIT(character).matcher(messageStr);
		while (matcher.find()){
			messageStr = messageStr.substring(0,matcher.start(1)) + character + "#" + matcher.group(2).replace(character, "") + messageStr.substring(matcher.end(1));
		}
		message.setText(messageStr);
		String[] colors = message.getText().split(character);
		TextComponent baseComponent = (TextComponent) message.duplicate();
		baseComponent.setText("");
		TextComponent[] components = new TextComponent[colors.length];
		ChatColor previous = message.getColor();
		boolean strike = message.isStrikethrough();
		boolean italic = message.isItalic();
		boolean bold = message.isBold();
		boolean underline = message.isUnderlined();
		boolean magic = message.isObfuscated();
		for (int i = 0; i < colors.length; i++) {
			if (i != 0) {
				components[i] = (TextComponent) baseComponent.duplicate();
				if (String.valueOf(colors[i].charAt(0)).equalsIgnoreCase("z")
						&& !colors[i].equalsIgnoreCase("zLINK-PLACEHOLDER")) {
					components[i].setText(colors[i].substring(1));
					components[i].setObfuscated(magic);
					components[i].setStrikethrough(strike);
					components[i].setItalic(italic);
					components[i].setBold(bold);
					components[i].setUnderlined(underline);
					components[i].setColor(previous);
				} else if (onlyLinkify) {
					components[i].setText(character + colors[i]);
					components[i].setColor(previous);
					components[i].setObfuscated(magic);
					components[i].setStrikethrough(strike);
					components[i].setItalic(italic);
					components[i].setBold(bold);
					components[i].setUnderlined(underline);
				} else if (COLOR_CODES.contains(String.valueOf(colors[i].charAt(0)).toLowerCase())
						|| ((HEX_CODES.test(colors[i]))&& supportHex)) {
					boolean isHex = supportHex && HEX_CODES.test(colors[i]);
					components[i].setText(colors[i].substring(isHex ? 7 : 1));
					previous = isHex ? ChatColor.of(colors[i].substring(0,7)): ChatColor.getByChar(colors[i].charAt(0));
					components[i].setColor(previous);
					strike = false;
					italic = false;
					bold = false;
					underline = false;
					magic = false;
				} else if (FORMAT_CODES.contains(String.valueOf(colors[i].charAt(0)).toLowerCase())) {
					components[i].setColor(previous);
					components[i].setText(colors[i].substring(1));
					String code = String.valueOf(colors[i].charAt(0)).toLowerCase();
					if (code.equalsIgnoreCase("k")) {
						magic = true;
					} else if (code.equalsIgnoreCase("m")) {
						strike = true;
					} else if (code.equalsIgnoreCase("o")) {
						italic = true;
					} else if (code.equalsIgnoreCase("l")) {
						bold = true;
					} else if (code.equalsIgnoreCase("n")) {
						underline = true;
					}
					components[i].setObfuscated(magic);
					components[i].setStrikethrough(strike);
					components[i].setItalic(italic);
					components[i].setBold(bold);
					components[i].setUnderlined(underline);
				} else {
					components[i].setText(character + colors[i]);
					components[i].setColor(previous);
					components[i].setObfuscated(magic);
					components[i].setStrikethrough(strike);
					components[i].setItalic(italic);
					components[i].setBold(bold);
					components[i].setUnderlined(underline);
				}
			} else {
				components[i] = (TextComponent) baseComponent.duplicate();
				components[i].setText(colors[i]);
			}
			if (components[i].getText().equalsIgnoreCase(character + "zLINK-PLACEHOLDER")) {
				String url = matchedUrls.poll();
				if (url != null) {
					if (!url.startsWith("https://") && !url.startsWith("http://"))
						url = "https://" + url;
					components[i].setObfuscated(magic);
					components[i].setStrikethrough(strike);
					components[i].setItalic(italic);
					components[i].setBold(bold);
					components[i].setUnderlined(underline);
					components[i].setText(url);
					components[i].setColor(previous);
					components[i].setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
					components[i].setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
							new BaseComponent[]{new TextComponent(hoverText.replace("{url}",url))}));
				}
			}
		}
		return components;
	}


	/**
	 * Translates color codes in the given string to the given color using text components.
	 * Links are also made clickable and given a hover text message.
	 * This function also supports hex colour if the server running it supports hex colours.
	 * There are also named colours where the colours are as follows:
	 * <ul>
	 *     <li>0 - black</li>
	 *     <li>1 - dark_blue</li>
	 *     <li>2 - dark_green</li>
	 *     <li>3 - dark_aqua</li>
	 *     <li>4 - dark_red</li>
	 *     <li>5 - dark_purple</li>
	 *     <li>6 - gold</li>
	 *     <li>7 - gray</li>
	 *     <li>8 - dark_gray</li>
	 *     <li>9 - blue</li>
	 *     <li>a - green</li>
	 *     <li>b - aqua</li>
	 *     <li>c - red</li>
	 *     <li>d - light_purple</li>
	 *     <li>e - yellow</li>
	 *     <li>f - white</li>
	 *     <li>k - obfuscated</li>
	 *     <li>l - bold</li>
	 *     <li>m - italic</li>
	 *     <li>n - underlined</li>
	 *     <li>o - strikethrough</li>
	 *     <li>r - reset</li>
	 * </ul>
	 *
	 * @param character The character prepended to the colour code.
	 * @param message The message to translate.
	 * @return The array of text components that represent the translated message.
	 */
	public static TextComponent[] translateColorCodes(char character, String message) {
		return translateColorCodes(String.valueOf(character), message);
	}

	/**
	 * Translates color codes in the given string to the given color using text components.
	 * Links are also made clickable and given a hover text message.
	 * This function also supports hex colour if the server running it supports hex colours.
	 * There are also named colours where the colours are as follows:
	 * <ul>
	 *     <li>0 - black</li>
	 *     <li>1 - dark_blue</li>
	 *     <li>2 - dark_green</li>
	 *     <li>3 - dark_aqua</li>
	 *     <li>4 - dark_red</li>
	 *     <li>5 - dark_purple</li>
	 *     <li>6 - gold</li>
	 *     <li>7 - gray</li>
	 *     <li>8 - dark_gray</li>
	 *     <li>9 - blue</li>
	 *     <li>a - green</li>
	 *     <li>b - aqua</li>
	 *     <li>c - red</li>
	 *     <li>d - light_purple</li>
	 *     <li>e - yellow</li>
	 *     <li>f - white</li>
	 *     <li>k - obfuscated</li>
	 *     <li>l - bold</li>
	 *     <li>m - italic</li>
	 *     <li>n - underlined</li>
	 *     <li>o - strikethrough</li>
	 *     <li>r - reset</li>
	 * </ul>
	 *
	 * @param character The character prepended to the colour code.
	 * @param message The message to translate.
	 * @return The array of text components that represent the translated message.
	 */
	public static TextComponent[] translateColorCodes(char character, TextComponent message) {
		return translateColorCodes(String.valueOf(character), message,false);
	}


	/**
	 * Replaces text in a TextComponent array that match the given input string with the replacement TextComponent array.
	 * @param message The message to replace in.
	 * @param old The string that should be replaced
	 * @param replacement The replacement.
	 * @return The new TextComponent array where any occurrence of old is replaced with the replacement.
	 */
	public static TextComponent[] replace(TextComponent[] message, String old, TextComponent[] replacement) {
		return replace(message, Pattern.compile(escapeSpecialRegexChars(old)), replacement);
	}

	/**
	 * Replaces text in a TextComponent array that match the given input string with the replacement TextComponent array.
	 * @param message The message to replace in.
	 * @param pattern The regex pattern that should be replaced.
	 * @param replacement The replacement.
	 * @return The new TextComponent array where any match with the pattern is replaced with the replacement.
	 */
	public static TextComponent[] replace(TextComponent[] message, Pattern pattern, TextComponent[] replacement) {
		ArrayList<TextComponent> result = new ArrayList<>();

		// Get the plain text of the input message
		String plain = Stream.of(message).map(TextComponent::getText).collect(Collectors.joining(""));

		Matcher matcher = pattern.matcher(plain);

		int lastEnd = 0;

		while (matcher.find()) {
			// Build a map where the character index can be linked to the index of their TextComponent.
			TreeMap<Integer, Integer> indexMap = new TreeMap<>();
			int charIndex = 0;
			for (int i = 0; i < message.length; i++) {
				TextComponent index = message[i];
				indexMap.put(charIndex, i);
				charIndex += index.getText().length();
			}

			// Correct the positions by removing the character count that is already added to the result.
			int start = matcher.start() - lastEnd;
			int end = matcher.end() - lastEnd;


			int startIndex = indexMap.floorEntry(start).getValue();

			// Add the text before the match.
			result.addAll(Arrays.asList(message).subList(0, startIndex));

			// Remove the text after the match from the first text component that is matched and add it to the result.
			TextComponent startComponent = message[startIndex].duplicate();
			int componentStartIndex = indexMap.floorEntry(start).getKey();
			startComponent.setText(startComponent.getText().substring(0, start - componentStartIndex));
			result.add(startComponent);

			// Remove the text in the match so it doesn't appear anymore in the result.
			int endIndex = indexMap.floorEntry(end - 1).getValue();
			int endComponentCharIndex = indexMap.floorEntry(end - 1).getKey();
			message[endIndex].setText(message[endIndex].getText().substring(end - endComponentCharIndex));

			// Update the lastEnd to the amount of characters that have been removed from the beginning message already.
			lastEnd += end;

			// Add the replacement.
			result.addAll(List.of(replacement));

			// Set the message variable to the unhandled text components
			message = Arrays.copyOfRange(message, endIndex, message.length);
		}

		// No more matches thus add all unhandled text components to the result.
		result.addAll(Arrays.asList(message));

		return result.toArray(new TextComponent[0]);
	}

	/**
	 * Find the text component instance that contains the given query string.
	 * @param message The TextComponent array to search in.
	 * @param query The query string to search for.
	 * @return The first TextComponent instance that contains the query string.
	 */
	public static TextComponent findComponent(TextComponent[] message, String query) {
		for (TextComponent component : message) {
			if (component.getText().contains(query))
				return component;
		}
		return null;
	}


	/**
	 * Translates color codes in the given string to the given color using text components.
	 * Links are also made clickable and given a hover text message.
	 * This function also supports hex colour if the server running it supports hex colours.
	 * There are also named colours where the colours are as follows:
	 * <ul>
	 *     <li>0 - black</li>
	 *     <li>1 - dark_blue</li>
	 *     <li>2 - dark_green</li>
	 *     <li>3 - dark_aqua</li>
	 *     <li>4 - dark_red</li>
	 *     <li>5 - dark_purple</li>
	 *     <li>6 - gold</li>
	 *     <li>7 - gray</li>
	 *     <li>8 - dark_gray</li>
	 *     <li>9 - blue</li>
	 *     <li>a - green</li>
	 *     <li>b - aqua</li>
	 *     <li>c - red</li>
	 *     <li>d - light_purple</li>
	 *     <li>e - yellow</li>
	 *     <li>f - white</li>
	 *     <li>k - obfuscated</li>
	 *     <li>l - bold</li>
	 *     <li>m - italic</li>
	 *     <li>n - underlined</li>
	 *     <li>o - strikethrough</li>
	 *     <li>r - reset</li>
	 * </ul>
	 *
	 * @param character The character prepended to the colour code.
	 * @param message The message to translate.
	 * @return The array of text components that represent the translated message.
	 */
	public static TextComponent[] translateColorCodes(char character, TextComponent[] message) {
		ArrayList<TextComponent> result = new ArrayList<>();
		for (TextComponent component : message) {
			result.addAll(Arrays.asList(translateColorCodes(character, component)));
		}
		return result.toArray(new TextComponent[0]);
	}

	/**
	 * Translates color codes in the given string to the given color using text components.
	 * Links are also made clickable and given a hover text message.
	 * This function also supports hex colour if the server running it supports hex colours.
	 * There are also named colours where the colours are as follows:
	 * <ul>
	 *     <li>0 - black</li>
	 *     <li>1 - dark_blue</li>
	 *     <li>2 - dark_green</li>
	 *     <li>3 - dark_aqua</li>
	 *     <li>4 - dark_red</li>
	 *     <li>5 - dark_purple</li>
	 *     <li>6 - gold</li>
	 *     <li>7 - gray</li>
	 *     <li>8 - dark_gray</li>
	 *     <li>9 - blue</li>
	 *     <li>a - green</li>
	 *     <li>b - aqua</li>
	 *     <li>c - red</li>
	 *     <li>d - light_purple</li>
	 *     <li>e - yellow</li>
	 *     <li>f - white</li>
	 *     <li>k - obfuscated</li>
	 *     <li>l - bold</li>
	 *     <li>m - italic</li>
	 *     <li>n - underlined</li>
	 *     <li>o - strikethrough</li>
	 *     <li>r - reset</li>
	 * </ul>
	 *
	 * @param character The character prepended to the colour code.
	 * @param message The message to translate.
	 * @param onlyLinkify Whether to only make links clickable and not colour the rest.
	 * @return The array of text components that represent the translated message.
	 */
	public static TextComponent[] translateColorCodes(char character, TextComponent[] message, boolean onlyLinkify) {
		ArrayList<TextComponent> result = new ArrayList<>();
		for (TextComponent component : message) {
			result.addAll(Arrays.asList(translateColorCodes(String.valueOf(character), component, onlyLinkify)));
		}
		return result.toArray(new TextComponent[0]);
	}

	/**
	 * Set the colour of all text components in the given array.
	 * @param color The colour to set the text components to.
	 * @param components The components to set the colour of.
	 */
	@Contract(mutates = "param2")
	public static void setColors(ChatColor color, TextComponent... components) {
		for (TextComponent component : components) {
			component.setColor(color);
		}
	}

	private static final Pattern SPECIAL_REGEX_CHARS = Pattern.compile("[{}()\\[\\].+*?^$\\\\|]");

	private static String escapeSpecialRegexChars(String str) {

		return SPECIAL_REGEX_CHARS.matcher(str).replaceAll("\\\\$0");
	}
}
