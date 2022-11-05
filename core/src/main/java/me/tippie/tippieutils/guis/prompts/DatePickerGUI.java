package me.tippie.tippieutils.guis.prompts;

import me.tippie.tippieutils.functions.ItemUtils;
import me.tippie.tippieutils.guis.GuiBuilder;
import me.tippie.tippieutils.guis.GuiManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

import static me.tippie.tippieutils.functions.ItemUtils.getSkull;
import static me.tippie.tippieutils.functions.ItemUtils.getTextItem;

public class DatePickerGUI extends PromptGUI<Instant> {
    private final String title;
    private static final List<ItemStack> GRAY_NUMBERS = List.of(
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTNhNDg3YjFmODFjOWVjYzZlMTg4NTdjNjU2NjUyOWU3ZWZhMjNlZWY1OTgxNGZlNTdkNjRkZjhlMmNmMSJ9fX0="),
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmY2MTI2OTczNWYxZTQ0NmJlY2ZmMjVmOWNiM2M4MjM2Nzk3MTlhMTVmN2YwZmJjOWEwMzkxMWE2OTJiZGQifX19"),
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2Q4MWEzMmQ5NzhmOTMzZGViN2VhMjZhYTMyNmU0MTc0Njk3NTk1YTQyNmVhYTlmMmFlNWY5YzJlNjYxMjkwIn19fQ=="),
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2VhZGFkZWQ4MTU2M2YxYzg3NzY5ZDZjMDQ2ODlkY2RiOWU4Y2EwMWRhMzUyODFjZDhmZTI1MTcyOGQyZCJ9fX0="),
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmM2MDhjMmRiNTI1ZDZkNzdmN2RlNGI5NjFkNjdlNTNlOWQ3YmFjZGFmZjMxZDRjYTEwZmJiZjkyZDY2In19fQ=="),
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTE0NGM1MTkzNDM1MTk5YzEzNWJkNDdkMTY2ZWYxYjRlMmQzMjE4MzgzZGY5ZDM0ZTNiYjIwZDlmOGU1OTMifX19"),
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjYxZjdlMzg1NTY4NTZlYWU1NTY2ZWYxYzQ0YThjYzY0YWY4ZjNhNTgxNjJiMWRkODAxNmE4Nzc4YzcxYyJ9fX0="),
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmUxY2YzMWM0OWEyNGE4ZjM3ODQ5ZmMzYzU0NjNhYjY0Y2M5YmNlYjZmMjc2YTVjNDRhZWRkMzRmZGY1MjAifX19"),
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjFjOWMwOWQ1MmRlYmM0NjVjMzI1NDJjNjhiZTQyYmRhNmY2NzUzZmUxZGViYTI1NzMyN2FjNWEwYzNhZCJ9fX0="),
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmRjZjM5ZjRiY2Q5ODQ4NGIwYjQ3OWE3OTkyZDkyNzBmZTNhNTliOWIxYTgwNmQ3YTY0ZmZiNWI1NTFhZCJ9fX0="),
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTc3YTU2Y2U0MTVkN2MzMDgwODcwNmE5NGNjMmJhZmE4OTdjYjdlNDg2Mjg3YzMzN2E0NGFmNDJiOTI4YzQzIn19fQ=="),
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjhjN2NhODNmZGE4Y2U1ZTdlZWM4ZWU3NDYyY2E3OGFiNWY4MGU5ZjJmNTFkZDYxYWQ0ZDc1Y2FjMzk1OCJ9fX0="),
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2VlNTVmYmIzOWViZTQ5OGQ1Mjg2ZDdhZmEyYWY4ZDRhY2E0ZTFjZjc1OTQ5OTA0NDhiOTdkMDYyYWFjIn19fQ=="),
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGY4NWVmNzk4NGI3MWI2ZGQxMTQ2NDRlYWY3Y2Y4ZWI3ZTljMmE5YjZjNTFkOTQ1ZmNlM2Y4YzE5ZjBjNjFmIn19fQ=="),
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTk5ZjRiMmJmNzU1NjNmNzE3NDFmOTc4MjUyMDZlODQ2MWMwNGViMWU0OTljNDg3NTUzNmI1OTZkZDJhMjcifX19"),
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDgzYTY5NWE5ODI3MjhjNmE2N2FhOTg1OGZkNWZkMmZlZGQ3MWIyMWVjYWI1MTYwNGVmMzA3NDJlODE5In19fQ=="),
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzQyOTdkYTlhNGRiMmFlOWFhNTFlMzVkMWQyYjdmZjJhMDU1YTZlNzhkNWY2YTU2YWM4OWY4ZjdlZTg5YmU3In19fQ=="),
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2M5OWMwZDRmNWI1YTM3N2Y4NGRhNDYzN2VlYzg3M2JhZWNmNTkxY2U1OTZiMWQyMGQ0YjRjZWFmNDgxOGI4In19fQ=="),
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTNlZTg1ZDUxYzI5YTk2NTRjNWRjOTBlMDMzNjVmN2NkZDc5M2RhZDRkMzQ2OTc0NDJlZThkYmVhZDFkOWY2In19fQ=="),
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQ2YTZkYTRhMmQ5MTY5MGJjZTZkY2Q5MTQzNGRlNDhiYTUyYzliN2U1ZGQzNDIxYmExYTU4MjM5YjhjYTE5In19fQ=="),
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGZiMDllMTE1N2ZlYzRmMjUyMTVlODgyZDQ0OWEwNmI2MTNjNjU0NjMxNDliZTc5MjdjZTg4MTdiMmQ0NyJ9fX0="),
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWEzZjZjMzdkZDJjYzhmN2VmODU3YTViZGU0NTEzYWY1MWY1OGU1MzI2ZTFkODY2ZjUzMzE1NDlkZTdkOGUxMSJ9fX0="),
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTM2NGY4OWI4ZjI5MWUxOTI4NjcxZjk1OGM3OTVkZDY5NzA1ZDcyNjJhYTQ5OTI5MmIwMjIyOWEzYWUxMWNlIn19fQ=="),
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTc1Y2I0Y2VhNGYzNTZlMzI5NGM5MmUxMzlhNjU5M2UyZmQ5NGI3Yzg3MmQzOTgzYzkyYTI1OTc1NWY4YjFlIn19fQ=="),
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWRiYjU4MWY0YzIwYjdiYTQ5NmI4ZTQ5YTMzN2E3NmFmNzhiYWRiYTQ4YTJiMzhhZjRkZDExMWIyMjYifX19"),
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNThlMTlkZDA5MmNmOWE5OWMxOWNmYzRmMjgyNGQ2MjhiNzFhZTNiNDY5YTcyNTUxMjI3ODljMTA4MDg5In19fQ=="),
            getSkull("ewogICJ0aW1lc3RhbXAiIDogMTY2NzYwNjUxNzQ5MSwKICAicHJvZmlsZUlkIiA6ICIxNGEwNThiNDc0NzE0ZTY2YjFhMTRmNDY3MDNlOWY3NCIsCiAgInByb2ZpbGVOYW1lIiA6ICJFaW5tZXJhIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2ZlYjgxNzE2NjY2ZWFkZjFjMDc4MDMxODMyMWM4NTEwNTFkYjFjOGVkZmYwNGM3OWRkZmViNmYxYjQ0MTk3MDEiCiAgICB9CiAgfQp9"),
            getSkull("ewogICJ0aW1lc3RhbXAiIDogMTY2NzYwNjUwMTkzOSwKICAicHJvZmlsZUlkIiA6ICJmNWYyNDcyZGVhNDY0ODJhYWUwNDllYjM2ODE5ODU0NiIsCiAgInByb2ZpbGVOYW1lIiA6ICJEZXRhaWxlcnMiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjliOGYyZjE3NWVjMzhiNzE5YmMzYzcyMjJhMjhmOWU3MzJjODMyMTg2M2FmMGU1YzllYjdmOWQyOGFjNjI2ZCIKICAgIH0KICB9Cn0"),
            getSkull("ewogICJ0aW1lc3RhbXAiIDogMTY2NzYwNjQ4ODIxNSwKICAicHJvZmlsZUlkIiA6ICI1MjhlYzVmMmEzZmM0MDA0YjYwY2IwOTA5Y2JiMjdjYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJQdWxpenppIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzQ1ZjZlMGYyZDFiN2JkMWM3MzhkY2ZlMGYwM2M4YTJkNzg0NjgxOTU0NmQ3ZmE1ZWRmNDZlMjAzNTRjYTEzY2YiCiAgICB9CiAgfQp9"),
            getSkull("ewogICJ0aW1lc3RhbXAiIDogMTY2NzYwNjQ3MDkyMCwKICAicHJvZmlsZUlkIiA6ICIzOTdlMmY5OTAyNmI0NjI1OTcyNTM1OTNjODgyZjRmMyIsCiAgInByb2ZpbGVOYW1lIiA6ICJ4WnlkdWVMeCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8yMmVkMjY4NDViMTg2NDhkZTI5YTM1NDkxYjdlNzU0OTk5OGY5MWFmN2FjZDBkNDUwZTQ4OGU5MTlhNmNkMzAyIgogICAgfQogIH0KfQ"),
            getSkull("ewogICJ0aW1lc3RhbXAiIDogMTY2NzYwNjQ1NTQ4NywKICAicHJvZmlsZUlkIiA6ICJlZTg4M2RmMjM0ZWI0YWM1YTFlNDEwODhhYzZkZWIxNyIsCiAgInByb2ZpbGVOYW1lIiA6ICJUdW5lc0Jsb2NrIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2Q2Zjk2NzdjNjRiMzQzZmE1N2RkN2VkMzQzOWUyZGE3YjA5NDdjZmRhNDU3N2UxYTUzMjcyYmE5MzEzMDgzNTQiCiAgICB9CiAgfQp9"),
            getSkull("ewogICJ0aW1lc3RhbXAiIDogMTY2NzYwNjQzNzc5MSwKICAicHJvZmlsZUlkIiA6ICI4N2YzOGM1MWE4Yzc0MmNmYTY2YTgxNWExZTI2NzMzYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJCZWR3YXJzQ3V0aWUiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNThhMjFkMTkwMThmN2FmZDk1OGEyZGZjZGI3YzdlNWYzZDZhNTQzZGE4ZGFmMmY0NDUzYzczYjU4Y2U5NWZjYSIKICAgIH0KICB9Cn0"),
            getSkull("ewogICJ0aW1lc3RhbXAiIDogMTY2NzYwNjQxOTg2MSwKICAicHJvZmlsZUlkIiA6ICJlYjA3ZmQzMmFiOTE0NjRjODVjYmU1YjVhYTlkYTRjZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJSZXphV2luc3RyZWFrIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzg1NjdhNmQ0ZTkzMmQ1YzdmMjcyNDliNjAzY2QxMzYxMjE3MTk4MWYyNDk3MzZmY2U3NDM2ZTkyYTExNTg2OWIiCiAgICB9CiAgfQp9")
    );


    private static final List<ItemStack> GREEN_NUBMERS = List.of(
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjQ1ODFkMzk1NWU5YWNkNTEzZDI4ZGQzMjI1N2FlNTFmZjdmZDZkZjA1YjVmNGI5MjFmMWRlYWU0OWIyMTcyIn19fQ=="),
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQ2NWNlODNmMWFhNWI2ZTg0ZjliMjMzNTk1MTQwZDViNmJlY2ViNjJiNmQwYzY3ZDFhMWQ4MzYyNWZmZCJ9fX0="),
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGQ1NGQxZjhmYmY5MWIxZTdmNTVmMWJkYjI1ZTJlMzNiYWY2ZjQ2YWQ4YWZiZTA4ZmZlNzU3ZDMwNzVlMyJ9fX0="),
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjFlNGVhNTliNTRjYzk5NDE2YmM5ZjYyNDU0OGRkYWMyYTM4ZWVhNmEyZGJmNmU0Y2NkODNjZWM3YWM5NjkifX19"),
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGI1MjdiMjRiNWQyYmNkYzc1NmY5OTVkMzRlYWU1NzlkNzQxNGIwYTVmMjZjNGZmYTRhNTU4ZWNhZjZiNyJ9fX0="),
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODRjOGMzNzEwZGEyNTU5YTI5MWFkYzM5NjI5ZTljY2VhMzFjYTlkM2QzNTg2YmZlYTZlNmUwNjEyNGIzYyJ9fX0="),
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTIxMTNjNjA0YTIyYjIyNGZiZDM1OTdmOTA0YTdmOTIyN2E3YzFhZTUzNDM5Yzk2OTk0YmZhMjNiNTJlYiJ9fX0="),
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjRiZGU3OWY4NGZjNWYzZjFmYmM1YmMwMTA3MTA2NmJkMjBjZDI2M2ExNjU0ZDY0ZDYwZDg0MjQ4YmE5Y2QifX19"),
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjJlZTEzNzFkOGYwZjVhOGI3NTljMjkxODYzZDcwNGFkYzQyMWFkNTE5ZjE3NDYyYjg3NzA0ZGJmMWM3OGE0In19fQ=="),
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDM3OGYyZWQ3NzNjZDZiMjU1MTgxOTIxOGJmZjg3YzM3NGE0YjdkNmYzYjJjMjM2Nzg3ZWE3OTM2N2JmNmQxYyJ9fX0="),
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDFjMWQ0ODZhMmQyNDJkNTdiZDRhY2E0Y2NhOTgxNDViNjEyYWIyYTcwOGQ1OGVlNDVkMDMzNmE5OGZjMzEifX19"),
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTU5YTZiYTIxNDFmNmZiY2JkYWY5NDhkZjRlODYxMmY5ZGI2NDhiYjVjNGU5YzM1MjgzZmI2N2M3OSJ9fX0="),
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTljMTRiYTUzMjUxNTA3YzY3ZGRiYmYyMTFhZjg0MjQ1OTQ3MmRlM2YyYzEwYWYyZDQ5OTdkOWMwMWQyYmIifX19"),
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTk0MTJiYjdmNTYzYzhkZmQwNWI0MWM5MzhlODk3ZDhlM2M1NjBkNDRmYzIyY2E3NjRhZWVmMTBmYzNiZDVjZSJ9fX0="),
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWVlODhlMTVjOTM1YTcyMzRiNmNhOTU1Njk4ZDdjOGNlZmYyM2RlYTc2ZjdiNDI4NjJlNmRiYzlkODRiIn19fQ=="),
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTQ3NjkzYjA0YTdlZGUxNTQxNTM5ZTVkMTM3OGM3NWYzOGQ0MWFlYzJiM2RkYzhjMTExNGRlMzI0NTgzMmM1In19fQ=="),
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTlmNjJhNDY5YTIwNmFkZDczODg3YzczNjYzNzZhNmM0ZjMzNzdiMmY1Yjk3OTM1MWU5NmFjNjM0NTcyIn19fQ=="),
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2VlYmUxYWI2Y2NlMjQ3ZWJkNzRhNjM0YTM5MWY4YjY0Y2E4ZTFmN2JmNzFlYmE0MjU2MmIzNjhhMTFkODQ4In19fQ=="),
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjExMmNlZTVmM2E2N2I2NDQ2ZjNlMmViY2Q5OGE3MzYxODliMWY1MmNlMmQ4M2M5NDJlMjJhN2Q2ZDJhZWEifX19"),
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjU1ODMyOTVmODE1ZmJjYTUyMTI1ODliMjI5YWQ1NGMxYTM4MWNhYmRkYTg3NzNkY2QyNGVkM2FkN2QxZSJ9fX0="),
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjBmN2NkODJlM2I0MTZiNjZiNTk2MzcxMWU0OWRmZjhmNTViMWMyNDE2MTFmZDExZDkxNzk0YzAxNmExYmIifX19"),
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWZjMDZlY2Y3MjMzNTExOTJhZGJiMDFmYWFiNjVkMWY3OWU1ZmMxMjFiZGY0NGRkZmExYjM2ZTkzNThlZDVhIn19fQ=="),
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjZiZWM4YjVkNDc5ZDhkYmNiZWMyMjdlMjg2ZTU2ZmQ0ZjE2NWVhZTQ4YWQzMTU1YmM4NzY2ODQ0OTZlIn19fQ=="),
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzYzOTVkMDExN2EyM2U5OGQ4MGI0ZDc3MWQ0NmQzOTYzZDBkYzkyNGM0NTRkN2M1NTJmMzNjYTU1NjEifX19"),
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWMxMGU1MjViYjQ4NGU2ZjNhZTE4ODk4MzE3NjMzNWQ5NDk3MTYxMjk2M2NlZTZhY2FkNzUwOTI2ODdkYyJ9fX0="),
            getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTEzMWQ2ZTA0NmFjMjU3OTY4MWM5NjI2YjZmZDE0NzZkYTc2YWU3YzM5NGNhMzhjMDgzYjViZTI0ZTEifX19"),
            getSkull("ewogICJ0aW1lc3RhbXAiIDogMTY2NzU5OTE0NDE3MywKICAicHJvZmlsZUlkIiA6ICI0M2NmNWJkNjUyMDM0YzU5ODVjMDIwYWI3NDE0OGQxYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJrYW1pbDQ0NSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS81MzBlZTcyMDdkZTM3YzI4ZjA2YTY1Yzg1NjQ0YjgzMDhhMTcyZDY5MWU0YmIwYjc4OGM4YjA0NjhjM2M3ODExIgogICAgfQogIH0KfQ"),
            getSkull("ewogICJ0aW1lc3RhbXAiIDogMTY2NzU5OTEyODIxMSwKICAicHJvZmlsZUlkIiA6ICJiNDA5ZmMyZjg4OTk0ZTVkOWZmNWFiMmUyOWZjNjQzMyIsCiAgInByb2ZpbGVOYW1lIiA6ICJkb2dzaGp0IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzQxN2MyYjdiOGJhZTBjMGJjODIxZTFmMzYwMjYyMjE2ZWE1M2Q0ZmQ2NGRmZThhNGNiNzUxY2EwMmI1ZDk4ZmIiCiAgICB9CiAgfQp9"),
            getSkull("ewogICJ0aW1lc3RhbXAiIDogMTY2NzU5OTExMDk2NiwKICAicHJvZmlsZUlkIiA6ICJmYzg3ZTI3YTYwZjY0NjdhOGMwMDgyMmI2ZWY5ZTMyNSIsCiAgInByb2ZpbGVOYW1lIiA6ICJhbmRyZWlvX28iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWE1ZTMyNmQ0OTg0OTMwNjczZjcwMWFjNjYzYjdlNzNjNmVmNGRjY2UxNTkyOGY0ZjY5MmRlMTdiNzBjNTUxOCIKICAgIH0KICB9Cn0"),
            getSkull("ewogICJ0aW1lc3RhbXAiIDogMTY2NzU5OTA5MjY4OSwKICAicHJvZmlsZUlkIiA6ICJkOGNkMTNjZGRmNGU0Y2IzODJmYWZiYWIwOGIyNzQ4OSIsCiAgInByb2ZpbGVOYW1lIiA6ICJaYWNoeVphY2giLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTk1NGJlZDBmYjA5NGI0Yzc5YjUzNTQxOGFhNzZkOGM5NjA1ODRjMDEzYjNhYjE2MDQwNjEyOWQ0NWRkZjY2MyIKICAgIH0KICB9Cn0"),
            getSkull("ewogICJ0aW1lc3RhbXAiIDogMTY2NzU5OTA3Nzg5OCwKICAicHJvZmlsZUlkIiA6ICIxMzdmMjg3MjUwOTE0ZmI4YjA0ZTYwYjg4MWUwZWE2YiIsCiAgInByb2ZpbGVOYW1lIiA6ICJub3JtYWxpc2luZyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS82NGNmZGJlOWJkN2FiYTQwZGFlYjQ2MTU2ZDUyNGM1ZDJkY2RiNzAxOWU0MzE5NTAwNTUwNjQ5NzkzYjFlMDMxIgogICAgfQogIH0KfQ"),
            getSkull("ewogICJ0aW1lc3RhbXAiIDogMTY2NzU5OTA2MTk0OCwKICAicHJvZmlsZUlkIiA6ICJkMmM1MThjMzJjNTY0OGJmOTM2ZjY0YjhmZDQ1ZThjZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJEcmFjb0JlbGxzIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2FkNTUyMmZiNDNhMGIyYmEwMGYwOTI0YWIxYTM1ZDZlNzkxNGNkOGVkMTczZDNjMjBlYzQ3ZDE4ODQxZTljMjMiCiAgICB9CiAgfQp9"),
            getSkull("ewogICJ0aW1lc3RhbXAiIDogMTY2NzU5OTA0MjUwMywKICAicHJvZmlsZUlkIiA6ICIwYzE1OTI3Yjc4OTY0MTA3OTA5MWQyMjkxN2U0NmIyYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJZb3VBcmVTY2FyeSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS83MzY5ODc2NjUwNmM1MWRjNjRlYWRhMjMyZGE2MTg2YmY0ZWY3MzI2Y2YwZTg2ODg5OTA1OWI1NmZlNDM1ZDk1IgogICAgfQogIH0KfQ")
    );

    private static final ItemStack GRAY_COLON = getSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTkxOWZlNTk5Zjk4MjU4ZGRmZjQ0Yzc2ODEzN2UyZTljYmFmNGI5NGY1YTI1MjMzYzI3ZjNkZDJmNTM1OSJ9fX0=");
    private Instant date = Instant.now();

    public DatePickerGUI(Player player, GuiManager manager, String title) {
        super(player, manager);
        this.title = title;
    }

    private CompletableFuture<Instant> future;

    @Override
    public CompletableFuture<Instant> prompt() {
        if (future != null)
            throw new IllegalStateException("Only call prompt once per PromptGUI instance");

        future = new CompletableFuture<>();
        openDatePicker();

        return future;
    }

    private void openDatePicker() {
        ZonedDateTime zoned = date.atZone(ZoneId.systemDefault());
        openDatePicker(zoned.getMonth(), Year.of(zoned.getYear()));
    }

    private void openDatePicker(Month month, Year year) {
        GuiBuilder builder = new GuiBuilder(6, title, null);
        ZonedDateTime zoned = date.atZone(ZoneId.systemDefault());

        // Button to go to time picker
        builder.setSlot(44, getTextItem(Material.CLOCK, "§6Choose Time"), (inventoryClickEvent, openGUI) -> {
            openTimePicker();
        });

        // Create calendar
        int row = 0;
        for (int i = 1; i < month.length(year.isLeap()) + 1; i++) {
            LocalDate localDate = year.atMonth(month).atDay(i);
            int dayOfWeek = year.atMonth(month).atDay(i).getDayOfWeek().getValue();
            boolean selected = zoned.get(ChronoField.MONTH_OF_YEAR) == month.getValue() && zoned.get(ChronoField.YEAR) == year.getValue() && zoned.get(ChronoField.DAY_OF_MONTH) == i;
            ItemStack skull = (selected ? GREEN_NUBMERS : GRAY_NUMBERS).get(i).clone();
            ItemMeta meta = skull.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + year.atMonth(month).atDay(i).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)));
            skull.setItemMeta(meta);
            int finalI = i;
            builder.setSlot(row * 9 + dayOfWeek, skull, (inventoryClickEvent, openGUI) -> {
                date = zoned.withDayOfMonth(finalI).withMonth(month.getValue()).withYear(year.getValue()).toInstant();
                openDatePicker(month, year);
            });
            if (dayOfWeek == 7) row++;
        }

        // Create next and previous month buttons

        LocalDate localDate = year.atMonth(month).atDay(15);
        LocalDate prevMonth = localDate.minus(1, ChronoUnit.MONTHS);
        LocalDate nextMonth = localDate.plus(1, ChronoUnit.MONTHS);

        builder.setSlot(18, ItemUtils.getTextItem(Material.ARROW, ChatColor.GOLD + prevMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.UK) + " " + nextMonth.getYear()), (inventoryClickEvent, openGUI) -> {
            openDatePicker(prevMonth.getMonth(), Year.of(prevMonth.getYear()));
        });

        builder.setSlot(26, ItemUtils.getTextItem(Material.ARROW, ChatColor.GOLD + nextMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.UK) + " " + nextMonth.getYear()), (inventoryClickEvent, openGUI) -> {
            openDatePicker(nextMonth.getMonth(), Year.of(nextMonth.getYear()));
        });

        commonComponents(builder);
    }

    private void openTimePicker() {
        GuiBuilder builder = new GuiBuilder(6, title, null);

        // Button to go to date picker
        builder.setSlot(44, getTextItem(Material.CLOCK, "§6Choose Date"), (inventoryClickEvent, openGUI) -> {
            openDatePicker();
        });

        // Create rendered time
        ZonedDateTime zoned = date.atZone(ZoneId.systemDefault());
        String twhFmt = zoned.format(DateTimeFormatter.ISO_TIME);
        String localFmt = zoned.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.LONG));
        int first = Integer.parseInt(String.valueOf(twhFmt.charAt(0)));
        int second = Integer.parseInt(String.valueOf(twhFmt.charAt(1)));
        int third = Integer.parseInt(String.valueOf(twhFmt.charAt(3)));
        int fourth = Integer.parseInt(String.valueOf(twhFmt.charAt(4)));

        ItemStack firstItem = GREEN_NUBMERS.get(first).clone();
        ItemMeta firstMeta = firstItem.getItemMeta();
        firstMeta.setDisplayName(ChatColor.GREEN + localFmt);
        firstItem.setItemMeta(firstMeta);
        builder.setSlot(19, firstItem, null);

        ItemStack secondItem = GREEN_NUBMERS.get(second).clone();
        ItemMeta secondMeta = secondItem.getItemMeta();
        secondMeta.setDisplayName(ChatColor.GREEN + localFmt);
        secondItem.setItemMeta(secondMeta);
        builder.setSlot(20, secondItem, null);


        ItemStack colonItem = GRAY_COLON.clone();
        ItemMeta colonMeta = colonItem.getItemMeta();
        colonMeta.setDisplayName(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + ":");
        colonItem.setItemMeta(colonMeta);
        builder.setSlot(21, colonItem, null);

        ItemStack thirdItem = GREEN_NUBMERS.get(third).clone();
        ItemMeta thirdMeta = thirdItem.getItemMeta();
        thirdMeta.setDisplayName(ChatColor.GREEN + localFmt);
        thirdItem.setItemMeta(thirdMeta);
        builder.setSlot(22, thirdItem, null);

        ItemStack fourthItem = GREEN_NUBMERS.get(fourth).clone();
        ItemMeta fourthMeta = fourthItem.getItemMeta();
        fourthMeta.setDisplayName(ChatColor.GREEN + localFmt);
        fourthItem.setItemMeta(fourthMeta);
        builder.setSlot(23, fourthItem, null);

        // Create increase / decrease buttons

        ItemStack increaseItem = ItemUtils.getTextItem(Material.ARROW, ChatColor.GOLD + "Increase");
        builder.setSlot(10, increaseItem, (inventoryClickEvent, openGUI) -> {
            date = date.plus(10, ChronoUnit.HOURS);
            openTimePicker();
        });
        builder.setSlot(11, increaseItem, (inventoryClickEvent, openGUI) -> {
            date = date.plus(1, ChronoUnit.HOURS);
            openTimePicker();
        });
        builder.setSlot(13, increaseItem, (inventoryClickEvent, openGUI) -> {
            date = date.plus(10, ChronoUnit.MINUTES);
            openTimePicker();
        });
        builder.setSlot(14, increaseItem, (inventoryClickEvent, openGUI) -> {
            date = date.plus(1, ChronoUnit.MINUTES);
            openTimePicker();
        });

        ItemStack decreaseItem = ItemUtils.getTextItem(Material.ARROW, ChatColor.GOLD + "Decrease");
        builder.setSlot(28, decreaseItem, (inventoryClickEvent, openGUI) -> {
            date = date.minus(10, ChronoUnit.HOURS);
            openTimePicker();
        });
        builder.setSlot(29, decreaseItem, (inventoryClickEvent, openGUI) -> {
            date = date.minus(1, ChronoUnit.HOURS);
            openTimePicker();
        });
        builder.setSlot(31, decreaseItem, (inventoryClickEvent, openGUI) -> {
            date = date.minus(10, ChronoUnit.MINUTES);
            openTimePicker();
        });
        builder.setSlot(32, decreaseItem, (inventoryClickEvent, openGUI) -> {
            date = date.minus(1, ChronoUnit.MINUTES);
            openTimePicker();
        });

        commonComponents(builder);
    }

    private void commonComponents(GuiBuilder builder) {
        ZonedDateTime zoned = date.atZone(ZoneId.systemDefault());
        builder.setCloseConsumer((inventoryCloseEvent, openGUI) -> {
            if (!future.isDone())
                openGUI.setCancelNextClose(true);
        });

        builder.setSlot(53, ItemUtils.getTextItem(Material.GREEN_TERRACOTTA, ChatColor.GREEN + "" + ChatColor.BOLD + "Confirm Date", ChatColor.GRAY + zoned.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL))), (inventoryClickEvent, openGUI) -> {
            future.complete(date);
            inventoryClickEvent.getWhoClicked().closeInventory();
        });

        builder.setSlot(8, ItemUtils.getTextItem(Material.BARRIER, ChatColor.RED + "" + ChatColor.BOLD + "Cancel"), (inventoryClickEvent, openGUI) -> {
            future.completeExceptionally(new RuntimeException("User cancelled prompt"));
            inventoryClickEvent.getWhoClicked().closeInventory();
        });

        builder.open(player, manager);
    }
}
