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
       byte value = value(operation.operand());

        switch (operation.opCode()) {
            //Load Acumulator
            case LDA -> setFlag(accumulator = value);

            //Store Accumulator
            case STA -> pokeValue(location((Operand.AddressOperand) operation.operand()), accumulator);

            //Load X register
            case LDX -> setFlag(xRegister = value);

            //Store X register
            case STX -> pokeValue(location((Operand.AddressOperand) operation.operand()), xRegister);

            //Load y register
            case LDY -> setFlag(yRegister = value);

            //Store y register
            case STY -> pokeValue(location((Operand.AddressOperand) operation.operand()), yRegister);

            //Clear carry flag
            case CLC -> carryFlag = false;
            default -> throw new UnsupportedOperationException("Not yet implemented: " + operation.opCode());
        }
    }

    private void setFlag(byte result) {
        this.zeroFlag = result == 0;
        this.negativeFlag = result < 0;
    }

    private int resolveLabel(String label) {
        return -1;
    }

    private int location(Operand.AddressOperand addressOperand) {
        return switch (addressOperand) {
            case Operand.LabelOperand labelOperand -> resolveLabel(labelOperand.label());
            case Operand.OneByteAddress oneByteAddress -> {

                byte byteValue = oneByteAddress.byteValue();

                yield switch (oneByteAddress.addressingMode()) {
                    case ZeroPageAddress -> byteValue;
                    case ZeroPageAddressX -> byteValue + xRegister;
                    case ZeroPageAddressY -> byteValue + yRegister;
                    case IndirectIndexedY -> {
                        //Must be 0-paged
                        int address = byteValue;
                        var lowByte = peekValue(address);
                        var highByte = peekValue(address+1);

                        yield toInt(lowByte, highByte) + yRegister;
                    }
                    case IndexedIndirectX -> {
                        int address = byteValue;
                        var lowByte = peekValue(address + xRegister);
                        var highByte = peekValue(address+ xRegister +1);

                        yield toInt(lowByte, highByte);
                    }

                    default -> throw new IllegalArgumentException(
                            String.format("Can't use %s for OneByteAddress", oneByteAddress.addressingMode())
                    );
                };
            }
            case Operand.TwoByteAddress twoByteAddress -> {
                var baseAddress = toInt(twoByteAddress.lowByte(), twoByteAddress.highByte());
                yield switch (twoByteAddress.addressingMode()) {
                    case AbsoluteAddress -> baseAddress;
                    case AbsoluteAddressX -> baseAddress + xRegister;
                    case AbsoluteAddressY -> baseAddress + yRegister;
                    default -> throw new IllegalArgumentException(
                            String.format("Can't use %s for TwoByteAddress", twoByteAddress.addressingMode())
                    );
                };
            }
        };
    }

    private byte value(Operand operand) {
        return switch (operand) {
            case Operand.ByteValue byteValue ->  byteValue.value();
            case Operand.AddressOperand addressOperand ->  peekValue(location(addressOperand));
            case default -> throw new IllegalArgumentException("Illegal Operand: " + operand);
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
