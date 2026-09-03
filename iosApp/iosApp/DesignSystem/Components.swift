import SwiftUI

/*
 * The house components, each with a one-to-one Android counterpart in Components.kt:
 *
 *   Kicker           ↔ Kicker
 *   section(_:)      ↔ Section
 *   View.appCard     ↔ Modifier.appCard
 *   View.selectionMark ↔ Modifier.selectionLift + SelectionBadge
 *   SegmentedRow     ↔ SegmentedRow
 *   View.appTitle    ↔ AppTopBar
 *
 * Every dimension comes from Theme.swift (generated from design/tokens.json).
 */

extension Font {
    /// The display serif. `relativeTo` makes it scale with the user's Dynamic Type setting.
    static func appSerif(_ size: CGFloat) -> Font {
        .custom(Typography.displayFamily, size: size, relativeTo: .largeTitle)
    }
}

/// The uppercase section label, one style across every screen.
struct Kicker: View {
    private let text: String
    init(_ text: String) { self.text = text }
    var body: some View {
        Text(text)
            .font(.caption).fontWeight(.semibold)
            .foregroundStyle(Theme.inkSoft)
            .textCase(.uppercase)
            .accessibilityAddTraits(.isHeader)
    }
}

/// One choice in a `SegmentedRow`.
struct SegmentOption<T: Hashable>: Identifiable {
    let value: T
    let label: String
    var id: T { value }

    init(_ value: T, _ label: String) {
        self.value = value
        self.label = label
    }
}

/// The app's segmented control: a capsule track with the primary pill sliding to the choice.
/// Replaces the system segmented style, which reads off-brand.
struct SegmentedRow<T: Hashable>: View {
    let options: [SegmentOption<T>]
    @Binding var selection: T
    @Namespace private var pill

    var body: some View {
        HStack(spacing: Control.trackInset) {
            ForEach(options) { option in
                let selected = option.value == selection
                Button {
                    selection = option.value
                } label: {
                    Text(option.label)
                        .font(.subheadline).fontWeight(.semibold)
                        .frame(maxWidth: .infinity, minHeight: Control.height - Control.trackInset * 2)
                        .background {
                            if selected {
                                Capsule()
                                    .fill(Theme.primary)
                                    .matchedGeometryEffect(id: "segmentPill", in: pill)
                                    .shadow(color: Theme.primary.opacity(0.3), radius: 6, y: 2)
                            }
                        }
                        .foregroundStyle(selected ? Theme.surface : Theme.inkSoft)
                        .contentShape(Capsule())
                }
                .buttonStyle(.plain)
                .accessibilityAddTraits(selected ? [.isSelected, .isButton] : .isButton)
            }
        }
        .padding(Control.trackInset)
        .background(Theme.surface, in: Capsule())
        .animation(.spring(response: 0.3, dampingFraction: 0.75), value: selection)
    }
}

/// The check badge that marks a chosen option.
struct SelectionBadge: View {
    var body: some View {
        Image(systemName: "checkmark.circle.fill")
            .font(.system(size: 18))
            .symbolRenderingMode(.palette)
            .foregroundStyle(.white, Theme.primary)
            .background(Circle().fill(Theme.background).padding(2))
    }
}

extension View {
    /// One selected-state treatment for every picker: a badge, a lift, and unselected
    /// options receding.
    func selectionMark(_ selected: Bool, badgePadding: CGFloat = 0) -> some View {
        self
            .overlay(alignment: .bottomTrailing) {
                if selected {
                    SelectionBadge()
                        .padding(badgePadding)
                        .transition(.scale.combined(with: .opacity))
                }
            }
            .scaleEffect(selected ? Selection.scale : 1.0)
            .opacity(selected ? 1 : Selection.recededAlpha)
            .shadow(color: selected ? Theme.primary.opacity(0.24) : .clear, radius: 8, y: 3)
    }

    /// The house card: surface, hairline border, soft primary-tinted shadow.
    func appCard(padding: CGFloat = CardStyle.padding, radius: CGFloat = CardStyle.radius) -> some View {
        self
            .padding(padding)
            .frame(maxWidth: .infinity)
            .background(Theme.surface, in: RoundedRectangle(cornerRadius: radius))
            .overlay(RoundedRectangle(cornerRadius: radius).stroke(Theme.inkSoft.opacity(CardStyle.borderAlpha)))
            .shadow(color: Theme.primary.opacity(CardStyle.shadowAlpha),
                    radius: CardStyle.shadowRadius, x: 0, y: CardStyle.shadowY)
    }

    /// The navigation title: centred display serif in ink, matching the Android top bar
    /// (not the stock large title, which reads off-brand).
    func appTitle(_ title: String) -> some View {
        navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .principal) {
                    Text(title)
                        .font(.appSerif(Typography.navTitleSize))
                        .foregroundStyle(Theme.ink)
                        .lineLimit(1)
                        .accessibilityAddTraits(.isHeader)
                }
            }
    }

    /// A kicker over its content, the counterpart of Android's `Section`.
    func section(_ title: String) -> some View {
        VStack(alignment: .leading, spacing: Spacing.sm) {
            Kicker(title)
            self
        }
    }
}
