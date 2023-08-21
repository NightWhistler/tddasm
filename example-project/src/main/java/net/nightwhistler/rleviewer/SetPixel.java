package net.nightwhistler.rleviewer;

import net.nightwhistler.tddasm.mos65xx.Operand;
import net.nightwhistler.tddasm.mos65xx.ProgramBuilder;

import static net.nightwhistler.tddasm.mos65xx.Operand.accumulator;
import static net.nightwhistler.tddasm.mos65xx.Operand.value;
import static net.nightwhistler.tddasm.mos65xx.Operand.zeroPage;
import static net.nightwhistler.tddasm.mos65xx.ProgramBuilder.buildProgram;

public class SetPixel {

    private static final Operand.OneByteAddress xpos = zeroPage(0xFD);
    private static final Operand.OneByteAddress temp = zeroPage(0x02);
    private static final Operand.OneByteAddress ypos = zeroPage(0xFF);
    private static final Operand.OneByteAddress ial = zeroPage(0xFB);
    private static final Operand.OneByteAddress iah = (Operand.OneByteAddress) ial.plus(1);

    /**
     * Subroutine that sets a pixel to the specified colour.
     *
     * Arguments:
     *  - X-register: X coordinate
     *  - Y-register: Y coordinate
     *  - Accumulator: the colour value to set
     *
     * @return
     */
    public static ProgramBuilder setPixel() {
        return new ProgramBuilder()
            .label("set_pixel_variables")
                .pha() //Save accumulator on the stack
                .txa()
                .pha() //Save x-coordinate on the stack
                .tya()
                .pha() //Save y-coordinate on the stack

                //Calculate the byte to change

                //Calculate the pixel to change

                //store the value

                .rts();

    }


    private static ProgramBuilder plot(ProgramBuilder builder) {
        return builder
            .label("plot")
                .jsr("adhadl") //Turn X and Y into an address
                .sei() //Disable operating system interrupts
                .lda(zeroPage(01))  //Clear bit zero of 6510 IOP
                .and(value(0xFE))  //To switch basic ROM out of system to access R/W memory
                .sta(zeroPage(01))
                .lda(xpos) //Identify bit position in byte
                .and(value(7)) //Mask all but low three bits
                .tax() //Put in X for a counter
                .inx() //Increment counter
                .lda(value(0)) //Clear A
                .sec() //Carry will be rotated into A
            .label("bit")
                .ror(accumulator())  //Rotate right X times
                .dex() //Next X
                .bne("bit")

                .ora(ial.indirectIndexedY()) //Combine with other bits in
                .sta(ial.indirectIndexedY()) //the same byte in memory

                .lda(zeroPage(01))  //Switch BASIC ROM back into
                .ora(value(01)) //Commodore system
                .sta(zeroPage(01))
                .cli()  //Enable interrupts
                .rts();
    }

    private static ProgramBuilder calculateAdhAdlForPixel(ProgramBuilder builder) {

        /**
         * This takes the values in xpos and ypos and uses them to
         * calculate a vector, which is stored in ial, iah
         *
         * This can then be used to set the right value in that
         * address
         */

        return builder
                //Calculate y for (ind),Y addressing mode
            .label("adhadl")
                .lda(value(0xF8)) //Clear bits 0,1 and 2 of X
                .and(xpos)
                .sta(temp)
                .lda(ypos)
                .ora(temp)
                .tay()

                //Calculate BAL for (IND),Y addressing mode
                .lda(value(00))
                .sta(iah) //Clear IAL+1 location
                .lda(value(0xF8))  //Drop low three bits of Y
                .and(ypos)
                .asl(accumulator()) //Multiply by 8 with three
                .rol(iah) //shifts and rotates
                .asl(accumulator())
                .rol(iah)
                .asl(accumulator())
                .rol(iah)
                .sta(ial) //Calculation of BAL is complete

                //Calculate BAH for (IND),Y adressing mode
                .lda(ypos)
                .lsr(accumulator()) //Divide by 8 with 3 shifts left
                .lsr(accumulator())
                .lsr(accumulator())
                .clc()   //Add high byte of X
                .adc(xpos.plus(1))
                .adc(value(0xA0)) //Add bse address of HiRes screen
                .adc(iah)  //Add previous result
                .sta(iah)  //Calculation of BAH is complete
                .rts();
    }
}
