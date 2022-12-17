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
        //FIXME: This is used a lot, maybe make a separate Address type
        //Then have AddressOperand be a record wrapping an Address and an AddressingMode

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

        public TwoByteAddress plus(int offset) {
            int newAddress = toInt() + offset;
            return new TwoByteAddress(this.addressingMode, ByteUtils.lowByte(newAddress), ByteUtils.highByte(newAddress));
        }

    }

    record LabelOperand(String label, AddressingMode addressingMode) implements AddressOperand {
        public LabelOperand(String label) {
            this(label, AddressingMode.AbsoluteAddress);
        }

        public LabelOperand xIndexed() {
            return new LabelOperand(label, AddressingMode.AbsoluteAddressX);
        }

        public LabelOperand yIndexed() {
            return new LabelOperand(label, AddressingMode.AbsoluteAddressY);
        }

        @Override
        public byte[] bytes() {
            if (addressingMode == AddressingMode.Relative) {
                return new byte[1];
            } else {
                return new byte[2];
            }
        }
    }

    static LabelOperand addressOf(String label) {
        return new LabelOperand(label);
    }

    static ByteValue value(int value) {
        return new ByteValue((byte) value);
    }

    static TwoByteAddress address(int value) {
        byte lowByte = lowByte(value);
        byte highByte = highByte(value);

        return new TwoByteAddress(AddressingMode.AbsoluteAddress, lowByte, highByte);
    }

    static OneByteAddress zeroPage(int value) {
        return new OneByteAddress(AddressingMode.ZeroPageAddress, (byte) value);
    }

}
