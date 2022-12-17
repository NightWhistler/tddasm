package net.nightwhistler.tddasm.util;

import net.nightwhistler.tddasm.mos65xx.Program;

import java.io.IOException;
import java.io.OutputStream;

public class ProgramWriter {
    public static void writeProgram(Program program, OutputStream outputStream) throws IOException {
        outputStream.write(program.startAddress().lowByte());
        outputStream.write(program.startAddress().highByte());
        outputStream.write(program.compile());
    }
}
