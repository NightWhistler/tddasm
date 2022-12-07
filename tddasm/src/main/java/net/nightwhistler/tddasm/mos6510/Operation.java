package net.nightwhistler.tddasm.mos6510;

import io.vavr.Tuple2;
import io.vavr.collection.List;


public record Operation(OpCode opCode, AddressingMode addressingMode, byte... values) {

    public byte[] toBytes(AddressingMode addressingMode, byte... value) {
        byte firstByte = opCode.codeForAddressingMode(addressingMode).getOrElseThrow(
                () -> new IllegalArgumentException("Unsupported addressingmode: " + addressingMode + " for opcode " + opCode)
        );

        byte[] result = new byte[value.length+1];
        result[0] = firstByte;
        System.arraycopy(value, 0, result, 1, value.length);

        return result;
    }

    public static Operation fromBytes(byte... bytes) {
        byte firstByte = bytes[0];
        var maybeMapping= List.of(OpCode.values())
                .flatMap(o -> o.addressingModeMappings().map(m -> new Tuple2<>(o, m)))
                .filter(t -> t._2._2 == firstByte);

        return maybeMapping.map(tuple -> {
            OpCode opCode = tuple._1;
            AddressingMode addressingMode = tuple._2._1;
            if (bytes.length == 2) {
              return new Operation(opCode, addressingMode, bytes[1]);
            } else {
               return new Operation(opCode, addressingMode, bytes[1], bytes[2]);
            }
        }).getOrElseThrow(() -> new UnsupportedOperationException("Cannot map byte-value: " + Integer.toHexString(firstByte)));

    }
}
