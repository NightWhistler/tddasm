package net.nightwhistler.tddasm.mos6510;

import java.util.function.Consumer;

public record OpCodeHandler(OpCode opCode, Consumer<Processor> processFunction) {
    public boolean handlesOpCode(OpCode opCode) {
        return opCode == this.opCode;
    }



}
