package net.nightwhistler.helloworld;

import net.nightwhistler.tddasm.annotation.CompileProgram;
import net.nightwhistler.tddasm.mos65xx.Program;
import net.nightwhistler.tddasm.mos65xx.ProgramBuilder;

import static net.nightwhistler.tddasm.c64.kernal.ChrOut.CHROUT_ADDRESS;
import static net.nightwhistler.tddasm.c64.kernal.ClearScreen.CLR_SCREEN_ADDRESS;
import static net.nightwhistler.tddasm.mos65xx.Operand.address;
import static net.nightwhistler.tddasm.mos65xx.Operand.label;
import static net.nightwhistler.tddasm.mos65xx.Operand.value;

public class HelloWorld {

    /**
     * Version to be compiled to a C64 binary,
     * adds the BASIC starter for running on actual hardware
     * or on VICE.
     *
     * @return
     */
    @CompileProgram("hw-pure.prg")
    public static Program helloWorldUsingASM() {
        return usingPureASM().buildProgram().withBASICStarter();
    }

    /**
     * A Hello Wold that doesn't use any Kernal routines
     * @return
     */
    public static ProgramBuilder usingPureASM() {

        /*
        Hello world example taken from
        https://8bitheaven.home.blog/2020/01/07/c64-assembly-hello-world/
        and modified to not use the kernal
         */
        return new ProgramBuilder()
                      .label("start")
                        //Make screen black and text white
                        .lda(value(0x00))
                        .sta(address(0xd020))
                        .sta(address(0xd021))
                        .lda(value(0x01))
                        .sta(address(0x286))
                        //Clear the screen and jump to draw routine
                        .jsr("clear_screen")
                        //Select lower-case fonts
                        .lda(value(0x17))
                        .sta(address(0xD018))

                        //Call text rendering routine
                        .jsr("draw_text")
                        .rts()

                      .label("msg")
                        .screenCodes("             Hello world!               ")
                      .label("draw_text")
                        .lda(value(0x00))
                      .label("draw_loop")
                        .lda(label("msg").xIndexed())
                        .sta(address(0x05e0).xIndexed())
                        .inx()
                        .cpx(value(0x28))
                        .bne("draw_loop")
                        .rts()

                .include(clearScreen());
    }

    private static ProgramBuilder clearScreen() {
        return new ProgramBuilder()
          .label("clear_screen")
                .lda(value(0x20))
                .sta(address(0x400).xIndexed())
                .sta(address(0x500).xIndexed())
                .sta(address(0x600).xIndexed())
                .sta(address(0x6E8).xIndexed())
                //Grab the text colour and set it in colour RAM
                .lda(address(0x286))
                .sta(address(0xD800).xIndexed())
                .sta(address(0xD900).xIndexed())
                .sta(address(0xDA00).xIndexed())
                .sta(address(0xDAE8).xIndexed())
                .inx()
                .bne("clear_screen")
                .rts();
    }

    /**
     * A much shorter Hello World using Kernal routines
     * @return
     */
    @CompileProgram("hw-kernal.prg")
    public static Program helloWorldUsingKernal() {
        return usingKernal().buildProgram().withBASICStarter();
    }

    public static ProgramBuilder usingKernal() {
        return new ProgramBuilder()
                .jsr(CLR_SCREEN_ADDRESS)  //$e5ff, unofficial but used a lot
                .ldx(value(0x00))
            .label("write")
                .lda(label("hello").xIndexed())
                .jsr(CHROUT_ADDRESS) //$FFD2
                .inx()
                .cpx(value(0x0B))
                .bne("write")
                .rts()
            .label("hello")
                .screenCodes("HELLO WORLD");
    }
}
