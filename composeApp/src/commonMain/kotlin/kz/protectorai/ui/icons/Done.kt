package kz.protectorai.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.DefaultFillType
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val done = KotlinVector {
    ImageVector.Builder(
        name = "done",
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
            moveTo(9.0f, 16.2f)
            lineTo(4.8f, 12.0f)
            lineToRelative(-1.4f, 1.4f)
            lineTo(9.0f, 19.0f)
            lineTo(21.0f, 7.0f)
            lineToRelative(-1.4f, -1.4f)
            lineTo(9.0f, 16.2f)
            close()
        }
    }.build()
}
