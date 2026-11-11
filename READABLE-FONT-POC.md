# Optional interface font

Window settings > Sans-serif interface font selects Java's logical SansSerif font for desktop controls. The option defaults to off. Interface font size applies only when enabled (12–20, default 14, before display scaling). Restart after changing either setting.

Built-in plugin panels inherit the theme font or use the UI font helpers. Game fonts, overlays, text shadows, and the existing GASP antialiasing policy are preserved. Third-party panels with explicit fonts may not follow the setting. Fixed panel widths can still truncate text.

The configuration keys remain readableUiFont and readableUiFontSize for compatibility with earlier prototype profiles. Enabling the option initializes the font before plugins construct their controls; disabling it preserves the original theme initialization order.

Build with JDK 17 using ./gradlew :client:shadowJar. Targeted validation: ./gradlew :client:test --tests '*ReadableUiFontTest' :client:checkstyleMain :client:checkstyleTest :client:pmdMain.

Tests cover font selection, size bounds, plugin-list and progress-label vertical fit, original defaults, unchanged game font identity, and preservation of the antialiasing policy. This does not establish every panel layout at every size or cross-platform visual equivalence. No typography enforcement rule is included.
