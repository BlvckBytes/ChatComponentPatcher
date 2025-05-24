package me.blvckbytes.link_enabler;

import java.util.function.Consumer;

public record JsonAndUpdater(
  String json,
  Consumer<String> updater
) {}
