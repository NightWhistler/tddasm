package net.nightwhistler.tddasm.mos65xx;

import java.util.function.Consumer;

public record OpCodeHandler(OpCode opCode, Consumer<Processor> processFunction) {
    public boolean handlesOpCode(OpCode opCode) {
        return opCode == this.opCode;
    }



}
