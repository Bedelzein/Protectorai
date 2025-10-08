package kz.protectorai.ui.icons

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val logo = KotlinVector {
    ImageVector.Builder(
        name = "logo",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 8524.4f,
        viewportHeight = 9002.4f
    ).apply {
        path(
            fill = Brush.linearGradient(
                colorStops = arrayOf(
                    0f to Color.Black,
                    0.5f to Color.Black,
                    1f to Color.Black
                ),
                start = Offset(6837.9f, 11153f),
                end = Offset(1910.7f, -2575.5f)
            )
        ) {
            moveTo(4262.2f, 4262.2f)
            moveToRelative(-893.9f, 0f)
            arcToRelative(893.9f, 893.9f, 0f, isMoreThanHalf = true, isPositiveArc = true, 1787.8f, 0f)
            arcToRelative(893.9f, 893.9f, 0f, isMoreThanHalf = true, isPositiveArc = true, -1787.8f, 0f)
        }
        path(fill = SolidColor(Color(0xFF373435))) {
            moveTo(4262.2f, 1974.1f)
            curveToRelative(1263.7f, 0f, 2288.1f, 1024.4f, 2288.1f, 2288.1f)
            curveToRelative(0f, 1263.7f, -1024.4f, 2288.1f, -2288.1f, 2288.1f)
            curveToRelative(-1263.7f, 0f, -2288.1f, -1024.4f, -2288.1f, -2288.1f)
            curveToRelative(0f, -1263.7f, 1024.4f, -2288.1f, 2288.1f, -2288.1f)
            close()
            moveTo(4262.2f, 2511.4f)
            curveToRelative(966.9f, 0f, 1750.8f, 783.8f, 1750.8f, 1750.8f)
            curveToRelative(0f, 966.9f, -783.8f, 1750.8f, -1750.8f, 1750.8f)
            curveToRelative(-966.9f, 0f, -1750.8f, -783.8f, -1750.8f, -1750.8f)
            curveToRelative(0f, -966.9f, 783.8f, -1750.8f, 1750.8f, -1750.8f)
            close()
        }
        path(
            fill = Brush.linearGradient(
                colorStops = arrayOf(
                    0f to Color.Black,
                    0.8f to Color.Black,
                    1f to Color.Black
                ),
                start = Offset(2143.4f, -1149.7f),
                end = Offset(5897f, 12635.5f)
            )
        ) {
            moveTo(4262.2f, 0f)
            curveToRelative(2353.9f, 0f, 4262.2f, 1908.3f, 4262.2f, 4262.2f)
            curveToRelative(0f, 2353.9f, -1908.3f, 4262.2f, -4262.2f, 4262.2f)
            curveToRelative(-514.8f, 0f, -1008.3f, -91.4f, -1465.1f, -258.7f)
            lineToRelative(0f, 0.1f)
            lineToRelative(-961.8f, 736.7f)
            lineToRelative(0f, -1236.3f)
            lineToRelative(0f, -1228.1f)
            curveToRelative(607f, 647.1f, 1469.7f, 1051.4f, 2426.9f, 1051.4f)
            curveToRelative(1837.6f, 0f, 3327.2f, -1489.6f, 3327.2f, -3327.2f)
            curveToRelative(0f, -1837.6f, -1489.6f, -3327.2f, -3327.2f, -3327.2f)
            curveToRelative(-1837.6f, 0f, -3327.2f, 1489.6f, -3327.2f, 3327.2f)
            lineToRelative(0f, 2663.6f)
            curveToRelative(-584.9f, -729.7f, -935f, -1655.7f, -935f, -2663.6f)
            curveToRelative(0f, -2353.9f, 1908.3f, -4262.2f, 4262.2f, -4262.2f)
            close()
        }
    }.build()
}