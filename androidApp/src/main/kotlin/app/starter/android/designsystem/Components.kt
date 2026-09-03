package app.starter.android.designsystem

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/*
 * The house components, named after their iOS counterparts in Components.swift:
 *
 *   Kicker          ↔ Kicker
 *   Section         ↔ section(_:)
 *   Modifier.appCard      ↔ View.appCard
 *   Modifier.selectionLift + SelectionBadge ↔ View.selectionMark
 *   SegmentedRow    ↔ SegmentedRow
 *
 * Every dimension comes from Theme.kt (generated from design/tokens.json).
 */

/** The uppercase section label, one style across every screen. */
@Composable
fun Kicker(text: String, modifier: Modifier = Modifier) {
    Text(
        text.uppercase(),
        modifier,
        color = Theme.inkSoft,
        style = Type.caption,
        fontWeight = FontWeight.SemiBold,
    )
}

/** A kicker over its content (the iOS `section(_:)` helper). */
@Composable
fun Section(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        Kicker(title)
        content()
    }
}

/**
 * The house card: surface, hairline border, soft primary-tinted shadow.
 * The shadow must be drawn before the background or it lands on top of it.
 */
fun Modifier.appCard(padding: Dp = CardStyle.padding, radius: Dp = CardStyle.radius): Modifier {
    val shape = RoundedCornerShape(radius)
    return this
        .fillMaxWidth()
        .shadow(elevation = CardStyle.elevation, shape = shape, ambientColor = Theme.primary, spotColor = Theme.primary)
        .background(Theme.surface, shape)
        .border(1.dp, Theme.inkSoft.copy(alpha = CardStyle.borderAlpha), shape)
        .padding(padding)
}

/**
 * Selection as on iOS: the chosen option lifts and stays opaque while the others recede.
 * No border ring.
 */
@Composable
fun Modifier.selectionLift(selected: Boolean): Modifier {
    val scale by animateFloatAsState(
        targetValue = if (selected) Selection.scale else 1f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 380f),
        label = "selectionScale",
    )
    val fade by animateFloatAsState(
        targetValue = if (selected) 1f else Selection.recededAlpha,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 380f),
        label = "selectionAlpha",
    )
    return this
        .semantics { this.selected = selected }
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
            alpha = fade
        }
}

/** The check badge marking a chosen option. Place inside a Box. */
@Composable
fun BoxScope.SelectionBadge(padding: Dp = 0.dp) {
    Box(
        Modifier
            .align(Alignment.BottomEnd)
            .padding(padding)
            .size(20.dp)
            .clip(CircleShape)
            .background(Theme.background)
            .padding(2.dp)
            .clip(CircleShape)
            .background(Theme.primary),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.size(10.dp)) { drawCheck(Theme.surface) }
    }
}

private fun DrawScope.drawCheck(color: Color) {
    val w = size.width
    val h = size.height
    val path = Path().apply {
        moveTo(w * 0.1f, h * 0.55f)
        lineTo(w * 0.4f, h * 0.85f)
        lineTo(w * 0.9f, h * 0.2f)
    }
    drawPath(path, color, style = Stroke(width = w * 0.2f, cap = StrokeCap.Round, join = StrokeJoin.Round))
}

/**
 * The app's segmented control: a capsule track with the primary pill on the choice, as on
 * iOS. Material's outlined segmented button is not used.
 */
@Composable
fun <T> SegmentedRow(
    options: List<Pair<T, String>>,
    selected: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Control.pillRadius + Control.trackInset))
            .background(Theme.surface)
            .padding(Control.trackInset),
        horizontalArrangement = Arrangement.spacedBy(Control.trackInset),
    ) {
        options.forEach { (value, label) ->
            val isOn = value == selected
            Box(
                Modifier
                    .weight(1f)
                    .height(Control.height)
                    .clip(RoundedCornerShape(Control.pillRadius))
                    .background(if (isOn) Theme.primary else Color.Transparent)
                    .clickable(role = Role.Tab) { onSelect(value) }
                    .semantics { this.selected = isOn },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    label,
                    color = if (isOn) Theme.surface else Theme.inkSoft,
                    style = Type.subheadline,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}
