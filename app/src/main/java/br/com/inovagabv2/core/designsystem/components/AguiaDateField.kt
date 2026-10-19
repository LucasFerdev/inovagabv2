package br.com.inovagabv2.core.designsystem.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun AguiaDateField(
    digits: String,
    onDigitsChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    error: String? = null
) {
    AguiaTextField(
        value = digits,
        onValueChange = { onDigitsChange(it.filter(Char::isDigit).take(8)) },
        label = label,
        modifier = modifier,
        error = error,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        visualTransformation = DateMaskVisualTransformation
    )
}

object DateMaskVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.filter(Char::isDigit).take(8)
        val formatted = buildString {
            digits.forEachIndexed { index, char ->
                if (index == 2 || index == 4) append('/')
                append(char)
            }
        }
        return TransformedText(
            AnnotatedString(formatted),
            DateMaskOffsetMapping(digits.length)
        )
    }
}

class DateMaskOffsetMapping(private val digitCount: Int) : OffsetMapping {
    override fun originalToTransformed(offset: Int): Int {
        val safe = offset.coerceIn(0, digitCount)
        val transformed = when {
            safe <= 2 -> safe
            safe <= 4 -> safe + 1
            else -> safe + 2
        }
        return transformed.coerceAtMost(visualLength(digitCount))
    }

    override fun transformedToOriginal(offset: Int): Int {
        val safe = offset.coerceIn(0, visualLength(digitCount))
        val original = when {
            safe <= 2 -> safe
            safe <= 5 -> safe - 1
            else -> safe - 2
        }
        return original.coerceIn(0, digitCount)
    }

    companion object {
        fun visualLength(digitCount: Int): Int = when {
            digitCount <= 2 -> digitCount
            digitCount <= 4 -> digitCount + 1
            else -> digitCount + 2
        }
    }
}
