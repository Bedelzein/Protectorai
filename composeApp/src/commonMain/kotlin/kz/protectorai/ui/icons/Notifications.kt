package kz.protectorai.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.DefaultFillType
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val notifications = KotlinVector {
    ImageVector.Builder(
        name = "notifications",
        defaultWidth = MaterialIconDimension.dp,
        defaultHeight = MaterialIconDimension.dp,
        viewportWidth = MaterialIconDimension,
        viewportHeight = MaterialIconDimension
    ).apply {
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 1f,
            stroke = null,
            strokeAlpha = 1f,
            strokeLineWidth = 1f,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Bevel,
            strokeLineMiter = 1f,
            pathFillType = DefaultFillType
        ) {
            moveTo(12.0f, 22.0f)
            curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
            horizontalLineToRelative(-4.0f)
            curveToRelative(0.0f, 1.1f, 0.89f, 2.0f, 2.0f, 2.0f)
            close()
            moveTo(18.0f, 16.0f)
            verticalLineToRelative(-5.0f)
            curveToRelative(0.0f, -3.07f, -1.64f, -5.64f, -4.5f, -6.32f)
            lineTo(13.5f, 4.0f)
            curveToRelative(0.0f, -0.83f, -0.67f, -1.5f, -1.5f, -1.5f)
            reflectiveCurveToRelative(-1.5f, 0.67f, -1.5f, 1.5f)
            verticalLineToRelative(0.68f)
            curveTo(7.63f, 5.36f, 6.0f, 7.92f, 6.0f, 11.0f)
            verticalLineToRelative(5.0f)
            lineToRelative(-2.0f, 2.0f)
            verticalLineToRelative(1.0f)
            horizontalLineToRelative(16.0f)
            verticalLineToRelative(-1.0f)
            lineToRelative(-2.0f, -2.0f)
            close()
        }
    }.build()
}