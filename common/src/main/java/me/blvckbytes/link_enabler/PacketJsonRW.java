package me.blvckbytes.link_enabler;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;
import org.jetbrains.annotations.Nullable;

public abstract class PacketJsonRW {

  public @Nullable JsonAndUpdater tryExtract(PacketEvent event) {
    var packet = event.getPacket();

    if (packet.getType() == PacketType.Play.Server.SYSTEM_CHAT)
      return tryExtractSystemChat(event);

    if (packet.getType() == PacketType.Play.Server.CHAT)
      return tryExtractPlayerChat(event);

    return null;
  }

  protected abstract @Nullable JsonAndUpdater tryExtractSystemChat(PacketEvent event);

  protected abstract @Nullable JsonAndUpdater tryExtractPlayerChat(PacketEvent event);

}
