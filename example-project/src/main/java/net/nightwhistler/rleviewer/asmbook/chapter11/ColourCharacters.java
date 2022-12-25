package net.nightwhistler.rleviewer.asmbook.chapter11;

import net.nightwhistler.tddasm.mos65xx.Operand;
import net.nightwhistler.tddasm.mos65xx.Program;
import net.nightwhistler.tddasm.mos65xx.ProgramBuilder;
import net.nightwhistler.tddasm.util.ProgramWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import static net.nightwhistler.tddasm.mos65xx.Operand.address;
import static net.nightwhistler.tddasm.mos65xx.Operand.value;

public class ColourCharacters {
    /**
     * Example 11-1 A Progtam to Demonstrate some of the
     * Video Display Options of the VIC
     */
    public static Program vicCharacters() {
        final Operand.TwoByteAddress SCN = address(0x8400);
        final Operand.TwoByteAddress CLR = address(0xD800);

        return new ProgramBuilder()
                //VIC Bank select routine
                .lda(value(03)) //Make bits 0 and 1 of port A
                .ora(address(0xDD02)) //Output bits
                .sta(address(0xD002))
                .lda(value(0xFC))  //Mask bits 0 and 1
                .and(address(0xDD00))
                .ora(value(01)) //Select bank #2, $8000-$BFFF
                .sta(address(0xDD00)) //Store in port A of CIA #2

                //Select locations for screen memory
                .lda(value(0x0F)) //Mask most signigicant nibble
                .and(address(0xD018)) //$D018 is register $18 in the VIC
                .ora(value(0x10)) //Put screen memory at $8400
                .sta(address(0xD018))

                //Fill screen memory with screen codes
                .lda(value(250))  //Set up screen memory to display the character set
            .label("loop1")
                .dex()
                .txa()
                .sta(SCN.xIndexed()) //Put codes in all 1000 locations
                .sta(SCN.plus(250).xIndexed())
                .sta(SCN.plus(500).xIndexed())
                .sta(SCN.plus(750).xIndexed())
                .bne("loop1")

                //Fill colour memory with colour codes
                .ldx(value(250)) //Set up colour memory for a variety of colours
            .label("loop2")
                .txa()
                .and(value(0x0F))
                .dex()
                .sta(CLR.xIndexed())
                .sta(CLR.plus(250).xIndexed())
                .sta(CLR.plus(500).xIndexed())
                .sta(CLR.plus(750).xIndexed())
                .bne("loop2")

                //Choose background and exteriour colour
                .lda(value(0x0F)) //Choose light gray for the background colour
                .sta(address(0xD021))
                .lda(value(0x02)) //Choose red for the border
                .sta(address(0xD020))

                //Select Location for character memory
                .lda(value(0xF1)) //Mask bits 1,2 and 3
                .and(address(0xD018)) //Put character memory at $9000
                .ora(value(0x04))
                .sta(address(0xD018))
            .label("here")
                .jmp("here") //Loop forever
                .buildProgram();

    }


    public static void main(String argv[]) {
        try {
            File output = new File("vic_characters.prg");
            output.createNewFile();
            FileOutputStream fout = new FileOutputStream(output);
            var program = vicCharacters().withBASICStarter();
            program.printASM(new PrintWriter(System.out), true);

            ProgramWriter.writeProgram(program, fout);
            fout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
