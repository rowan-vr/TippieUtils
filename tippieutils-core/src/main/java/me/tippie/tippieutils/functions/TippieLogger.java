package me.tippie.tippieutils.functions;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.*;

public class TippieLogger extends Logger {
    private final JavaPlugin plugin;
    private final int prefixHex;
    private final boolean logToFile;
    private final boolean isDebug;

    /**
     * This constructor creates a new LogUtils object
     *
     * @param plugin    The JavaPlugin instance to modify the logger for
     * @param prefixHex The hex color code for the plugin prefix, with or without the #
     * @param options   The optional options for the logger <br>
     *                  index 0: Whether to log to a file instead of the console
     *                  index 1: Whether debug mode is enabled i.e. stack traces are printed
     */
    public TippieLogger(JavaPlugin plugin, int prefixHex, boolean... options) {
        super(plugin.getName(), null);
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
     * @see Level
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
     *   <li>{@link Level#BEST}   - The best log level</li>
     *   <li>{@link Level#BETTER} - A better log level</li>
     *   <li>{@link Level#GOOD}   - A good log level</li>
     *   <li>{@link Level#DEBUG}  - A debug log level</li>
     *   <li>{@link Level#TEST}   - A test log level</li>
     *   <li>{@link Level#TEXT}   - A text log level</li>
     *   <li>{@link Level#WARN}   - A warning log level</li>
     *   <li>{@link Level#ERROR}  - An error log level</li>
     *   <li>{@link Level#CRASH}  - A crash log level</li>
     * </ul>
     */
    @Getter
    public static class Level extends java.util.logging.Level {
        public static final Level BEST = new Level("BEST", 300, "045300");
        public static final Level BETTER = new Level("BETTER", 400, "2ea025");
        public static final Level GOOD = new Level("GOOD", 500, "bcff9e");
        public static final Level DEBUG = new Level("DEBUG", 600, "ce70ff");
        public static final Level TEST = new Level("TEST", 700, "ff00ff");
        public static final Level TEXT = new Level("TEXT", 800, "FBFBFB");
        public static final Level WARN = new Level("WARN", 900, "eeef7a");
        public static final Level ERROR = new Level("ERROR", 1000, "ff8994");
        public static final Level CRASH = new Level("CRASH", 1100, "7d1c25");

        private final String color;

        protected Level(String name, int value, String color) {
            super(name, value);
            this.color = color;
        }

        /**
         * This method translates a {@link Level} to a {@link Level}. If the level is an instance of {@link Level}, it will return itself.
         *
         * @param lvl The level to translate
         * @return Itself if the level is a {@link Level}, otherwise a new {@link Level} based on the {@link Level}
         */
        public static Level translate(java.util.logging.Level lvl) {
            if (lvl instanceof Level l) return l;
            else if (lvl.equals(Level.ALL)) return CRASH;
            else if (lvl.equals(Level.SEVERE)) return ERROR;
            else if (lvl.equals(Level.WARNING)) return WARN;
            else if (lvl.equals(Level.INFO)) return TEXT;
            else if (lvl.equals(Level.CONFIG)) return TEST;
            else if (lvl.equals(Level.FINE)) return GOOD;
            else if (lvl.equals(Level.FINER)) return BETTER;
            else if (lvl.equals(Level.FINEST)) return BEST;
            else return TEXT;
        }

    }

    private class ColorFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            String color = (Level.translate(record.getLevel())).getColor();
            String pluginName = plugin.getName();
            String pluginColor = String.format("\u001B[38;2;%s;%s;%sm",
                    (prefixHex >> 16) & 0xFF,
                    (prefixHex >> 8) & 0xFF,
                    prefixHex & 0xFF
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
            if (record.getThrown() == null) return formattedMessage.toString();
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
