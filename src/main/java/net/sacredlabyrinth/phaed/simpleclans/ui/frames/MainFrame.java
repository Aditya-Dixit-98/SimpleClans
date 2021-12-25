package net.sacredlabyrinth.phaed.simpleclans.ui.frames;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.ui.*;
import net.sacredlabyrinth.phaed.simpleclans.ui.frames.staff.StaffFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.*;

public class MainFrame extends SCFrame {

	private final SimpleClans plugin = SimpleClans.getInstance();

	public MainFrame(Player viewer) {
		super(null, viewer);
	}

	@Override
	public void createComponents() {
		add(Components.getPlayerComponent(this, getViewer(), getViewer(), 0, false)); // TODO
		add(Components.getClanComponent(this, getViewer(),
				plugin.getClanManager().getCreateClanPlayer(getViewer().getUniqueId()).getClan(), 1, true)); // TODO
		addLeaderboard();
		addClanList();
		addResetKdr();
		addStaff();
		addLanguageSelector();
		addOtherCommands();
	}

	private void addOtherCommands() {
		SCComponent otherCommands = getComponentFromConfig("other_commands",
				lang("gui.main.other.commands.title", getViewer()), lang("gui.main.other.commands.lore", getViewer()));
		otherCommands.setListener(ClickType.LEFT, () -> InventoryController.runSubcommand(getViewer(), "help", false));
		add(otherCommands);
	}

	private void addStaff() {
		if (plugin.getPermissionsManager().has(getViewer(), "simpleclans.mod.staffgui")) {
			SCComponent staff = getComponentFromConfig("staff", lang("gui.main.staff.title", getViewer()),
					lang("gui.main.staff.lore", getViewer()));
			staff.setPermission(ClickType.LEFT, "simpleclans.mod.staffgui");
			staff.setListener(ClickType.LEFT, () -> InventoryDrawer.open(new StaffFrame(this, getViewer())));
			add(staff);

		}
	}

	private void addLeaderboard() {
		SCComponent leaderboard = getComponentFromConfig("leaderboard", lang("gui.main.leaderboard.title", getViewer()),
				lang("gui.main.leaderboard.lore", getViewer()));
		leaderboard.setListener(ClickType.LEFT,
				() -> InventoryDrawer.open(new LeaderboardFrame(getViewer(), this)));
		leaderboard.setPermission(ClickType.LEFT, "simpleclans.anyone.leaderboard");
		add(leaderboard);

	}

	private void addClanList() {
		SCComponent clanList = getComponentFromConfig("clan_list", lang("gui.main.clan.list.title", getViewer()),
				lang("gui.main.clan.list.lore", getViewer()));
		clanList.setListener(ClickType.LEFT, () -> InventoryDrawer.open(new ClanListFrame(this, getViewer())));
		clanList.setPermission(ClickType.LEFT, "simpleclans.anyone.list");
		add(clanList);
	}

	private void addLanguageSelector() {
		if (plugin.getSettingsManager().is(LANGUAGE_SELECTOR)) {
			List<String> lore = Arrays.asList(lang("gui.main.languageselector.lore.left.click", getViewer()),
					lang("gui.main.languageselector.lore.right.click", getViewer()));
			SCComponent language = getComponentFromConfig("language_selector",
					lang("gui.main.languageselector.title", getViewer()), lore);
			language.setListener(ClickType.LEFT, () -> InventoryDrawer.open(new LanguageSelectorFrame(this, getViewer())));
			language.setListener(ClickType.RIGHT, () -> {
				getViewer().sendMessage(lang("click.to.help.translating", getViewer(),
						"https://crowdin.com/project/simpleclans"));
				getViewer().closeInventory();
			});
			add(language);
		}
	}

	public void addResetKdr() {
		List<String> lore;
		if (plugin.getSettingsManager().is(ECONOMY_PURCHASE_RESET_KDR)) {
			lore = Arrays.asList(
					lang("gui.main.reset.kdr.lore.price", getViewer(), plugin.getSettingsManager().getString(ECONOMY_RESET_KDR_PRICE)),
					lang("gui.main.reset.kdr.lore", getViewer()));
		} else {
			lore = Collections.singletonList(lang("gui.main.reset.kdr.lore", getViewer()));
		}
		SCComponent resetKdr = getComponentFromConfig("reset_kdr", lang("gui.main.reset.kdr.title", getViewer()), lore);
		resetKdr.setConfirmationRequired(ClickType.LEFT);
		resetKdr.setPermission(ClickType.LEFT, "simpleclans.vip.resetkdr");
		add(resetKdr);
	}

	@Override
	public @NotNull String getTitle() {
		return lang("gui.main.title", getViewer(), plugin.getSettingsManager().getColored(SERVER_NAME));
	}

}
