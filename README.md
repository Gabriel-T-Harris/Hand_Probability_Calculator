# Hand Probability Calculator

## Introduction

This application calculates the chance of drawing a particular hand from a given finite deck. Both are defined at runtime. Obviously, hands are drawn without replacement. The calculation is performed through simulation (due to the mathematics requiring a dynamic exponential equation to account for overlapping scenarios).

Pull requests welcome.

## How To Use

### Prerequisites

Are simply at least Java 8 and a configuration file to read. In terms of hardware, the program is written to either run in sequential mode which uses the minimum about of resources or parallel mode. Parallel mode is written to automatically adjust its resource use based on available hardware.

One can simply check that they have Java installed by running the command `java -version` in one's terminal. Of it says something like 1.8... (or higher) then it is probably installed properly. Otherwise go install it, is a rather simple process. 

### How to Run

Simply enter into a terminal `java -jar Hand_Probability_Calculator_V1_GTH_2021.jar`. The JAR can be run from anywhere, just specify its location.

Typically one will also specify some additional arguments, such as both "--input" and "--simulation_results_console". An explanation on them can be found in the Command Line Arguments section.

The format for running with arguments is `java -jar Hand_Probability_Calculator_V1_GTH_2021.jar --arguments -flags`. The combination of both flags and arguments, can be placed in any order.

## Command Line Arguments

The lack of any of the following will result in their corresponding default value being used. Flags take no arguments, but parameters must be followed by an argument. 

### Flags

- --help: shows a help message instead of running the rest of the program.
- --error_reporting: to show various error messages not relating to created files about errors, for example, reporting issues with the command line arguments and other errors that do not fall under other groups.
- --verbose: is for all files relating to error logs to be created. As well as for files that shows the progress of the program.
- --scenario_output_flag: outputs all scenarios in dot file format. Be careful with using this, as it creates a lot of files, about equal to all the other output files that the other flags would create, combined.
- --simulation_results_console: to have the simulation results be output to console instead of in a created file.
- --force_sequential: to force the program to perform the simulation sequentially rather than allowing the program to pick sequential or parallel.

### Parameters

- --input: is where to look for configuration file. If the value is a file, then will read only that one; else if it is a directory, then will read all files in that directory.
- --output: is where created files will go.
- --hand_size: is starting hand size.
- --test_hands: is the number of hands to simulate.

## How To Write Configuration File

The formal specification can be found at [Grammar notes](parser/Grammar%20notes.txt). What follows is simply an explanation of it for those who do not understand context free grammar.

Line comments are written with `//`. While block comments are written as `/* */`.

Whitespace characters of various types are allowed in any number between the significant parts.

Both card names and scenario names may be anything except for the following:
- Starts with a non-reserved keyword (case sensitive):
    - AND
    - OR
    - XOR
    - NOT
    - true
    - false
    - display
    - deck list:
    - scenarios:
    - scenario
- They also may not contain at all any of the following characters:
    - ;
    - =
    - \n which is a newline character
    - {
    - }
    - \[
    - ]
    - (
    - )
- Thus, anything not allowed is allowed to form the name.

### Decklist

The decklist section must start with `deck list:`, followed by `{`. Then at least one card must be provided. Each card name is expected to be followed by a `;`. Once done entering cards, mark the end of the deck with `}`.

Example:
`decklist: {1; B; card; last card;}`

### Scenario

The start of the scenarios section must start with `scenarios:`, followed by `{`. Then at least one scenario must be defined. The scenarios section must end with `}`. Scenarios are defined as a scenario name followed by `=`, followed by `{`, followed by Boolean like conditions, then terminated by `;`. Optionally followed by the display part which is (in order): `display`, `=`, either `true` or `false`, terminated by `;`. The specific scenario must then be terminated by `}`.

When a card is matched, it is marked as used so that it will not be considered for subsequent conditions. So, for example, one card could not fulfill the requirement of two cards. Operators are also essentially short circuit evaluated, as well.

Boolean conditions are defined as a large expression as follows (note that all leading and trailing space characters are removed):
- match card `[` card name `]`
- match already defined scenario `<` scenario name `>`
- while `(` and `)` are used to group a whole subexpression.

The above conditions form the operands of the following operators:
- `AND` is a simple and statement. Both operands must be true for this operator to be true.
- `OR` is inclusive or, meaning at least one operand must be true. Thus, only the matched operand will take up cards.
- `XOR` is exclusive or, thus only one operand may be true. Thus, both operands having the same value will result in an overall false value.
- `NOT` is special, though `XOR` does similar things. It is the absence of something being true, thus it does not use up any cards. As conditions operate on what remains, `NOT` is very much non-associative, unlike the other operators, which are for the most part.

Regarding operators, they are left to right associative. Example `[a] AND [b] AND [c]` is equivalent to `([a] AND [b]) AND [c]`. Also `NOT [h] AND [l]` is equivalent to `(NOT [h]) AND [l]`.

Note that a Scenario that does not have a defined display part will have it default to being true. Said value controls whether or not, it is simulated as its own 'standalone' thing, as the display part refers to being displayed in the simulation part's output.

Examples:

```
//Assume that all cards referenced exist, but they do not have to.

Simple example            = {scenario = { [A] }; } //look for only a card called "A"

Silly example of or       = {scenario = {[A] OR [A]}; } //Useless or. 

None Boolean algebra and  = {scenario = {[A] AND [A]}; } //requires that the hand have two cards called "A" left.

Equivalent to silly or    = {scenario = { <Simple example> OR [A] }; } //note that 1) one can mix cards and scenarios, and that 2) the trailing spaces in "Simple example             " were removed and thus must be referenced as such.

not example 1            = {scenario = {NOT [A] AND [A]}; } //always false

not example 2            = {scenario = {[A] AND NOT [A]}; } //can be true

// Key thing about conditions is that it is about what remains. Hence the difference in the above two `NOT` examples. However, if there is a correct permutation that will lead to true, then it will be found.

/*Will not be simulated on its own.
only want one starter*/
display xor example          = {scenario = {[starter 1] XOR [starter 2]}; display = false;}
```

Note that scenario names must be unique per file and referenced ones must be defined before use.

## Examples Of Use

Examples of both input files and output files can be found in Input and Output folders respectively. Those are just examples, as one can pick both of those locations.

## Additional Notes

The program is written to attempt to salvage poorly written configuration files, however some are unsavable. It is suggested to check error logs to ensure that the configuration file it written properly. Empty files (not the same as no file) indicates no errors for that phase.

Scenario names with none filename friendly char (/, <, >, :, ", \\, |, ?, \*) are discouraged, but allowed. In the event that they are outputted, they will be removed which may cause filename clashes between similarly named scenarios.

Outputted scenarios are in dot file format, they can be viewed at the URL shortcut specified at [Dot File Viewer](https://dreampuf.github.io/GraphvizOnline). Simply copy the generated file's content into the text area of the viewer.

All generated files can be viewed as plain text. As such simply open them with a txt viewer.
