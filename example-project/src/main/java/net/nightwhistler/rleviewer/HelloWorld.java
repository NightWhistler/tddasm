package net.nightwhistler.rleviewer;

import net.nightwhistler.tddasm.mos65xx.Operand;
import net.nightwhistler.tddasm.mos65xx.Program;
import net.nightwhistler.tddasm.mos65xx.ProgramElementsBuilder;

import static net.nightwhistler.tddasm.mos65xx.Operand.absolute;
import static net.nightwhistler.tddasm.mos65xx.Operand.addressOf;
import static net.nightwhistler.tddasm.mos65xx.Operand.value;

public class HelloWorld {
    public Program main() {
        return new Program(Operand.absolute(0x4000),
                new ProgramElementsBuilder()
                      .label("start")
                        //Make screen black and text white
                        .lda(value(0x00))
                        .sta(absolute(0xd020))
                        .sta(absolute(0xd021))
                        .lda(value(0x01))
                        .sta(absolute(0x286))
                        //Clear the screen and jump to draw routine
                        .jsr(absolute(0xe544))
                        .jsr("draw_text")
                        .rts()

                      .label("msg")
                        .text("hello world!")

                      .label("draw_text")
                        .lda(value(0x00))
                      .label("draw_loop")
                        .lda(addressOf("message").xIndexed())
                        .inx()
                        .cpx(value(0x28))
                        .bne("draw_loop")
                        .rts()

                    .build());
    }
}
