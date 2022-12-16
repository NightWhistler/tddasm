package net.nightwhistler.tddasm.mos65xx;

public enum AddressingMode {
    Implied,
    Value,
    AbsoluteAddress,
    AbsoluteAddressX,
    AbsoluteAddressY,
    AbsoluteIndirect,
    ZeroPageAddress,
    ZeroPageAddressX,
    ZeroPageAddressY,
    IndexedIndirectX,
    IndirectIndexedY,
}
