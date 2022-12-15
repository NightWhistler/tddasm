package net.nightwhistler.tddasm.mos65xx;

import static net.nightwhistler.ByteUtils.JAVA_BYTE_0_MASK;
import static net.nightwhistler.ByteUtils.JAVA_BYTE_1_MASK;

public sealed interface Operand {
    AddressingMode addressingMode();

    sealed interface AddressOperand extends Operand {}

    final class NoValue implements Operand {
        @Override
        public AddressingMode addressingMode() {
            return AddressingMode.Implied;
        }
    }
    record ByteValue(byte value) implements Operand {
        @Override
        public AddressingMode addressingMode() {
            return AddressingMode.Value;
        }
    }

    record OneByteAddress(AddressingMode addressingMode, byte byteValue) implements AddressOperand {}

    record TwoByteAddress(AddressingMode addressingMode, byte lowByte, byte highByte) implements AddressOperand {}

    record LabelOperand(String label) implements AddressOperand {
        @Override
        public AddressingMode addressingMode() {
            //Labels are resolved to an absolute address,
            //which gives them absolute addressing
            return AddressingMode.AbsoluteAddress;
        }
    }

    static ByteValue value(int value) {
        return new ByteValue((byte) value);
    }

    static TwoByteAddress absolute(int value) {
        byte lowByte = (byte) (value & JAVA_BYTE_0_MASK);
        byte highByte = (byte) (value & JAVA_BYTE_1_MASK);

        return new TwoByteAddress(AddressingMode.AbsoluteAddress, lowByte, highByte);
    }


    static OneByteAddress zeroPage(int value) {
        return new OneByteAddress(AddressingMode.ZeroPageAddress, (byte) value);
    }

    static OneByteAddress indexedIndirectY(OneByteAddress oneByteAddress) {
        return new OneByteAddress(AddressingMode.IndirectIndexedY, oneByteAddress.byteValue);
    }

    static OneByteAddress indexedIndirectY(int value) {
        return new OneByteAddress(AddressingMode.IndirectIndexedY, (byte) value);
    }

}
