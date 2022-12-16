package net.nightwhistler.tddasm.mos65xx;

import static net.nightwhistler.ByteUtils.highByte;
import static net.nightwhistler.ByteUtils.lowByte;

public sealed interface Operand {
    AddressingMode addressingMode();

    sealed interface AddressOperand extends Operand {}

    static NoValue noValue() {
        return NoValue.noValue();
    }

    final class NoValue implements Operand {
        private NoValue() {}
        private static NoValue instance = new NoValue();

        public static NoValue noValue() {
            return instance;
        }

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

    record OneByteAddress(AddressingMode addressingMode, byte byteValue) implements AddressOperand {
        public OneByteAddress(byte byteValue) {
            this(AddressingMode.ZeroPageAddress, byteValue);
        }

        public OneByteAddress xIndexed() {
            return new OneByteAddress(AddressingMode.ZeroPageAddressX, byteValue);
        }

        public OneByteAddress yIndexed() {
            return new OneByteAddress(AddressingMode.ZeroPageAddressY, byteValue);
        }


        public OneByteAddress indirectIndexedY() {
            return new OneByteAddress(AddressingMode.IndirectIndexedY, byteValue);
        }
    }

    record TwoByteAddress(AddressingMode addressingMode, byte lowByte, byte highByte) implements AddressOperand {
        public TwoByteAddress(byte lowByte, byte highByte) {
            this(AddressingMode.AbsoluteAddress, lowByte, highByte);
        }

        public TwoByteAddress xIndexed() {
            return new TwoByteAddress(AddressingMode.AbsoluteAddressX, lowByte, highByte);
        }

        public TwoByteAddress yIndexed() {
            return new TwoByteAddress(AddressingMode.AbsoluteAddressY, lowByte, highByte);
        }
    }

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
        byte lowByte = lowByte(value);
        byte highByte = highByte(value);

        return new TwoByteAddress(AddressingMode.AbsoluteAddress, lowByte, highByte);
    }

    static OneByteAddress zeroPage(int value) {
        return new OneByteAddress(AddressingMode.ZeroPageAddress, (byte) value);
    }

}
