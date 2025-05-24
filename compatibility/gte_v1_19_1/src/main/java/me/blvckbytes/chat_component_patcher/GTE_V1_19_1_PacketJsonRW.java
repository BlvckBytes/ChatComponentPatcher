package me.blvckbytes.chat_component_patcher;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GTE_V1_19_1_PacketJsonRW extends PacketJsonRW {

  private final Logger logger;

  public GTE_V1_19_1_PacketJsonRW(Logger logger) {
    this.logger = logger;
  }

  @Override
  protected JsonAndUpdater tryExtractSystemChat(PacketEvent event) {
    var packet = event.getPacket();
    var jsonString = packet.getStrings().readSafely(0);

    return new JsonAndUpdater(
      jsonString,
      patchedJson -> {
        try {
          packet.getStrings().write(0, patchedJson);
        } catch (Throwable e) {
          logger.log(Level.SEVERE, "An error occurred while trying to write a chat-json-string back", e);
        }
      }
    );
  }

  @Override
  protected @Nullable JsonAndUpdater tryExtractPlayerChat(PacketEvent event) {
    if (!(event.getPacket().getHandle() instanceof ClientboundPlayerChatPacket chatPacket))
      return null;

    var jsonString = IChatBaseComponent.ChatSerializer.a(chatPacket.b().c());

    return new JsonAndUpdater(
      jsonString,
      patchedJson -> {
        var patchedComponent = IChatBaseComponent.ChatSerializer.a(patchedJson);

        if (patchedComponent == null)
          logger.warning("Patched component was not parsable and returned null");

        var patchedPacket = new ClientboundPlayerChatPacket(
          new PlayerChatMessage(
            chatPacket.b().i(),
            chatPacket.b().j(),
            chatPacket.b().k(),
            Optional.ofNullable(patchedComponent),
            chatPacket.b().m()
          ),
          chatPacket.c()
        );

        event.setPacket(new PacketContainer(event.getPacketType(), patchedPacket));
      }
    );
  }
}
