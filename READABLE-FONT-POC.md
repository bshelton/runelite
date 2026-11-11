# App interface font

RuneLite > Window settings > App interface font uses the existing font picker: built-in, installed, and custom font families; size; bold; and italic. Changes apply immediately to app controls and built-in plugin panels. Overlay font settings remain separate.

The default is RuneScape, size 16, with bold and italic off. That exact selection preserves the original regular, heading, and small fonts and restores the original control heights. The previous prototype checkbox and size settings are replaced by appInterfaceFont; old prototype keys are no longer used.

AppFontManager tracks panel roots and explicit font roles with weak references, including hidden and detached controls. Changes run on the Swing event thread. Existing controls retain their UI delegates and plugin state. Layout callbacks recalculate cached heights after fonts are assigned. Game fonts and text-rendering policy remain unchanged. Third-party controls with explicit fonts do not necessarily follow this setting.

Build: ./gradlew :client:shadowJar

Validation: ./gradlew :client:test --tests '*ReadableUiFontTest' :client:checkstyleMain :client:checkstyleTest :client:pmdMain

Tests cover live family/style/size changes, hidden and newly created controls, original-font restoration, explicit plugin font preservation, shadow UI preservation, game font identity, and row/progress heights. Windows smoke testing verifies the picker and existing plugin panels update without restart. Fixed panel widths still limit long labels and very large font choices. Cross-platform visual verification remains outstanding. No typography enforcement rule is added.
