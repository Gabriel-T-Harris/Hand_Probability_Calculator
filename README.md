
# Hand Probability Calculator

## Introduction

Application to calculate the chance of drawing an arbitrary hand from an arbitrary finite deck. All of which is defined at runtime. Obviously without replacement. The calculation is performed through simulation (unfortuantlely due to the mathematics being a bit too complicated for the complicatated expresseions that a user may define).

Pull requests welcome.

## How To Use

//TODO: complete when application is ready for deployment

## Command Line Arguments

The lack of any of the following will result in their corresponding deafult value being used.

### Flags

- --help: show a help message instead of running the rest of the program.
- --error_reporting: to show various error messages not relating to created files about errors. Such as reporting issues with the command line arguments and other errors which does not fall under other groups.
- --verbose: is for all files relating to error logs to be created. As well as for files which show progress of program.
- --scenario_output_flag: outputs all sceanrios in dot file format. Careful with using this as it creates all of files. About equal to all the other output files that the verbose flag would create.

### Parameters

- --input: is where to look for configuration file. If the value is a file, then will read only that one, else if is directory, then will read all files in that directory.
- --output: is where created files will go.
- --hand_size: is starting hand size.
- --test_hands: is the number hands to simulate.

## How To Write Configuration File

The formal specification can be found at [Grammar notes]("parser/Grammar%20notes.txt"). What follows is simply an explanation of it for those who do not understand context free grammar.

Line comments are written with `//`. While block comments are written as `/* */`.

Whitespace characters of various types are allowed in any number between the significant parts.

Both card names and scenario names may be anything except for the following:
- Starts with a non-reserved keyword (case sensative):
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
- They also may not contain at all any of the following char:
    - ;
    - =
    - \n which is a newline character
    - {
    - }
    - \[
    - ]
    - (
    - )
- Thus anything not allowed is allowed to form the name.

### Decklist

The decklist section must start with `deck list:`, followed by `{`. Then at least one card must be provided. Each card name is expected to be followed by a `;`. Once done entering cards, mark the end of the deck with `}`.

Example:
`decklist: {1; B; card; last card;}`

### Scenario

The start of the scenarios section must start with `scenarios:`, followed by `{`. Then at least one sceanrio must be defined. The sceanrios section must end with `}`. Scenarios are defined as a scenario name followed by `=`, followed by `{`, followed by boolean like conditions, then terminated by `;`. Optionally followed by the display part which is (in order): `display`, `=`, either `true` or `false`, terminated by `;`. The specific sceanrio must then be terminated by `}`. And the overall section as a whole is also terminated by `}`.

When a card is matched, it is marked as used so that it will not be considered for subsequent condititions. So that for example 1 card could not furfill the requirement of 2 cards. Operators are also essenitally short circuit evaluation as well.

Boolean conditions are defined as a large expression as follows, note that all leading and trailing space characters are removed:
- match card `[` card name `]`
- match already defined scenario `<` scenario name `>`
- while `(` and `)` are used to group a whole subexpression.

The above conditions form the operands of the following operators:
- `AND` is a simple and statement. Both operands must be true for it to be true.
- `OR` is inclusive or, meaning at least one operand must be true. Thus only the matched operand will take up cards.
- `XOR` is exclusive or, thus only one operand may be true. Thus both operands having the same value will result in an overall false value.
- `NOT` is special. Though `XOR` does similar things. Is the absence of something being true, thus does not use up any cards. As condition operate on what remains, not is very much non-associative. Unlike the other operators which are for the most part.

Regarding opperators they are left to right assoiativitive. Example `[a] AND [b] AND [c]` is equivalent to `([a] AND [b]) AND [c]`. Also `NOT [h] AND [l]` is equivalent to `(NOT [h]) AND [l]`.

Note that a Scenario that does not have a defined display part, will have it default to being true. Said value controls whether or not, it is simulated as its own 'standalone' thing. As the displat part refers to being displayed in the simulation part itself's output.

Examples:

```
//Assume that all cards referenced exist, but they do not have to.

Simple example            = {sceanrio = { [A] }; } //look for only a card called "A"

Silly example of or       = {sceanrio = {[A] OR [A]}; } //Useless or. 

None boolean algebra and  = {sceanrio = {[A] AND [A]}; } //requires that the hand have 2 cards called "A" left.

Equivalent to silly or    = {sceanrio = { <Simple example> OR [A] }; } //note that 1) one can mix cards and sceanrios, and that 2) the trailling spaces in "Simple example             " were removed and thus must be referenced as such.

not example 1            = {sceanrio = {NOT [A] AND [A]}; } //always false

not example 2            = {sceanrio = {[A] AND NOT [A]}; } //can be true

// Key thing about conditions is that it is about what remains. Hence the difference in the above 2 not examples. Though if there is a correct permutation that will lead to true, then it will be found.

/*Will not be simulated on its own.
only want one starter*/
display xor example          = {scenario = {[starter 1] XOR [starter 2]}; display = false;}
```

Note that sceanario names must be unique per file and referenced ones must exist.

## Examples Of Use

Examples of both input files and output files can be found in Input and Output folders respectively. Those are just examples as one can pick both of those locations.

## Additional Notes

The program is written to attempt to salvage poorly written configuration files, however some are unsavable. It is suggested to check error logs to ensure that the configuration file it written properly. Empty files (not the same as no file) indicates no errors for that phase.

Scenario names with none filename friendly char (/, <, >, :, ", \\, |, ?, \*) are discouraged, but allowed. In the event that they are outputted, they will be removed which may cause filename clashes between similarly named sceanrios.

Outputted sceanrios are in dot file format, they can be viewed at the URL shortcut specificed at [Dot File Viewer](https://dreampuf.github.io/GraphvizOnline). Simply copy the generated file's content into the textarea of the viewer.
