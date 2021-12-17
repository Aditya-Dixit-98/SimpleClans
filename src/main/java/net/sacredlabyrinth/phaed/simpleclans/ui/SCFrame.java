package net.sacredlabyrinth.phaed.simpleclans.ui;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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

	public abstract int getSize();

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

	private YamlConfiguration readConfig() {
		SimpleClans plugin = SimpleClans.getInstance();
		File externalFile = new File(plugin.getDataFolder(), getConfigPath());
		InputStream resource = plugin.getResource(getConfigPath());
		YamlConfiguration config = YamlConfiguration.loadConfiguration(externalFile);
		if (resource != null) {
			YamlConfiguration defaults = YamlConfiguration.loadConfiguration(new InputStreamReader(resource));
			config.setDefaults(defaults);
		}
		return config;
	}

	private String getConfigPath() {
		return getClass().getName().replace("net.sacredlabyrinth.phaed.simpleclans.ui.", "")
				.replace(".", File.pathSeparator);
	}

}
