# Readable app font proof of concept

This branch adds an optional sans-serif font to RuneLite's desktop UI. It is intended to improve plugin-panel text at fractional display scaling, particularly 150%, while leaving the game's fonts and overlay font settings alone.

## Try it

Build using JDK 17:

```
./gradlew :client:shadowJar
```

Run the shaded JAR, open RuneLite settings, and enable **Window settings > Readable UI font**. Set **Readable UI font size** (12–20, default 14), then restart. Leave launcher/display scaling at your preferred size. The option defaults to off; disabling it and restarting restores the original font.

For a separate test installation, launch with `-Duser.home=<test directory>` before `-jar`. To enable the experiment on its first launch, create `<test directory>/.runelite/settings.properties` containing:

```properties
runelite.readableUiFont=true
runelite.readableUiFontSize=14
```

Use `-Dsun.java2d.uiScale=1.5` before `-jar` to reproduce 150% scaling. `--safe-mode --disable-telemetry` after the JAR disables external plugins/GPU and telemetry for a basic UI smoke test. Game authentication and normal gameplay are not part of this proof of concept's verification.

## Scope

- Standard Swing controls use a sans-serif font rendered at the chosen size, with grayscale antialiasing requested by the theme.
- Plugin-list rows adapt their height. Configuration headings, Plugin Hub headings/metadata, the search clear button, and the plugin-error description use separate app-font helpers.
- Plugin Hub row and footer heights accommodate larger text.
- Existing RuneScape font getters and overlay settings are unchanged.
- Restart is required; this avoids partially updating cached or explicitly assigned fonts in already-created controls.

This is not adjustable panel width or an icon upgrade. Long labels can still truncate in the existing fixed-width panels. Panels that explicitly select their own fonts (including some built-in and third-party panels) may retain those fonts. A broader layout and font migration would be a separate contribution. No claim is made that fractional scaling of existing raster icons is fixed.

## Validation

```
./gradlew :client:test --tests '*ReadableUiFontTest' :client:checkstyleMain :client:checkstyleTest :client:pmdMain :client:shadowJar
```

The regression test checks actual plugin-list label sizes and vertical fit at 12, 14, 16, 18, and 20, bounded invalid sizes, restoration of the original appearance, and unchanged game font identity. The rendering test produces comparisons at 100%, 150%, and 200% in `runelite-client/build/font-preview`. These are offscreen renders of Swing components, not screenshots of a logged-in game.

Before proposing this upstream, discuss scope and configuration naming with maintainers. This branch is a personal experiment, not an upstream pull request.
