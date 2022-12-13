package net.nightwhistler.tddasm.mos65xx;

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

}
