package net.nightwhistler.passing;

import net.nightwhistler.tddasm.mos65xx.Operand;
import net.nightwhistler.tddasm.mos65xx.Processor;
import net.nightwhistler.tddasm.mos65xx.Program;
import net.nightwhistler.tddasm.mos65xx.ProgramBuilder;

import static net.nightwhistler.ByteUtils.bytes;
import static net.nightwhistler.tddasm.mos65xx.Operand.label;
import static net.nightwhistler.tddasm.mos65xx.Operand.value;

public class ArgumentPassing {

    //Slightly hacky, but by adding 3 to the stack base pointer, 0 becomes arg0
    static final Operand.TwoByteAddress stackMinusReturnAddress = Processor.STACK_BASE_ADDRESS.plus(3);

    public static Program oneByteAdditionArgPassing() {
        return new ProgramBuilder()
                .lda(value(0x03))
                .pha()
                .lda(value(0x05))
                .pha()
                .jsr("add")
                .rts()

                //We grab the 2 arguments from the stack and pass the result in the accumulator
            .label("add")
                .tsx()  //Grab the stack pointer
                .lda(stackMinusReturnAddress.xIndexed()) //Grab first arg
                .adc(stackMinusReturnAddress.plus(1).xIndexed()) //Grab second arg
                .rts()
                .buildProgram();
    }

    public static Program twoByteAddition() {

        return new ProgramBuilder()
                .jmp("main")
            .label("variables")
                .label("result").data(bytes(0x00, 0x00))
            .label("main")
                .lda(value(0xff))  //Push 2 bytes of the first number on to the stack
                .pha()
                .lda(value(0x00))
                .pha()

                .lda(value(0xff)) // And 2 bytes of the second number
                .pha()
                .lda(value(0x00))
                .jsr("two_byte_add")
                .rts()
            .label("two_byte_add")

                .java((p) -> System.out.println("Calling routine two_byte_add"))

                .tsx()
                .clc()
                //First add low bytes
                .lda(stackMinusReturnAddress.plus(0).xIndexed())
                .adc(stackMinusReturnAddress.plus(2).xIndexed())

                .java((p) -> System.out.println("First add, accumulator is " + p.getAccumulatorValue()))

                .ldy(value(0))
                .sta(label("result").yIndexed())
                //Then add high bytes, with carry
                .lda(stackMinusReturnAddress.plus(1).xIndexed())
                .adc(stackMinusReturnAddress.plus(3).xIndexed())
                .iny()
                .sta(label("result").yIndexed())
                .rts()
                .data(bytes(0x00, 0x00))

                .buildProgram();
    }
}
