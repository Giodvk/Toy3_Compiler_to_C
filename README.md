# Toy3 Compiler — Handcrafted from tokens to C

A handcrafted compiler for the **Toy3** language — built with care and zero magic.  
Handwritten lexer, JavaCUP-driven parser with a hand-crafted grammar, manual semantic analysis, and a custom C code generator (strings, dynamic allocation, functions, calling conventions — everything hand-managed). 
Ready to generate readable C that you compile with `gcc`.

---

## Quick summary
- Handwritten **lexer** for precise token control  
- **JavaCUP** parser using a custom grammar (manually designed)  
- Manual **semantic analyzer**: symbol table, scoping, type checking  
- Handcrafted **C code generator**: dynamic memory, string handling, functions, locals, control flow  
- Target: readable `.c` output → compile with `gcc`

---

## Features
- Full pipeline up to semantic analysis and code generation
- Explicit AST representation and deterministic translation strategy
- Memory model mapping Toy3 strings & allocations to C (`malloc`/`free` semantics)
- Function calling, parameter passing, and return value handling
- Clear, debuggable C output — ideal for verification and teaching

---

## Tech stack
- Implementation language: **Java**  
- Parser generator: **JavaCUP** (grammar hand-authored)  
- Target language: **C** (compiled with `gcc`)  
- Build: plain `javac` + `java` for the compiler; `gcc` for generated C

## Author
**GioDVK**: Head Developper
