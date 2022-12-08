package net.nightwhistler.tddasm.mos65xx;

public class Processor {
    private byte accumulator;
    private byte xRegister;
    private byte yRegister;

    private boolean zeroFlag;
    private boolean negativeFlag;
    private boolean carryFlag;

    //64kb of memory, C64 style.
    private static int MEMORY_SIZE = (int) Math.pow(2, 16);
    private byte[] memory = new byte[MEMORY_SIZE];

    public void performOperation(Operation operation) {
        byte value = value(operation.addressingMode(), operation.values());
        switch (operation.opCode()) {
            //Load Acumulator
            case LDA -> setFlag(accumulator = value);

            //Load X register
            case LDX -> setFlag(xRegister = value);

            //Load y register
            case LDY -> setFlag(yRegister = value);

            //Clear carry flag
            case CLC -> carryFlag = false;
            default -> throw new UnsupportedOperationException("Not yet implemented: " + operation.opCode());
        }
    }

    private void setFlag(byte result) {
        this.zeroFlag = result == 0;
        this.negativeFlag = result < 0;
    }

    private byte value(AddressingMode addressingMode, byte[] operand) {
        return switch (addressingMode) {
            case Value -> operand[0];
            case AbsoluteAddress -> peekValue(toInt(operand[0], operand[1]));
            case AbsoluteAddressX -> peekValue(toInt(operand[0], operand[1]) + xRegister);
            case AbsoluteAddressY -> peekValue(toInt(operand[0], operand[1]) + yRegister);
            case ZeroPageAddress -> peekValue(toInt(operand[0], (byte) 0x00));
            case ZeroPageAddressX -> peekValue(toInt(operand[0], (byte) 0x00) + xRegister);
            case IndirectIndexedY -> {
                //Must be 0-paged
                int address = toInt(operand[0], (byte) 0x00);
                var lowByte = peekValue(address);
                var highByte = peekValue(address+1);

                yield peekValue(toInt(lowByte, highByte) + yRegister);
            }
            case IndexedIndirectX -> {
                int address = toInt(operand[0], (byte) 0x00);
                var lowByte = peekValue(address + xRegister);
                var highByte = peekValue(address+ xRegister +1);

                yield peekValue(toInt(lowByte, highByte));
            }
        };
    }

    private int toInt(byte lowByte, byte highByte) {
        return lowByte + (highByte << 8);
    }

    public void pokeValue(int location, byte value) {
        int offset = (location & 0x00FF);
        memory[offset] = value;
    }

    public byte peekValue(int location) {
        int offset = (location & 0x00FF);
        return memory[offset];
    }

    public byte getAccumulatorValue() {
        return accumulator;
    }

    public byte getXRegisterValue() {
        return xRegister;
    }

    public byte getYRegisterValue() {
        return yRegister;
    }

    public boolean isCarryFlagSet() {
        return carryFlag;
    }

    public boolean isNegativeFlagSet() {
        return negativeFlag;
    }

    public boolean isZeroFlagSet() {
        return zeroFlag;
    }
}
