# gen2sat

This repository contains the source-code for Gen2sat - an automated tool for reasoning with non-classical logics.

## Installation
See INSTALL.md. Or don't install and use `gen2sat.jar`, which was pre-compiled.

## Usage
```
java -jar gen2sat.jar example.txt
```

## An example: logic C1
An example using logic C1 is given in c1.txt.
The connectives are the standard classical connectives, including TOP.

The calculus rules are well-known for C1.

The system is not analytic but is admits a more general analyticity criterion -- every sequent is derivable using a derivation that has subformulas of formulas that occur in the sequent, and their negations.
Therefore, we have the line:
`analyticity: NOT`.

The input sequent checkes whether the formula
`~(A/\ ~ (A/\ ~ (A/\ ~A)))` is valid in C1. It is not.
Therefore, the tool says "unprovable".
It also gives you a countermodel in a generalized semantics.

If you change the last line to:
`=>A OR (NOT A)`, then you get a valid formula in the logic, and gen2sat will say `provable`. It will also tell you that only 2 rules are needed to prove it.
