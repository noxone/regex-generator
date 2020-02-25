package org.olafneumann.regex.generator.js

fun createStepDefinition(element: String, title: String, description: String, position: String): dynamic {
    val stepDefinition: dynamic = js("{}")
    stepDefinition.element = element
    stepDefinition.popover = js("{}")
    stepDefinition.popover.title = title
    stepDefinition.popover.description = description
    stepDefinition.popover.position = position
    return stepDefinition
}

external class Popover {     // There will be no popover if empty or not given
    var className: String // 'popover-class', // className to wrap this specific step popover in addition to the general className in Driver options
    var title: String // 'Title',             // Title on the popover
    var description: String // 'Description', // Body of the popover
    var position: String // 'Position', // Position of the popover
    var showButtons: Boolean // false,         // Do not show control buttons in footer
    var doneBtnText: String // 'Done',        // Text on the last button
    var closeBtnText: String // 'Close',      // Text on the close button
    var nextBtnText: String // 'Next',        // Next button text
    var prevBtnText: String // 'Previous',    // Previous button text
}

external class StepDefinition {
    var element: String        // Query selector string or Node to be highlighted
    var stageBackground: String   // This will override the one set in driver
    var popover: Popover
    // var onNext: () => {},             // Called when moving to next step from current step
    // var onPrevious: () => {},         // Called when moving to previous step from current step
};

external class Driver {
    /** Checks if the driver is active or not */
    fun isActivated(): Boolean

    // In case of the steps guide, you can call below methods
    // fun defineSteps(stepDefinitions: Array<StepDefinition>)
    fun defineSteps(stepDefinitions: Array<dynamic>)
    /**Starts driving through the defined steps*/
    fun start()
    fun start(stepNumber: Number)
    fun moveNext();             // Moves to next step in the steps list
    fun movePrevious();         // Moves to previous step in the steps list
    fun hasNextStep();          // Checks if there is next step to move to
    fun hasPreviousStep();      // Checks if there is previous step to move to

/** Prevents the current move. Useful in `onNext` or `onPrevious` if you want to
perform some asynchronous task and manually move to next step */
    fun preventMove();

// Highlights the element using query selector or the step definition
    fun highlight(string: String);
    fun highlight(stepDefinition: StepDefinition)

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