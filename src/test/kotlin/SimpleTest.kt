import org.olafneumann.regex.generator.regex.RecognizerCombiner
import org.olafneumann.regex.generator.regex.RecognizerCombiner.Options
import kotlin.test.Test
import kotlin.test.assertEquals

class SimpleTest {
    @Test
    fun testRegexGeneration() {
        val given = "abc["
        val expected = "abc\\["
        val actual =
            RecognizerCombiner.combineMatches(inputText = given, selectedMatches = emptyList(), options = Options())

        assertEquals(expected, actual.pattern)
    }
}
