package org.olafneumann.regex.generator.js

data class StepDefinition(
    val element: String,
    val popover: DriverPopover
)

data class DriverPopover(
    val title: String,
    val description: String,
    val position: String
)

fun Driver.defineSteps(stepDefinitions: List<StepDefinition>) =
    defineSteps(stepDefinitions.map { it.asDynamic() }.toTypedArray())

@Suppress("UnusedPrivateMember", "TooManyFunctions")
@JsModule("driver.js")
external class Driver(options: dynamic) {
    /** Checks if the driver is active or not */
    fun isActivated(): Boolean

    // In case of the steps guide, you can call below methods
    // fun defineSteps(stepDefinitions: Array<StepDefinition>)
    fun defineSteps(stepDefinitions: Array<dynamic>)
    /**Starts driving through the defined steps*/
    fun start(stepNumber: Number = definedExternally)
    fun moveNext();             // Moves to next step in the steps list
    fun movePrevious();         // Moves to previous step in the steps list
    fun hasNextStep();          // Checks if there is next step to move to
    fun hasPreviousStep();      // Checks if there is previous step to move to

/** Prevents the current move. Useful in `onNext` or `onPrevious` if you want to
perform some asynchronous task and manually move to next step */
    fun preventMove();

// Highlights the element using query selector or the step definition
    fun highlight(string: String);
    fun highlight(stepDefinition: dynamic)

/** Reposition the popover and highlighted element */
    fun refresh();

/** Resets the overlay and clears the screen */
    fun reset();

// Additionally you can pass a boolean parameter
// to clear immediately and not do the animations etc
// Could be useful when you, let's say, want to run
// a different instance of driver while one was running
    fun reset(clearImmediately: Boolean);

// Checks if there is any highlighted element
    fun hasHighlightedElement(): Boolean

// Gets the currently highlighted element on screen
// It would be an instance of `/src/core/element.js`
    // fun getHighlightedElement(): Node

// Gets the last highlighted element, would be an instance of `/src/core/element.js`
    // fun getLastHighlightedElement(): Node
/*
    activeElement.getCalculatedPosition(); // Gets screen co-ordinates of the active element
    activeElement.hidePopover();           // Hide the popover
    activeElement.showPopover();           // Show the popover

    activeElement.getNode();  // Gets the DOM Element behind this element
 */
}
