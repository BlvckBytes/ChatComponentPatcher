package me.blvckbytes.chat_component_patcher;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

class MessagePatcher {

  private final JsonObject message;
  private final ServerVersion version;

  private boolean isFirstPart;
  private @Nullable JsonArray insertedExtraParts;

  public MessagePatcher(JsonObject message, ServerVersion version) {
    this.message = message;
    this.version = version;
    this.isFirstPart = true;
  }

  public void appendPart(String part, @Nullable String url) {
    JsonObject container;

    if (isFirstPart) {
      container = message;
    }

    else {
      container = new JsonObject();

      if (insertedExtraParts == null)
        insertedExtraParts = new JsonArray();

      insertedExtraParts.add(container);
    }

    container.addProperty("text", part);

    if (url != null) {
      container.addProperty("underlined", true);

      var clickEvent = new JsonObject();
      clickEvent.addProperty("action", "open_url");

      if (version.compareTo(ServerVersion.V1_21_5) >= 0)
        clickEvent.addProperty("url", url);
      else
        clickEvent.addProperty("value", url);

      if (version.compareTo(ServerVersion.V1_21_5) >= 0)
        container.add("click_event", clickEvent);
      else
        container.add("clickEvent", clickEvent);
    }

    isFirstPart = false;
  }

  public int doneAndGetInsertedCount() {
    if (insertedExtraParts == null)
      return 0;

    var count = insertedExtraParts.size();

    if (message.get("extra") instanceof JsonArray extraArray)
      insertedExtraParts.addAll(extraArray);

    message.add("extra", insertedExtraParts);

    return count;
  }
}
