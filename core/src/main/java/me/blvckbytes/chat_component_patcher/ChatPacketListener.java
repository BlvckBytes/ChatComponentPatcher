package me.blvckbytes.chat_component_patcher;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.google.gson.*;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatPacketListener extends PacketAdapter {

  private static final Gson GSON_INSTANCE = new GsonBuilder().create();

  private final ServerVersion version;
  private final PacketJsonRW jsonRW;
  private final Logger logger;

  public ChatPacketListener(Plugin plugin, ServerVersion version, PacketJsonRW jsonRW, Logger logger) {
    super(
      plugin,
      ListenerPriority.HIGHEST,
      PacketType.Play.Server.SYSTEM_CHAT,
      PacketType.Play.Server.CHAT
    );

    this.version = version;
    this.jsonRW = jsonRW;
    this.logger = logger;
  }

  private boolean patchJsonMessage(JsonObject message) {
    var hasBeenPatched = false;
    var insertedExtraCount = 0;

    if (message.get("text") instanceof JsonPrimitive textPrimitive) {
      var textContents = textPrimitive.getAsString();
      var linkMatcher = LinkParser.matchLinks(textContents);

      var patcher = new MessagePatcher(message, version);
      var lastLinkEnd = 0;

      while (linkMatcher.find()) {
        var linkStart = linkMatcher.start();
        var linkEnd = linkMatcher.end();

        if (linkStart > lastLinkEnd) {
          var linkLeadingRemainder = textContents.substring(lastLinkEnd, linkStart);
          patcher.appendPart(linkLeadingRemainder, null);
        }

        var linkText = textContents.substring(linkStart, linkEnd);
        var parsedLinkText = LinkParser.tryParseLink(textContents, linkMatcher);

        patcher.appendPart(linkText, parsedLinkText);

        lastLinkEnd = linkEnd;
      }

      if (lastLinkEnd != 0) {
        hasBeenPatched = true;

        if (lastLinkEnd < textContents.length()) {
          var linkTrailingRemainder = textContents.substring(lastLinkEnd);
          patcher.appendPart(linkTrailingRemainder, null);
        }
      }

      insertedExtraCount = patcher.doneAndGetInsertedCount();
    }

    if (message.get("extra") instanceof JsonArray extraArray) {
      for (var i = insertedExtraCount; i < extraArray.size(); ++i) {
        var extraItem = extraArray.get(i);

        if (extraItem instanceof JsonObject extraMessage) {
          hasBeenPatched |= patchJsonMessage(extraMessage);
          continue;
        }

        // Messages devoid of any style will simply be appended as a plain string by Minecraft
        if (extraItem instanceof JsonPrimitive extraPlain) {
          var plainObject = new JsonObject();
          plainObject.add("text", extraPlain);

          // Nothing to do - leave as is
          if (!patchJsonMessage(plainObject))
            continue;

          hasBeenPatched = true;
          extraArray.set(i, plainObject);
        }
      }
    }

    return hasBeenPatched;
  }

  private @Nullable String patchRawJsonMessage(String rawJson) {
    var jsonElement = GSON_INSTANCE.fromJson(rawJson, JsonElement.class);

    if (!(jsonElement instanceof JsonObject jsonObject))
      return null;

    if (!patchJsonMessage(jsonObject))
      return null;

    return GSON_INSTANCE.toJson(jsonObject);
  }

  @Override
  public void onPacketSending(PacketEvent event) {
    try {
      var jsonAndUpdater = jsonRW.tryExtract(event);

      if (jsonAndUpdater == null) {
        logger.warning("Could not extract json from " + event.getPacketType().name() + "-packet");
        return;
      }

      var patchedJson = patchRawJsonMessage(jsonAndUpdater.json());

      if (patchedJson == null)
        return;

      jsonAndUpdater.updater().accept(patchedJson);
    } catch (Throwable e) {
      logger.log(Level.SEVERE, "An error occurred while trying to patch a chat packet", e);
    }
  }

  @Override
  public void onPacketReceiving(PacketEvent event) {}
}
