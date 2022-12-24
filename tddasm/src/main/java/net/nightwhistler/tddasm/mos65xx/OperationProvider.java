package net.nightwhistler.tddasm.mos65xx;

public record OperationProvider(OpCode opCode, Operand operand) implements ProgramElement {
    Operation provide(Program program, Operand.TwoByteAddress offset) {
        return switch (operand) {
            case Operand.ConcreteOperand concreteOperand -> new Operation(opCode, concreteOperand);
            case Operand.VirtualOperand virtualOperand -> new Operation(opCode, virtualOperand.resolve(program, offset));
        };
    }

    @Override
    public int length() {
        //1 byte opcode, plus the operand
        return operand.length() + 1;
    }
}
