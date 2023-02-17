# Hand Probability Calculator

## Introduction

This application calculates the chance of drawing a particular hand from a given finite deck. Both are defined at runtime. Obviously, hands are drawn without replacement. The calculation is performed through simulation (due to the mathematics requiring a dynamic exponential equation to account for overlapping scenarios).

Releases found at https://github.com/Gabriel-T-Harris/Hand_Probability_Calculator/releases.

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
- --error_reporting_off: is to not show various error messages not relating to created files about errors, for example, reporting issues with the command line arguments and other errors that do not fall under other groups.
- --verbose_off: is to not output all files relating to error logs to be created. As well as for files that shows the progress of the program.
- --scenario_output_flag: outputs all scenarios in dot file format. Be careful with using this, as it creates a lot of files, about equal to all the other output files that the other flags would create, combined.
- --simulation_results_console: to have the simulation results be output to console instead of in a created file.
- --force_sequential: to force the program to perform the simulation sequentially rather than allowing the program to pick sequential or parallel.
- --display_progress_off: turns off displaying simulation progress, which speeds up the program.                                   

### Parameters

- --input: is where to look for configuration file. If the value is a file, then will read only that one; else if it is a directory, then will read all files in that directory. Careful about trailing slashes, or surround it with quotation marks.
- --output: is where created files will go. Careful about trailing slashes, or surround it with quotation marks.
- --hand_size: is starting hand size.
- --test_hands: is the number of hands to simulate.

## How To Write Configuration File

The formal specification can be found at [Grammar notes](parser/Grammar%20notes.txt). What follows is simply an explanation of it for those who do not understand context free grammar.

Line comments are written with `//`. While block comments are written as `/* */`.

Whitespace characters of various types are allowed in any number between the significant parts.

There are also examples of everything mentioned.

Additionally, the file must have have the following sections written in the following order decklist, special abilities (optional, can be completely omitted), then scenarios. They may not be reordered.

Both card names and scenario names may be anything except for the following:
- Starts with a non-reserved keyword (case sensitive):
    - AND
    - OR
    - XOR
    - NOT
    - COMBINATORIC
    - true
    - false
    - display
    - decklist:
    - scenarios:
    - scenario
    - special_abilities:
    - special_ability
    - DRAW
    - BANISH
    - REASONING
    - uses
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
    - *
    - #
    - ~
- Thus, anything not mentioned is allowed to form the name.

### Decklist

This section is simply the decklist that one will be using for scenarios.

The decklist section must start with `decklist:`, followed by `{`. Then at least one card must be provided. Each card name is expected to be followed by a `;`. Once done entering cards, mark the end of the deck with `}`.

Example:
`decklist: {Number12334; B; card; last card;}`

### Special Abilities

Special abilities are cards which do things. They are carried out before checking what scenarios match, after one's hand is drawn. The idea is to closer represent probability. As if one for example has a card which say draws 2 cards, then resolving it would result with a larger hand, which would of course increase the probability of scenarios matching. Thus completely ignoring it would result in the card being treated as doing nothing and adding nothing (which would be contradictory with reality).

The start of this section is `special_abilities:` followed by a `{`. This section is terminated with a `}`. This section is also completely optional, thus it can be completely omitted. One can also define such for cards not in deck (though that would be a waste of processing time). Like the other sections, one can essentially define any number of items in it. However one of course may not define the same thing twice. Each time after the first will be thrown out.

The syntax for defining what abilities a card has is as follows: `card_name = {special_ability = {SPECIAL_ABILITY_ACTION;...}; ACTIVATION_LIMITATION}`. "card_name" is of course the name of the card which will have said abilities, which should match a card in the given decklist (note that trailing white spaces are removed). "ACTIVATION_LIMITATION" is optional to include. If "ACTIVATION_LIMITATION" is omitted, then it will be treated as having no limitation (setting the limit to 0 is the same as no limit). The syntax for "ACTIVATION_LIMITATION" is simple `uses = non_negative_number;` (non_negative_number must be an actual number). Activation limit is the number of times a card should be processed successfully. A card that cannot be activated will not count towards the limit. For example, if one has 10 cards in deck, one cannot activate a card that says draw 20 cards.

All defined cards must have at least 1 special ability (essentially no upper limit). The abilities are executed sequentially in the order they appear from left to right. Abilities are defined in terms of `SPECIAL_ABILITY_ACTION`. A card must be able to perform all listed actions to be considered activatable. Each has its own syntax, they are as follows:

- `DRAW`, `MILL`, and `BANISH` are rather similar keywords so they will be explained together. The syntax is simply `keyword non_negative_integer` (keyword is `DRAW`, `MILL`, or `BANISH`; non_negative_integer is integer > -1, while 0 is legal, it will not be meaningful). The only difference between them is where the cards on the top of the deck go. `DRAW` result in them going to hand, `MILL` means they go to graveyard, and `BANISH` results in them bring banished. While one can have like `DRAW 1; DRAW 1;` for technical reasons it is advised to instead group them as a single action which would be `DRAW 2;` in this case (though both are equivalent in meaning).
- `REASONING`, mills cards from the top of the deck until a specified stopping point is reached. At which point said stopping point is put onto the field. In the event that no stopping point is found, than no cards are mill and card with this action will be treated as not activatable. The syntax is `REASONING {decklist}`, in which "decklist" follows the same format as stated in the decklist section.

Warning, as special abilities are a new feature in version 2, they are currently activated in effectively an arbitrary order and will activate them if able. Though cards drawn will be checked for special abilities. Also, after successfully resolving them, they go the graveyard location (may be definable in future version).

### Scenario

Scenarios are what one is looking for. Thus such is one is looking to know the odds of specified scenarios happening.

The start of the scenarios section must start with `scenarios:`, followed by `{`. Then at least one scenario must be defined. The scenarios section must end with `}`. Scenarios are defined as a scenario name followed by `=`, followed by `{`, followed by Boolean like conditions, then terminated by `;`. Optionally followed by the display part which is (in order): `display`, `=`, either `true` or `false`, terminated by `;`. The specific scenario must then be terminated by `}`.

When a card is matched, it is marked as used so that it will not be considered for subsequent conditions. So, for example, one card could not fulfill the requirement of two cards. Operators are also essentially short circuit evaluated, as well. For the most part, one will be looking to match cards in hand, but one may choose other locations to look at. Note that cards start in hand and can only end up in another location as a result of a special ability.

Boolean conditions are defined as a large expression as follows (note that all leading and trailing space characters are removed):
- match card `[` card name `]` in hand
- match card `*` card name `*` on field
- match card `#` card name `#` in graveyard
- match card `~` card name `~` that is banished
- match already defined scenario `<` scenario name `>`
- while `(` and `)` are used to group a whole subexpression.

The above conditions form the operands of the following operators:
- `AND` is a simple and statement. Both operands must be true for this operator to be true.
- `OR` is inclusive or, meaning at least one operand must be true. Thus, only the matched operand will take up cards.
- `XOR` is exclusive or, thus only one operand may be true. Thus, both operands having the same value will result in an overall false value.
- `NOT` is special, though `XOR` does similar things. It is the absence of something being true, thus it does not use up any cards. As conditions operate on what remains, `NOT` is very much non-associative, unlike the other operators, which are for the most part.
- `COMBINATORIC` is a kin to a normal mathematical combinatorial (the following is mainly a description of what that is). It is also a single compound expression. Essentially it represents an or of ands. It is required that the number that one is choosing is smaller than the number options available. Additionally, while the number (integer) chosen must be non-negative, there is no point in using it with a value of 1. It also assumes that what is being chosen from are all unique. It also has the following special syntax (refer to grammar for an algebraic explanation), `COMBINATORIC {non_negative_integer; EXPR; EXPR; EXPR;...}`. In which non_negative_integer is to be replaced with an actual integer. And "EXPR" is essentially any expression. Every scenario defines an expression and the operands of operators are expressions as well. Lastly the ellipse represents that one can optionally include more expressions to choose from. The minimum number of expressions is required to be 3 (as shown). This operator is purely for convenience, as such it can be ignored.

Regarding operators, they are left to right associative. Example `[a] AND [b] AND [c]` is equivalent to `([a] AND [b]) AND [c]`. Also `NOT [h] AND [l]` is equivalent to `(NOT [h]) AND [l]`.

Note that a Scenario that does not have a defined display part will have it default to being true. Said value controls whether or not, it is simulated as its own 'stand alone' thing, as the display part refers to being displayed in the simulation part's output.

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

//While not shown, one could have a combinatorial in another combinatorial. Also as combinatorial is one 'piece', one could have something like 'combinatorial OR [D]' and so on.
combinatorial example = {scenario = { COMBINATORIC {2; [A]; [B]; [C];} };}

//equivalent to combinatorial example
expanded combinatorial example = {scenario = {([A] AND [B]) OR ([A] AND [C]) OR ([B] AND [C])};}
```

Note that scenario names must be unique per file and referenced ones must be defined before use.

## Examples Of Use

Examples of both input files and output files can be found in Input and Output folders respectively. Those are just examples, as one can pick both of those locations.

## Additional Notes

The program is written to attempt to salvage poorly written configuration files, however some are unsalvageable. It is suggested to check error logs to ensure that the configuration file it written properly. Empty files (not the same as no file) indicates no errors for that phase.

Scenario names with none filename friendly char (/, <, >, :, ", \\, |, ?, \*) are discouraged, but allowed. In the event that they are outputted, they will be removed which may cause filename clashes between similarly named scenarios.

Outputted scenarios are in dot file format, they can be viewed at the URL shortcut specified at [Dot File Viewer](https://dreampuf.github.io/GraphvizOnline). Simply copy the generated file's content into the text area of the viewer.

Any mention of requiring a number, is expected to be be a non-negative integer.

All generated files can be viewed as plain text. As such simply open them with a txt viewer.
