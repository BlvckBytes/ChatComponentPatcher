package me.blvckbytes.chat_component_patcher;

import com.comphenix.protocol.ProtocolLibrary;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatComponentPatcherPlugin extends JavaPlugin {

  @Override
  public void onEnable() {
    var packetListener = new ChatPacketListener(this, getLogger());
    ProtocolLibrary.getProtocolManager().addPacketListener(packetListener);
  }
}