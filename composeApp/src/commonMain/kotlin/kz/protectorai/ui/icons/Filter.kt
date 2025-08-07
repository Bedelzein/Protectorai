package kz.protectorai.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.DefaultFillType
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val filter = KotlinVector {
    ImageVector.Builder(
        name = "filter",
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
            moveTo(4.25f, 5.61f)
            curveTo(6.27f, 8.2f, 10.0f, 13.0f, 10.0f, 13.0f)
            verticalLineToRelative(6.0f)
            curveToRelative(0.0f, 0.55f, 0.45f, 1.0f, 1.0f, 1.0f)
            horizontalLineToRelative(2.0f)
            curveToRelative(0.55f, 0.0f, 1.0f, -0.45f, 1.0f, -1.0f)
            verticalLineToRelative(-6.0f)
            curveToRelative(0.0f, 0.0f, 3.72f, -4.8f, 5.74f, -7.39f)
            curveTo(20.25f, 4.95f, 19.78f, 4.0f, 18.95f, 4.0f)
            horizontalLineTo(5.04f)
            curveTo(4.21f, 4.0f, 3.74f, 4.95f, 4.25f, 5.61f)
            close()
        }
    }.build()
}
