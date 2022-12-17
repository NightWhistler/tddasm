package net.nightwhistler.tddasm.mos65xx;

public sealed interface ProcessorEvent {
    record OperationPerformed(Operand.TwoByteAddress atLocation, Operation operation) implements ProcessorEvent {}
    record MemoryLocationChanged(Operand.TwoByteAddress atLocation, byte oldValue, byte newValue) implements ProcessorEvent {}

    interface Listener {
        void receiveEvent(ProcessorEvent processorEvent);
    }
}
