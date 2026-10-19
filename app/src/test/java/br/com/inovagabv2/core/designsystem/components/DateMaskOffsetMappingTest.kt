package br.com.inovagabv2.core.designsystem.components

import androidx.compose.ui.text.AnnotatedString
import org.junit.Assert.assertEquals
import org.junit.Test

class DateMaskOffsetMappingTest {

    @Test
    fun `offset mapping round trips for zero to eight digits`() {
        val sample = "20102026"
        for (count in 0..8) {
            val mapping = DateMaskOffsetMapping(count)
            for (offset in 0..count) {
                val transformed = mapping.originalToTransformed(offset)
                assertEquals(offset, mapping.transformedToOriginal(transformed))
            }
            assertEquals(0, mapping.originalToTransformed(0))
            assertEquals(
                DateMaskOffsetMapping.visualLength(count),
                mapping.originalToTransformed(count)
            )
            val formatted = DateMaskVisualTransformation.filter(AnnotatedString(sample.take(count)))
            assertEquals(expectedMask(sample.take(count)), formatted.text.text)
        }
    }

    @Test
    fun `cursor stays at end middle and slash boundaries`() {
        val mapping = DateMaskOffsetMapping(8)
        assertEquals(10, mapping.originalToTransformed(8))
        assertEquals(8, mapping.transformedToOriginal(10))
        assertEquals(2, mapping.originalToTransformed(2))
        assertEquals(5, mapping.originalToTransformed(4))
        assertEquals(4, mapping.transformedToOriginal(5))
        assertEquals(2, mapping.transformedToOriginal(3))
    }

    private fun expectedMask(digits: String): String = buildString {
        digits.forEachIndexed { index, char ->
            if (index == 2 || index == 4) append('/')
            append(char)
        }
    }
}
