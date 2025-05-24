# ChatComponentPatcher

Since Minecraft version `1.19.1` and upwards, links sent via chat-packets (be they of player- or system-origin) are no longer automatically rendered as clickable by the client; thus, the only solution to bring said behaviour back is to locate link-like texts and encapsulate them within a separate component, to then add an `open_url` click-event explicitly.

This rather concise plugin does so by manipulating the component's raw JSON, using the native serializer/deserializer, as to avoid a loss of information when using alternative parsers like md_5's.