package kz.protectorai.ui.icons

import androidx.compose.ui.graphics.vector.ImageVector

class CachedVector(kotlinVector: KotlinVector) : KotlinVector by kotlinVector {
    private var cache: ImageVector? = null

    operator fun invoke(): ImageVector = cache ?: build().also { cache = it }
}