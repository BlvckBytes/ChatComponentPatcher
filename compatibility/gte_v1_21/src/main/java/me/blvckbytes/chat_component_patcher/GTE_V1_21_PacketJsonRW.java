package me.blvckbytes.chat_component_patcher;

import com.comphenix.protocol.events.PacketEvent;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GTE_V1_21_PacketJsonRW extends PacketJsonRW {

  private final Logger logger;

  public GTE_V1_21_PacketJsonRW(Logger logger) {
    this.logger = logger;
  }

  @Override
  protected JsonAndUpdater tryExtractSystemChat(PacketEvent event) {
    var packet = event.getPacket();
    var chatComponent = packet.getChatComponents().readSafely(0);

    return new JsonAndUpdater(
      chatComponent.getJson(),
      patchedJson -> {
        try {
          chatComponent.setJson(patchedJson);
          packet.getChatComponents().write(0, chatComponent);
        } catch (Throwable e) {
          logger.log(Level.SEVERE, "An error occurred while trying to write a chat-component-wrapper back", e);
        }
      }
    );
  }

  @Override
  protected @Nullable JsonAndUpdater tryExtractPlayerChat(PacketEvent event) {
    var packet = event.getPacket();
    var chatComponent = packet.getChatComponents().readSafely(0);

    if (chatComponent == null)
      return null;

    return new JsonAndUpdater(
      chatComponent.getJson(),
      patchedJson -> {
        try {
          chatComponent.setJson(patchedJson);
          packet.getChatComponents().write(0, chatComponent);
        } catch (Throwable e) {
          logger.log(Level.SEVERE, "An error occurred while trying to write a chat-component-wrapper back", e);
        }
      }
    );
  }
}
