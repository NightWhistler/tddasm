package net.nightwhistler.tddasm.mos65xx;

import io.vavr.collection.List;

public sealed interface ProcessorEvent {
    record OperationPerformed(Operand.TwoByteAddress atLocation, Operation operation) implements ProcessorEvent {}
    record MemoryLocationChanged(Operand.TwoByteAddress atLocation, byte oldValue, byte newValue) implements ProcessorEvent {}

    record JumpedTo(Operand.TwoByteAddress toLocation, List<Label> labels) implements ProcessorEvent {}

    record RegisterStateChangedEvent(
            Operand.TwoByteAddress programCounter,
            byte xRegister,
            byte yRegister,
            byte accumulator,
            boolean zeroFlag,
            boolean negativeFlag,
            boolean carryFlag,
            boolean breakFlag
            ) implements ProcessorEvent {}

    interface Listener {
        void receiveEvent(ProcessorEvent processorEvent);
    }
}
