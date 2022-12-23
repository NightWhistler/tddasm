package net.nightwhistler.tddasm.mos65xx;

import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.control.Try;


public record Operation(OpCode opCode, Operand.ConcreteOperand operand) {

    public AddressingMode addressingMode() {
        return operand.addressingMode();
    }

    public Operation {
        if (!opCode.supportAddressingMode(operand.addressingMode())) {
            throw new IllegalArgumentException(
                    String.format("Opcode %s does not support AddressingMode %s", opCode, operand.addressingMode()));
        }
    }

    public static Operation operation(OpCode opCode) {
        return new Operation(opCode, Operand.noValue());
    }

    public static Operation operation(OpCode opCode, Operand.ConcreteOperand operand) {
        return new Operation(opCode, operand);
    }

    public byte[] bytes() {
        byte firstByte = opCode.findByAddressingMode(addressingMode()).getOrElseThrow(
                () -> new IllegalArgumentException("Unsupported AddressingMode: " + addressingMode() + " for OpCode " + opCode)
        ).code();

        byte[] value = operand.bytes();

        byte[] result = new byte[value.length+1];
        result[0] = firstByte;
        System.arraycopy(value, 0, result, 1, value.length);

        return result;
    }

    public static Operation fromBytes(byte... bytes) {
        byte firstByte = bytes[0];
        var maybeMapping= List.of(OpCode.values())
                .flatMap(o -> Try.of(() -> o.addressingModeMappings()).getOrElse(List.empty()).map(m -> new Tuple2<>(o, m)))
                .filter(t -> t._2.code() == firstByte);

        return maybeMapping.map(tuple -> {
            OpCode opCode = tuple._1;
            AddressingMode addressingMode = tuple._2.addressingMode();
            if (bytes.length == 2) {
              return new Operation(opCode, new Operand.OneByteAddress(addressingMode, bytes[1]));
            } else {
               return new Operation(opCode, new Operand.TwoByteAddress(addressingMode, bytes[1], bytes[2]));
            }
        }).getOrElseThrow(() -> new UnsupportedOperationException("Cannot map byte-value: " + Integer.toHexString(firstByte)));

    }

    @Override
    public String toString() {
        return (opCode.toString() + " " + operand.toString()).trim();
    }
}
