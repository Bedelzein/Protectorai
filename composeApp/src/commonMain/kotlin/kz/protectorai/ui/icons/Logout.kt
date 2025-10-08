package kz.protectorai.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.DefaultFillType
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val logout = KotlinVector {
    ImageVector.Builder(
        name = "logout",
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
            moveTo(17.0f, 7.0f)
            lineToRelative(-1.41f, 1.41f)
            lineTo(18.17f, 11.0f)
            horizontalLineTo(8.0f)
            verticalLineToRelative(2.0f)
            horizontalLineToRelative(10.17f)
            lineToRelative(-2.58f, 2.58f)
            lineTo(17.0f, 17.0f)
            lineToRelative(5.0f, -5.0f)
            close()
            moveTo(4.0f, 5.0f)
            horizontalLineToRelative(8.0f)
            verticalLineTo(3.0f)
            horizontalLineTo(4.0f)
            curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
            verticalLineToRelative(14.0f)
            curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
            horizontalLineToRelative(8.0f)
            verticalLineToRelative(-2.0f)
            horizontalLineTo(4.0f)
            verticalLineTo(5.0f)
            close()
        }
    }.build()
}