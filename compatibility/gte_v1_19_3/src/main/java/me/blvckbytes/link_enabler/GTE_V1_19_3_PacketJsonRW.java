package me.blvckbytes.link_enabler;

import com.comphenix.protocol.events.PacketEvent;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GTE_V1_19_3_PacketJsonRW extends PacketJsonRW {

  private final Logger logger;

  public GTE_V1_19_3_PacketJsonRW(Logger logger) {
    this.logger = logger;
  }

  @Override
  protected @Nullable JsonAndUpdater tryExtractSystemChat(PacketEvent event) {
    var packet = event.getPacket();
    var jsonString = packet.getStrings().readSafely(0);

    if (jsonString == null)
      return null;

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
