package net.nightwhistler.tddasm.examples;

import net.nightwhistler.tddasm.mos65xx.Operand;
import net.nightwhistler.tddasm.mos65xx.Program;
import net.nightwhistler.tddasm.mos65xx.ProgramElementsBuilder;
import net.nightwhistler.tddasm.util.ProgramWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static net.nightwhistler.tddasm.mos65xx.Operand.address;
import static net.nightwhistler.tddasm.mos65xx.Operand.addressOf;
import static net.nightwhistler.tddasm.mos65xx.Operand.value;

public class HelloWorld {
    public static Program main() {

        /*
        Hello world example taken from
        https://8bitheaven.home.blog/2020/01/07/c64-assembly-hello-world/
         */
        return new Program(Operand.address(0x4000),
                new ProgramElementsBuilder()
                      .label("start")
                        //Make screen black and text white
                        .lda(value(0x00))
                        .sta(address(0xd020))
                        .sta(address(0xd021))
                        .lda(value(0x01))
                        .sta(address(0x286))
                        //Clear the screen and jump to draw routine
                        .jsr(address(0xe544))
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
                        .lda(addressOf("msg").xIndexed())
                        .sta(address(0x05e0).xIndexed())
                        .inx()
                        .cpx(value(0x28))
                        .bne("draw_loop")
                        .rts()

                    .build());
    }

    public static void main(String argv[]) {
        try {
            File output = new File("hello_world.prg");
            output.createNewFile();
            FileOutputStream fout = new FileOutputStream(output);
            ProgramWriter.writeProgram(main(), fout);
            fout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
