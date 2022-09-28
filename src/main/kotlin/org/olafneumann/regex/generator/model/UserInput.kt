package org.olafneumann.regex.generator.model

import dev.andrewbailey.diff.DiffOperation
import dev.andrewbailey.diff.differenceOf

data class UserInput(
    private val userInput: String
) : Model {
    private val characters: List<Character>
    init {
        characters = userInput.toCharArray()
            .mapIndexed { index, c -> Character(character = c, index = index) }
            .toList()
    }

    val userInputString: String
        get() = characters.joinToString(separator = "") { it.character.toString() }

    val size: Int
        get() = characters.size

    fun setUserInput(newInputString: String): Result<UserInput, Change> {
        val oldInput = userInput.toCharArray().toList()
        val newInput = newInputString.toCharArray().toList()

        val differences = differenceOf(original = oldInput, updated = newInput, detectMoves = false)

        return Result(
            model = UserInput(userInput = newInputString),
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
