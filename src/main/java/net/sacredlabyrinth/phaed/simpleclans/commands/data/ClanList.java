package net.sacredlabyrinth.phaed.simpleclans.commands.data;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import net.sacredlabyrinth.phaed.simpleclans.utils.KDRFormat;
import net.sacredlabyrinth.phaed.simpleclans.utils.RankingNumberResolver;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.*;
import static net.sacredlabyrinth.phaed.simpleclans.utils.RankingNumberResolver.RankingType.ORDINAL;
import static org.bukkit.ChatColor.*;

public class ClanList extends Sendable {

    private final @Nullable String type;
    private final @Nullable String order;

    public ClanList(@NotNull SimpleClans plugin,
                    @NotNull CommandSender sender,
                    @Nullable String type,
                    @Nullable String order) {
        super(plugin, sender);
        this.type = type;
        this.order = order;
    }

    @Override
    public void send() {
        List<Clan> clans = getListableClans();
        if (clans.isEmpty()) {
            ChatBlock.sendMessage(sender, RED + lang("no.clans.have.been.created", sender));
            return;
        }
        RankingNumberResolver<Clan, ? extends Comparable<?>> ranking = getRankingResolver(clans, type, order);
        sendHeader(clans);
        for (Clan clan : clans) {
            addLine(ranking, clan);
        }

        sendBlock();
    }

    private RankingNumberResolver<Clan, ? extends Comparable<?>> getRankingResolver(List<Clan> clans,
                                                                                    @Nullable String type,
                                                                                    @Nullable String order) {
        boolean ascending = order == null || lang("list.order.asc").equalsIgnoreCase(order);
        if (type == null) {
            type = sm.getString(LIST_DEFAULT_ORDER_BY);
        }
        if (type.equalsIgnoreCase(lang("list.type.size"))) {
            return new RankingNumberResolver<>(clans, Clan::getSize, order != null && ascending, ORDINAL);
        }
        if (type.equalsIgnoreCase(lang("list.type.active"))) {
            return new RankingNumberResolver<>(clans, Clan::getLastUsed, order != null && ascending, ORDINAL);
        }
        if (type.equalsIgnoreCase(lang("list.type.founded"))) {
            return new RankingNumberResolver<>(clans, Clan::getFounded, ascending, ORDINAL);
        }
        if (type.equalsIgnoreCase(lang("list.type.name"))) {
            return new RankingNumberResolver<>(clans, Clan::getName, ascending, ORDINAL);
        }
        return new RankingNumberResolver<>(clans, clan -> KDRFormat.toBigDecimal(clan.getTotalKDR()),
                order != null && ascending, sm.getRankingType());
    }

    @NotNull
    private List<Clan> getListableClans() {
        List<Clan> clans = plugin.getClanManager().getClans();
        clans = clans.stream().filter(clan -> clan.isVerified() || sm.is(SHOW_UNVERIFIED_ON_LIST))
                .collect(Collectors.toList());
        return clans;
    }

    private void sendHeader(List<Clan> clans) {
        ChatBlock.sendBlank(sender);
        ChatBlock.saySingle(sender, sm.getColored(SERVER_NAME) + subColor + " " + lang("clans.lower", sender)
                + " " + headColor + Helper.generatePageSeparator(sm.getString(PAGE_SEPARATOR)));
        ChatBlock.sendBlank(sender);
        ChatBlock.sendMessage(sender, headColor + lang("total.clans", sender) + " " + subColor + clans.size());
        ChatBlock.sendBlank(sender);
        chatBlock.setAlignment("c", "l", "c", "c");
        chatBlock.setFlexibility(false, true, false, false);
        chatBlock.addRow("  " + headColor + lang("rank", sender), lang("name", sender),
                lang("kdr", sender), lang("members", sender));
    }

    private void addLine(RankingNumberResolver<Clan, ? extends Comparable<?>> ranking, Clan clan) {
        String tag = sm.getColored(CLANCHAT_BRACKET_COLOR) + sm.getString(CLANCHAT_BRACKET_LEFT)
                + sm.getColored(TAG_DEFAULT_COLOR) + clan.getColorTag() + sm.getColored(CLANCHAT_BRACKET_COLOR)
                + sm.getString(CLANCHAT_BRACKET_RIGHT);
        String name = (clan.isVerified() ? sm.getColored(PAGE_CLAN_NAME_COLOR) : GRAY) + clan.getName();
        String fullname = tag + " " + name;
        String size = WHITE + "" + clan.getSize();
        String kdr = clan.isVerified() ? YELLOW + "" + KDRFormat.format(clan.getTotalKDR()) : "";

        chatBlock.addRow("  " + ranking.getRankingNumber(clan), fullname, kdr, size);
    }

    protected void sendBlock() {
        SettingsManager sm = plugin.getSettingsManager();
        boolean more = chatBlock.sendBlock(sender, sm.getInt(PAGE_SIZE));

        if (more) {
            plugin.getStorageManager().addChatBlock(sender, chatBlock);
            ChatBlock.sendBlank(sender);
            ChatBlock.sendMessage(sender, sm.getColored(PAGE_HEADINGS_COLOR) + lang("view.next.page", sender,
                    sm.getString(COMMANDS_MORE)));
        }
        ChatBlock.sendBlank(sender);
    }
}
