package net.nightwhistler.tddasm.examples;

import net.nightwhistler.tddasm.mos65xx.Program;
import net.nightwhistler.tddasm.mos65xx.ProgramBuilder;
import net.nightwhistler.tddasm.util.ProgramWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import static net.nightwhistler.tddasm.mos65xx.Operand.address;
import static net.nightwhistler.tddasm.mos65xx.Operand.addressOf;
import static net.nightwhistler.tddasm.mos65xx.Operand.value;

public class HelloWorld {
    public static Program main() {

        /*
        Hello world example taken from
        https://8bitheaven.home.blog/2020/01/07/c64-assembly-hello-world/

        Adapted to use a custom clear screen routine, since we don't support
        emulating Kernal routines yet.
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
                        .lda(addressOf("msg").xIndexed())
                        .sta(address(0x05e0).xIndexed())
                        .inx()
                        .cpx(value(0x28))
                        .bne("draw_loop")
                        .rts()

                .include(clearScreen())
                .buildProgram();
    }

    private static ProgramBuilder clearScreen() {
        return new ProgramBuilder()
          .label("clear_screen")
                .lda(value(0x00))
                .sta(address(0x400).xIndexed())
                .sta(address(0x500).xIndexed())
                .sta(address(0x600).xIndexed())
                .sta(address(0x6e8).xIndexed())
                .inx()
                .bne("clear_screen")
                .rts();
    }

    public static void main(String argv[]) {
        try {
            File output = new File("hello_world2.prg");
            output.createNewFile();
            FileOutputStream fout = new FileOutputStream(output);
            var program = main().withBASICStarter();
            program.printASM(new PrintWriter(System.out), true);

            ProgramWriter.writeProgram(program, fout);
            fout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
