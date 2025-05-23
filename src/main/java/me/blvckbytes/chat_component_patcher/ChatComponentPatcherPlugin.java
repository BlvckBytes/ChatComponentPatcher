package me.blvckbytes.chat_component_patcher;

import com.comphenix.protocol.ProtocolLibrary;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class ChatComponentPatcherPlugin extends JavaPlugin {

  @Override
  public void onEnable() {
    var logger = getLogger();

    try {
      var version = ServerVersion.parseCurrent();

      if (version == null)
        throw new IllegalStateException("Could not parse the current server-version");

      var packetListener = new ChatPacketListener(this, version, logger);
      ProtocolLibrary.getProtocolManager().addPacketListener(packetListener);
    } catch (Throwable e) {
      logger.log(Level.SEVERE, "An error occurred while trying to enable the plugin", e);
      Bukkit.getPluginManager().disablePlugin(this);
    }
  }
}