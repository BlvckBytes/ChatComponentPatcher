package me.blvckbytes.chat_component_patcher;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

class MessagePatcher {

  private final JsonObject message;
  private boolean isFirstPart;
  private @Nullable JsonArray insertedExtraParts;

  public MessagePatcher(JsonObject message) {
    this.message = message;
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

    // NOTE: "url" and "click_event" are specific to >= 1.21.5
    if (url != null) {
      container.addProperty("underlined", true);

      var clickEvent = new JsonObject();
      clickEvent.addProperty("action", "open_url");
      clickEvent.addProperty("url", url);

      container.add("click_event", clickEvent);
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
