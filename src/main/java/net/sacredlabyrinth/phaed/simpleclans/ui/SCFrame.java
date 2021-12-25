package net.sacredlabyrinth.phaed.simpleclans.ui;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.cryptomorin.xseries.XMaterial;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

/**
 * 
 * @author RoinujNosde
 *
 */
public abstract class SCFrame {

	private final SCFrame parent;
	private final Player viewer;
	private final Set<SCComponent> components = ConcurrentHashMap.newKeySet();
	private final FileConfiguration config;
	
	public SCFrame(@Nullable SCFrame parent, @NotNull Player viewer) {
		this.parent = parent;
		this.viewer = viewer;
		this.config = readConfig();
	}

	@NotNull
	public abstract String getTitle();

	@NotNull
	public Player getViewer() {
		return viewer;
	}

	@Nullable
	public SCFrame getParent() {
		return parent;
	}

	public int getSize() {
		return getConfig().getInt("size");
	}

	public abstract void createComponents();

	@Nullable
	public SCComponent getComponent(int slot) {
		for (SCComponent c : getComponents()) {
			if (c.getSlot() == slot) {
				return c;
			}
		}
		return null;
	}
	
	public void add(@NotNull SCComponent c) {
		if (!c.isEnabled()) {
			return;
		}
		components.add(c);
	}
	
	public void clear() {
		components.clear();
	}

	@NotNull
	public Set<SCComponent> getComponents() {
		return components;
	}

	public FileConfiguration getConfig() {
		return config;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof SCFrame) {
			SCFrame otherFrame = (SCFrame) other;
			return getSize() == otherFrame.getSize() && getTitle().equals(otherFrame.getTitle())
					&& getComponents().equals(otherFrame.getComponents());
		}

		return false;
	}

	@Override
	public int hashCode() {
		return getTitle().hashCode() + Integer.hashCode(getSize()) + getComponents().hashCode();
	}

	protected @NotNull SCComponent getComponentFromConfig(String key, String displayName, List<String> lore) {
		int slot = getConfig().getInt("components." + key + ".slot");
		XMaterial material = XMaterial.matchXMaterial(getConfig().getString("components." + key + ".material"))
				.orElse(XMaterial.STONE);

		SCComponent component = new SCComponentImpl(displayName, lore, material, slot);
		component.setEnabled(getConfig().getBoolean("components." + key + ".enabled"));

		return component;
	}

	protected @NotNull SCComponent getComponentFromConfig(String key, String displayName, String lore) {
		return getComponentFromConfig(key, displayName, Collections.singletonList(lore));
	}

	private YamlConfiguration readConfig() {
		SimpleClans plugin = SimpleClans.getInstance();
		File externalFile = new File(plugin.getDataFolder(), getConfigPath()); // TODO Create file if it doesn't exist?
		InputStream resource = plugin.getResource(getConfigPath());
		YamlConfiguration config = YamlConfiguration.loadConfiguration(externalFile);
		if (resource != null) {
			YamlConfiguration defaults = YamlConfiguration.loadConfiguration(new InputStreamReader(resource));
			config.setDefaults(defaults);
		}
		return config;
	}

	private String getConfigPath() {
		return ("frames." + getFrameName()).replace(".", File.pathSeparator);
	}

	private String getFrameName() { // TODO Still necessary?
		return getClass().getName().replace("net.sacredlabyrinth.phaed.simpleclans.ui.frames.", "");
	}

}
