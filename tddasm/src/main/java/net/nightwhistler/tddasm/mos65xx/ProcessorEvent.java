package net.nightwhistler.tddasm.mos65xx;

import io.vavr.collection.List;

public sealed interface ProcessorEvent {
    record OperationPerformed(Operand.TwoByteAddress atLocation, Operation operation) implements ProcessorEvent {}
    record MemoryLocationChanged(Operand.TwoByteAddress atLocation, byte oldValue, byte newValue) implements ProcessorEvent {}

    record JumpedTo(Operand.TwoByteAddress toLocation, List<Label> labels) implements ProcessorEvent {}

    record RegisterStateChangedEvent(
            Operand.TwoByteAddress programCounter,
            int stackPointer,
            byte xRegister,
            byte yRegister,
            byte accumulator,
            StatusRegister statusRegister
            ) implements ProcessorEvent {}

    interface Listener<E extends ProcessorEvent> {
        void receiveEvent(E processorEvent);
    }
}
