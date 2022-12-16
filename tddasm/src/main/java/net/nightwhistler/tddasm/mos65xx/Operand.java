package net.nightwhistler.tddasm.mos65xx;

import net.nightwhistler.ByteUtils;

import static net.nightwhistler.ByteUtils.highByte;
import static net.nightwhistler.ByteUtils.lowByte;

public sealed interface Operand {
    AddressingMode addressingMode();

    default byte[] bytes() {
        return new byte[0];
    }

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

        @Override
        public byte[] bytes() {
            return new byte[]{ value };
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

        @Override
        public byte[] bytes() {
            return new byte[]{ byteValue };
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

        @Override
        public byte[] bytes() {
            return new byte[]{ lowByte, highByte };
        }

        public int toInt() {
            return ByteUtils.littleEndianBytesToInt(lowByte, highByte);
        }

    }

    record LabelOperand(String label, boolean relative) implements AddressOperand {
        public LabelOperand(String label) {
            this(label, false);
        }

        @Override
        public AddressingMode addressingMode() {
            if (relative ) {
                return AddressingMode.Relative;
            } else {
                return AddressingMode.AbsoluteAddress;
            }
        }

        @Override
        public byte[] bytes() {
            //FIXME: This gives the right size but obviously not the right value.
            if (relative) {
                return new byte[1];
            } else {
                return new byte[2];
            }
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
