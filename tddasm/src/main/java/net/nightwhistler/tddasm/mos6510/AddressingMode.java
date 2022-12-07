package net.nightwhistler.tddasm.mos6510;

public enum AddressingMode {
    Value,
    AbsoluteAddress,
    AbsoluteAddressX,
    AbsoluteAddressY,
    ZeroPageAddress,
    ZeroPageAddressX,
    IndexedIndirectX,
    IndirectIndexedY,
}
