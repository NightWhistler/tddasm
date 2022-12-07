# Test-driven Assembly programming

This is my attempt at a slightly insane idea: to be able to write assembly (in my case for the C64) in a test-driven way.

The method I intend to use is this:

 - Create a Java DSL for actually writing assembly. This should give you the same level of control as .asm files and a traditional assembler, but also allow the full power of Java for dynamic coding.
 - Create a very simplified model of the processor and the memory. This is not intended to be a full emulator, but it will have the full instruction set.
 - Create a set of Unit testing tools that hooks into JUnit and allows for easy assertions against the emulator, with memory state checks, etc.

I'm still very much learning C64 assembly, and letting this project grow with me. So, almost by definition it will have stuff missing.
I hope it will be useful to me and maybe others as well though.
