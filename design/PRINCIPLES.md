# Design principles

These are the conventions the template encodes: how an app is built and how it behaves, not
what colour it is. Each app owns its palette in `tokens.json`; the shape of that file and
everything below stays constant across apps.

## One studio, many palettes

- Shape is shared, values are not. Every app has a warm ground, a surface a shade lighter,
  solid ink, one primary, one secondary, and an accent with a darker `accentText` twin for
  copy; which colours fill those slots is the app's decision.
- Same recipes everywhere. The card (surface, hairline border, soft primary-tinted shadow),
  the kicker (uppercase caption in `inkSoft`), the segmented control (capsule track, primary
  pill) and the selection treatment (lift and recede, never a border ring) are the same
  components on iOS, on Android and in every app.
- A serif display face: Georgia on iOS, the platform serif on Android. The nav title is a
  centred inline serif in ink, since the stock large title reads off-brand.
- Light-only, no theme switch; the palette is the brand. iOS pins
  `.preferredColorScheme(.light)`, and Android uses a fixed light colour scheme,
  `forceDarkAllowed=false` in the theme, and `SystemBarStyle.light` in `enableEdgeToEdge`
  (the no-argument call reads the system dark-mode setting and draws white icons on a
  cream ground).

## Tokens, not literals

- Every value exists once. `design/tokens.json` generates `Theme.swift`, `Theme.kt`,
  `colors.xml`, the launch colorset and the launch storyboard, and `DesignTokensTest` fails
  when a generated file disagrees with the source.
- Spacing comes from the scale `xs sm md lg xl xxl`. Any other number in a layout is a
  literal to justify in review.
- Sizes scale with the user. iOS uses Dynamic Type (`relativeTo:`); Android mirrors the iOS
  Large scale point for point in `sp`, so a label is the same size on both phones and both
  honour the font-size setting.
- Solid ink, never opacity for hierarchy. Opacity over a mid-tone ground fails contrast in
  ways a bright monitor hides; `ink` and `inkSoft` are solid tokens.

## Platform-native, design-equivalent

- Shared logic lives in the KMP core; shared design is a token system rendered natively,
  never a pixel copy of one platform on the other. iOS uses HIG navigation and native
  controls; Android uses Compose and Material components restyled with the tokens.
- Real navigation bars on every tab. Hiding them makes a native app feel like a web page.
- No icon packs. `material-icons-extended` is thousands of icons for the handful an app
  needs; draw the missing few on a Canvas.
- Accessibility is part of done: 44pt minimum targets, nothing below 11pt, VoiceOver and
  TalkBack labels on every control, headers marked as headers.

## Honest by design

- Never claim what the data cannot support. Every insight sits behind guardrails (minimum
  logs, minimum per bucket) and returns `null` otherwise, and the UI then shows an empty or
  "keep logging" state.
- Missing data is reported as unknown, never as fine. A reassuring blank is the worst
  failure a wellness or safety app can have.
- Measure, never diagnose. "47 events, your median is 12" is a measurement; "your condition
  is worsening" is a claim, and the sentence that makes an app a medical device.
- Product copy is the author's voice. A person drafts store listings and in-app copy, and
  nothing generated ships as-is.

## Icons and launch

- The app icon is 1024×1024, no alpha, square, no pre-rounded corners. App Store Connect
  rejects icons with an alpha channel, and iOS applies its own mask.
- The launch screen is a storyboard with a centred mark on the brand background. The
  `UILaunchScreen` plist dictionary renders nothing on device, and storyboards cannot read
  asset catalogues, so the mark ships as loose PNGs.
- Android ships an adaptive icon with background, foreground and monochrome layers, all
  vectors, so themed icons on Android 13+ work.
