package me.tippie.tippieutils.functions;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.*;

public class LogUtils {
  private final JavaPlugin plugin;
  private final String prefixHex;
  private final boolean logToFile;
  private final boolean isDebug;

  /**
   * This constructor creates a new LogUtils object
   * @param plugin The JavaPlugin instance to modify the logger for
   * @param prefixHex The hex color code for the plugin prefix, with or without the #
   * @param options The optional options for the logger <br>
   *                index 0: Whether to log to a file instead of the console
   *                index 1: Whether debug mode is enabled i.e. stack traces are printed
   */
  public LogUtils(JavaPlugin plugin, String prefixHex, boolean... options) {
    this.plugin = plugin;
    this.prefixHex = prefixHex;
    this.logToFile = options.length > 0 && options[0];
    this.isDebug = options.length > 1 && options[1];
    setupLogger();
  }
  /**
   * This method sets up the logger for the plugin
   */
  private void setupLogger() {
    Logger logger = plugin.getLogger();
    // Remove existing handlers to avoid duplicate logging
    for (Handler handler : logger.getHandlers()) {
      logger.removeHandler(handler);
    }
    logger.setUseParentHandlers(false);
    ConsoleHandler consoleHandler = new ConsoleHandler();
    consoleHandler.setLevel(Level.ALL);
    consoleHandler.setFormatter(new ColorFormatter());
    logger.addHandler(consoleHandler);

    if (logToFile) {
      try {
        FileHandler fileHandler = getFileHandler();
        logger.addHandler(fileHandler);
      } catch (IOException e) {
        logger.log(Level.SEVERE, "Failed to initialize file handler for logging", e);
      }
    }
    logger.setLevel(Level.ALL);
  }
  private @NotNull FileHandler getFileHandler() throws IOException {
    File logDir = new File(plugin.getDataFolder(), "logs");
    if (!logDir.exists() && !logDir.mkdirs()) {
      throw new IllegalStateException("Failed to create log directory: " + logDir.getPath());
    }
    // Create a new log file every day and keep at most 10 previous logs
    FileHandler fileHandler = new FileHandler(new File(logDir, "plugin-%g.log").getPath(), 0, 10, true);
    fileHandler.setLevel(Level.ALL);
    fileHandler.setFormatter(new SimpleFormatter());
    return fileHandler;
  }
  /**
   * This method logs a message to the console using the plugin's logger if it is hooked
   *
   * @param lvl     The level of the message
   * @param message The message to log
   * @see LogLevel
   */
  public void log(Level lvl, Object... message) {
    Logger logger = plugin.getLogger();
    StringBuilder msg = new StringBuilder();
    Throwable throwable = null;
    for (Object e : message) {
      if (e instanceof Throwable t) throwable = t;
      else msg.append(e.toString()).append("\n");
    }
    if (throwable != null) logger.log(lvl, msg.toString(), throwable);
    else logger.log(lvl, msg.toString());
  }

  private static String getStackTrace(Throwable t) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw, true);
    t.printStackTrace(pw);
    return sw.getBuffer().toString();
  }

  /**
   * This class is used to define custom log levels with colors
   * <ul>
   *   <li>{@link LogLevel#BEST}   - The best log level</li>
   *   <li>{@link LogLevel#BETTER} - A better log level</li>
   *   <li>{@link LogLevel#GOOD}   - A good log level</li>
   *   <li>{@link LogLevel#DEBUG}  - A debug log level</li>
   *   <li>{@link LogLevel#TEST}   - A test log level</li>
   *   <li>{@link LogLevel#TEXT}   - A text log level</li>
   *   <li>{@link LogLevel#WARN}   - A warning log level</li>
   *   <li>{@link LogLevel#ERROR}  - An error log level</li>
   *   <li>{@link LogLevel#CRASH}  - A crash log level</li>
   * </ul>
   */
  @Getter
  public static class LogLevel extends Level {
    public static final LogLevel BEST = new LogLevel("BEST", 300, "045300");
    public static final LogLevel BETTER = new LogLevel("BETTER", 400, "2ea025");
    public static final LogLevel GOOD = new LogLevel("GOOD", 500, "bcff9e");
    public static final LogLevel DEBUG = new LogLevel("DEBUG", 600, "ce70ff");
    public static final LogLevel TEST = new LogLevel("TEST", 700, "ff00ff");
    public static final LogLevel TEXT = new LogLevel("TEXT", 800, "FBFBFB");
    public static final LogLevel WARN = new LogLevel("WARN", 900, "eeef7a");
    public static final LogLevel ERROR = new LogLevel("ERROR", 1000, "ff8994");
    public static final LogLevel CRASH = new LogLevel("CRASH", 1100, "7d1c25");

    private final String color;

    protected LogLevel(String name, int value, String color) {
      super(name, value);
      this.color = color;
    }

    /**
     * This method translates a {@link Level} to a {@link LogLevel}. If the level is an instance of {@link LogLevel}, it will return itself.
     * @param lvl The level to translate
     * @return Itself if the level is a {@link LogLevel}, otherwise a new {@link LogLevel} based on the {@link Level}
     */
    public static LogLevel translate(Level lvl) {
      if(lvl instanceof LogLevel l) return l;
      else if(lvl.equals(Level.ALL)) return CRASH;
      else if(lvl.equals(Level.SEVERE)) return ERROR;
      else if(lvl.equals(Level.WARNING)) return WARN;
      else if(lvl.equals(Level.INFO)) return TEXT;
      else if(lvl.equals(Level.CONFIG)) return TEST;
      else if(lvl.equals(Level.FINE)) return GOOD;
      else if(lvl.equals(Level.FINER)) return BETTER;
      else if(lvl.equals(Level.FINEST)) return BEST;
      else return TEXT;
    }

  }

  private class ColorFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
      String color = (LogLevel.translate(record.getLevel())).getColor();
      String pluginName = plugin.getName();
      String pluginColor = String.format("\u001B[38;2;%s;%s;%sm",
        Integer.valueOf(prefixHex.replace("#", "").substring(0, 2), 16),
        Integer.valueOf(prefixHex.replace("#", "").substring(2, 4), 16),
        Integer.valueOf(prefixHex.replace("#", "").substring(4, 6), 16)
      );
      String resetColor = "\u001B[0m";
      String[] lines = formatMessage(record).split("\n");
      StringBuilder formattedMessage = new StringBuilder();
      for (String line : lines) {
        formattedMessage.append(String.format("%s[%s]%s \u001B[38;2;%s;%s;%sm%s%s\n",
          pluginColor, pluginName, resetColor,
          Integer.valueOf(color.substring(0, 2), 16),
          Integer.valueOf(color.substring(2, 4), 16),
          Integer.valueOf(color.substring(4, 6), 16),
          line, resetColor));
      }
      if(record.getThrown() == null) return formattedMessage.toString();
      // Add a colored Throwable if present
      formattedMessage.append(String.format("%s[%s]%s \u001B[38;2;%s;%s;%sm%s%s\n",
        pluginColor, pluginName, resetColor,
        Integer.valueOf(color.substring(0, 2), 16),
        Integer.valueOf(color.substring(2, 4), 16),
        Integer.valueOf(color.substring(4, 6), 16),
        isDebug ? getStackTrace(record.getThrown()) : record.getThrown(), resetColor));
      return formattedMessage.toString();
    }
  }
}
