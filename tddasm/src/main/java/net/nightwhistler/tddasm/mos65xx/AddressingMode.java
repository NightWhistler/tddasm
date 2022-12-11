package net.nightwhistler.tddasm.mos65xx;

public enum AddressingMode {
    Value,
    AbsoluteAddress,
    AbsoluteAddressX,
    AbsoluteAddressY,
    ZeroPageAddress,
    ZeroPageAddressX,
    ZeroPageAddressY,
    IndexedIndirectX,
    IndirectIndexedY,
}
