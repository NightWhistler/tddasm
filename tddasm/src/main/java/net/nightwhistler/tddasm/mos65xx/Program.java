package net.nightwhistler.tddasm.mos65xx;

import io.vavr.collection.List;
import io.vavr.control.Option;
import net.nightwhistler.ByteUtils;

import static io.vavr.control.Option.none;

public record Program(Operand.TwoByteAddress startAddress, List<ProgramElement> elements) {
    public Option<Operand.TwoByteAddress> resolveLabel(String label) {
        return none();
    }

    /**
     * Compiles the program, but doesn't add the startAddress in the first 2 bytes
     * @return
     */
    public byte[] compile() {
        int length = elements.foldLeft(0, (count, e) -> count + e.length());

        //TODO Actually compile elements, and resolve labels.

        return new byte[length];
    }
}
