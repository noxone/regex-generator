package org.olafneumann.regex.generator.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class IntRangeTest {
    @Test
    fun testRangeLength1() {
        val range = IntRange(5, 5)
        assertEquals(expected = 1, range.length)
    }

    @Test
    fun testRangeLength2() {
        val range = IntRange(9, 12)

        assertEquals(expected = 4, actual = range.length)
    }

    @Test
    fun testIsContainedAndNotOnEdgesForInt() {
        val range = IntRange(3, 7)

        assertFalse(range.containsAndNotOnEdges(1), "IntRange(3,7) should not contain 1")
        assertFalse(range.containsAndNotOnEdges(2), "IntRange(3,7) should not contain 2")
        assertFalse(range.containsAndNotOnEdges(3), "IntRange(3,7) should not contain 3")
        assertTrue(range.containsAndNotOnEdges(4), "IntRange(3,7) should contain 4")
        assertTrue(range.containsAndNotOnEdges(5), "IntRange(3,7) should contain 5")
        assertTrue(range.containsAndNotOnEdges(6), "IntRange(3,7) should contain 6")
        assertFalse(range.containsAndNotOnEdges(7), "IntRange(3,7) should not contain 7")
        assertFalse(range.containsAndNotOnEdges(8), "IntRange(3,7) should not contain 8")
    }

    @Test
    fun testIsContainedAndNotOnEdgesForRange() {
        val range = IntRange(5, 10)

        assertFalse(range.containsAndNotOnEdges(IntRange(1,2)), "IntRange(5,10) should not contain (1,2)")
        assertFalse(range.containsAndNotOnEdges(IntRange(1,10)), "IntRange(5,10) should not contain (1,10)")
        assertFalse(range.containsAndNotOnEdges(IntRange(5,7)), "IntRange(5,10) should not contain (5,7)")
        assertFalse(range.containsAndNotOnEdges(IntRange(5,10)), "IntRange(5,10) should not contain (5,10)")
        assertFalse(range.containsAndNotOnEdges(IntRange(7,10)), "IntRange(5,10) should not contain (7,10)")
        assertFalse(range.containsAndNotOnEdges(IntRange(7,11)), "IntRange(5,10) should not contain (7,11)")
        assertFalse(range.containsAndNotOnEdges(IntRange(3,12)), "IntRange(5,10) should not contain (3,12)")
        assertFalse(range.containsAndNotOnEdges(IntRange(6,10)), "IntRange(5,10) should not contain (6,10)")
        assertFalse(range.containsAndNotOnEdges(IntRange(15,20)), "IntRange(5,10) should not contain (15,20)")
        assertTrue(range.containsAndNotOnEdges(IntRange(6,9)), "IntRange(5,10) should contain (6,9)")
        assertTrue(range.containsAndNotOnEdges(IntRange(6,7)), "IntRange(5,10) should contain (6,9)")
        assertTrue(range.containsAndNotOnEdges(IntRange(8,9)), "IntRange(5,10) should contain (6,9)")
        assertTrue(range.containsAndNotOnEdges(IntRange(7,8)), "IntRange(5,10) should contain (6,9)")
        assertTrue(range.containsAndNotOnEdges(IntRange(7,9)), "IntRange(5,10) should contain (6,9)")
    }

    @Test
    fun testIntersection() {
        val range1 = IntRange(1, 10)
        val range2 = IntRange(4, 9)
        val range3 = IntRange(2, 3)
        val range4 = IntRange(6, 9)
        val range5 = IntRange(15, 20)

        testIntersections(expected = true, range1, range2)
        testIntersections(expected = true, range1, range3)
        testIntersections(expected = true, range1, range4)
        testIntersections(expected = false, range1, range5)
        testIntersections(expected = true, range2, range1)
        testIntersections(expected = false, range2, range3)
        testIntersections(expected = true, range2, range4)
        testIntersections(expected = false, range2, range5)
        testIntersections(expected = true, range3, range1)
        testIntersections(expected = false, range3, range2)
        testIntersections(expected = false, range3, range4)
        testIntersections(expected = false, range3, range5)
        testIntersections(expected = true, range4, range1)
        testIntersections(expected = true, range4, range2)
        testIntersections(expected = false, range4, range3)
        testIntersections(expected = false, range4, range5)
        testIntersections(expected = false, range5, range1)
        testIntersections(expected = false, range5, range2)
        testIntersections(expected = false, range5, range3)
        testIntersections(expected = false, range5, range4)

        testIntersections(expected = true, range1, range1)
        testIntersections(expected = true, range2, range2)
        testIntersections(expected = true, range3, range3)
        testIntersections(expected = true, range4, range4)
        testIntersections(expected = true, range5, range5)
    }

    private fun testIntersections(expected: Boolean, rangeA: IntRange, rangeB: IntRange) {
        assertEquals(
            expected = expected,
            actual = rangeA.hasIntersectionWith(rangeB),
            message = "Intersection of $rangeA with $rangeB")
        assertEquals(
            expected = expected,
            actual = rangeB.hasIntersectionWith(rangeA),
            message = "Intersection of $rangeB with $rangeA")
    }

    @Test
    fun testIntRangeAdd() {
        testRangeAdd(originalRange = IntRange(5, 10), rangeToAdd = IntRange(1, 1), expected = IntRange(6, 11))
        testRangeAdd(originalRange = IntRange(1, 1), rangeToAdd = IntRange(1, 1), expected = listOf(1..2, 2..2))
        testRangeAdd(originalRange = IntRange(1, 10), rangeToAdd = IntRange(1, 1), expected = listOf(1..11, 2..11))
        testRangeAdd(originalRange = IntRange(5, 10), rangeToAdd = IntRange(1, 4), expected = IntRange(9, 14))
        testRangeAdd(originalRange = IntRange(5, 10), rangeToAdd = IntRange(1, 5), expected = IntRange(10, 15))
        testRangeAdd(originalRange = IntRange(5, 10), rangeToAdd = IntRange(1, 10), expected = IntRange(15, 20))
        testRangeAdd(originalRange = IntRange(5, 10), rangeToAdd = IntRange(1, 20), expected = IntRange(25, 30))
        testRangeAdd(originalRange = IntRange(5, 10), rangeToAdd = IntRange(4, 4), expected = IntRange(6, 11))
        testRangeAdd(originalRange = IntRange(5, 10), rangeToAdd = IntRange(5, 5), expected = listOf(5..11, 6..11))
        testRangeAdd(originalRange = IntRange(5, 10), rangeToAdd = IntRange(5, 7),
            expected = listOf(5..13, 6..13, 7..13, 8..13))
        testRangeAdd(originalRange = IntRange(5, 10), rangeToAdd = IntRange(5, 11),
            expected = listOf(5..17, 6..17, 7..17, 8..17, 9..17, 10..17, 11..17, 12..17))
        testRangeAdd(originalRange = IntRange(5, 10), rangeToAdd = IntRange(6, 6), expected = IntRange(5, 11))
        testRangeAdd(originalRange = IntRange(5, 10), rangeToAdd = IntRange(6, 15), expected = IntRange(5, 20))
        testRangeAdd(originalRange = IntRange(5, 10), rangeToAdd = IntRange(9, 12), expected = IntRange(5, 14))
        testRangeAdd(originalRange = IntRange(5, 10), rangeToAdd = IntRange(10, 10), expected = IntRange(5, 11))
        testRangeAdd(originalRange = IntRange(5, 10), rangeToAdd = IntRange(11, 14),
            expected = listOf(5..10, 5..11, 5..12, 5..13, 5..14))
        testRangeAdd(originalRange = IntRange(5, 10), rangeToAdd = IntRange(12, 12), expected = IntRange(5, 10))
        testRangeAdd(originalRange = IntRange(5, 10), rangeToAdd = IntRange(14, 18), expected = IntRange(5, 10))
    }

    private fun testRangeAdd(expected: IntRange, originalRange: IntRange, rangeToAdd: IntRange) =
        testRangeAdd(expected = listOf(expected), originalRange = originalRange, rangeToAdd = rangeToAdd)

    private fun testRangeAdd(expected: List<IntRange>, originalRange: IntRange, rangeToAdd: IntRange) {
        assertEquals(
            expected = expected,
            actual = originalRange.add(rangeToAdd),
            message = "Adding $rangeToAdd to $originalRange should be $expected")
    }

    @Test
    fun testIntRangeRemove() {
        testRangeRemove(originalRange = IntRange(5, 10), rangeToRemove = IntRange(1, 1), expected = IntRange(4, 9))
        testRangeRemove(originalRange = IntRange(5, 10), rangeToRemove = IntRange(4, 4), expected = IntRange(4, 9))
        testRangeRemove(originalRange = IntRange(5, 10), rangeToRemove = IntRange(5, 5), expected = IntRange(5, 9))
        testRangeRemove(originalRange = IntRange(5, 10), rangeToRemove = IntRange(6, 6), expected = IntRange(5, 9))
        testRangeRemove(originalRange = IntRange(5, 10), rangeToRemove = IntRange(9, 9), expected = IntRange(5, 9))
        testRangeRemove(originalRange = IntRange(5, 10), rangeToRemove = IntRange(10, 10), expected = IntRange(5, 9))
        testRangeRemove(originalRange = IntRange(5, 10), rangeToRemove = IntRange(11, 11), expected = IntRange(5, 10))
        testRangeRemove(originalRange = IntRange(5, 10), rangeToRemove = IntRange(11, 12), expected = IntRange(5, 10))
        testRangeRemove(originalRange = IntRange(5, 10), rangeToRemove = IntRange(3, 12), expected = emptyList())
        testRangeRemove(originalRange = IntRange(5, 10), rangeToRemove = IntRange(3, 6), expected = IntRange(3, 6))
        testRangeRemove(originalRange = IntRange(5, 15), rangeToRemove = IntRange(3, 6), expected = IntRange(3, 11))
        testRangeRemove(originalRange = IntRange(5, 10), rangeToRemove = IntRange(9, 12), expected = IntRange(5, 8))
        testRangeRemove(originalRange = IntRange(5, 10), rangeToRemove = IntRange(6, 9), expected = IntRange(5, 6))
        testRangeRemove(originalRange = IntRange(5, 10), rangeToRemove = IntRange(6, 19), expected = IntRange(5, 5))
        testRangeRemove(originalRange = 1..16, rangeToRemove = 2..3, expected = 1..14)
        testRangeRemove(originalRange = 3..6, rangeToRemove = 4..5, expected = 3..4)
    }

    private fun testRangeRemove(expected: IntRange, originalRange: IntRange, rangeToRemove: IntRange) =
        testRangeRemove(expected = listOf(expected), originalRange = originalRange, rangeToRemove = rangeToRemove)

    private fun testRangeRemove(expected: List<IntRange>, originalRange: IntRange, rangeToRemove: IntRange) {
        assertEquals(
            expected = expected,
            actual = originalRange.remove(rangeToRemove),
            message = "Removing $rangeToRemove from $originalRange should be $expected"
        )
    }
}
