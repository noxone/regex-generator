package org.olafneumann.regex.generator.model

import dev.andrewbailey.diff.DiffOperation
import dev.andrewbailey.diff.differenceOf

class S1UserInput(
    userInput: String
) : Model {
    private val characters: List<Character>
    init {
        characters = userInput.toCharArray()
            .mapIndexed { index, c -> Character(character = c, index = index) }
            .toList()
    }

    val userString: String
        get() = characters.joinToString(separator = "") { it.character.toString() }

    override val output: String
        get() = userString

    val size: Int
        get() = characters.size

    fun setUserInput(newInputString: String): ModelWithDelta<S1UserInput, Change> {
        val oldInput = characters.map { it.character }.toList()
        val newInput = newInputString.toCharArray().toList()

        val differences = differenceOf(original = oldInput, updated = newInput, detectMoves = false)

        return ModelWithDelta(
            model = S1UserInput(userInput = newInputString),
            change = Change(changes = differences.operations)
        )
    }

    data class Character(
        val character: Char,
        val index: Int
    )

    data class Change(
        val changes: List<DiffOperation<Char>>
    ) : Model.Change
}
