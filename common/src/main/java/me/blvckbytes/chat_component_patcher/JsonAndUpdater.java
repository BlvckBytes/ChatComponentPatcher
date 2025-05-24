package me.blvckbytes.chat_component_patcher;

import java.util.function.Consumer;

public record JsonAndUpdater(
  String json,
  Consumer<String> updater
) {}
