package me.blvckbytes.chat_component_patcher;

import com.comphenix.protocol.ProtocolLibrary;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatComponentPatcherPlugin extends JavaPlugin {

  @Override
  public void onEnable() {
    var logger = getLogger();

    try {
      var version = ServerVersion.parseCurrent();

      if (version == null)
        throw new IllegalStateException("Could not parse the current server-version");

      var jsonRW = decideJsonRW(version, logger);

      if (jsonRW == null)
        throw new IllegalStateException("Unsupported version: " + version);

      logger.info("Detected and loaded " + jsonRW.getClass().getSimpleName());

      var packetListener = new ChatPacketListener(this, version, jsonRW, logger);
      ProtocolLibrary.getProtocolManager().addPacketListener(packetListener);
    } catch (Throwable e) {
      logger.log(Level.SEVERE, "An error occurred while trying to enable the plugin", e);
      Bukkit.getPluginManager().disablePlugin(this);
    }
  }

  private @Nullable PacketJsonRW decideJsonRW(ServerVersion version, Logger logger) {
    if (version.compareTo(ServerVersion.V1_21) >= 0)
      return new GTE_V1_21_PacketJsonRW(logger);

    if (version.compareTo(ServerVersion.V1_19_3) >= 0)
      return new GTE_V1_19_3_PacketJsonRW(logger);

    if (version.compareTo(ServerVersion.V1_19_1) >= 0)
      return new GTE_V1_19_1_PacketJsonRW(logger);

    return null;
  }
}