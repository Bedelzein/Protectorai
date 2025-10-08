package kz.protectorai.navigation.feed

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import kz.protectorai.core.EMPTY_STRING

const val DATE_CHAR_LENGTH = 6

fun dateFilter(annotatedText: AnnotatedString): TransformedText {
    val trimmed = if (annotatedText.text.length >= DATE_CHAR_LENGTH) {
        annotatedText.text.substring(0 until DATE_CHAR_LENGTH)
    } else {
        annotatedText.text
    }
    var out = EMPTY_STRING
    for (i in trimmed.indices) {
        out += trimmed[i]
        if (i % 2 == 1 && i < 4) out += '/'
    }

    val numberOffsetTranslator = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int = when {
            offset <= 1 -> offset
            offset <= 3 -> offset + 1
            offset <= DATE_CHAR_LENGTH -> offset + 2
            else -> DATE_CHAR_LENGTH + 2
        }

        override fun transformedToOriginal(offset: Int): Int = when {
            offset <= 2 -> offset
            offset <= 5 -> offset - 1
            offset <= DATE_CHAR_LENGTH + 2 -> offset - 2
            else -> DATE_CHAR_LENGTH
        }
    }

    return TransformedText(AnnotatedString(out), numberOffsetTranslator)
}