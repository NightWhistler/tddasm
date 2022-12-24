package net.nightwhistler.tddasm.mos65xx;

import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vavr.control.Try;

import static net.nightwhistler.tddasm.mos65xx.AddressingMode.AbsoluteAddress;
import static net.nightwhistler.tddasm.mos65xx.AddressingMode.AbsoluteAddressX;
import static net.nightwhistler.tddasm.mos65xx.AddressingMode.AbsoluteAddressY;
import static net.nightwhistler.tddasm.mos65xx.AddressingMode.AbsoluteIndirect;
import static net.nightwhistler.tddasm.mos65xx.AddressingMode.Accumulator;
import static net.nightwhistler.tddasm.mos65xx.AddressingMode.Implied;
import static net.nightwhistler.tddasm.mos65xx.AddressingMode.IndexedIndirectX;
import static net.nightwhistler.tddasm.mos65xx.AddressingMode.IndirectIndexedY;
import static net.nightwhistler.tddasm.mos65xx.AddressingMode.Relative;
import static net.nightwhistler.tddasm.mos65xx.AddressingMode.Value;
import static net.nightwhistler.tddasm.mos65xx.AddressingMode.ZeroPageAddress;
import static net.nightwhistler.tddasm.mos65xx.AddressingMode.ZeroPageAddressX;
import static net.nightwhistler.tddasm.mos65xx.AddressingMode.ZeroPageAddressY;

/**
 * All the MOS6510 Opcodes, including illegal codes
 *
 */
public enum OpCode {
    ADC {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(
                    OpCode.mode(this, Value, 0x69),
                    OpCode.mode(this, AbsoluteAddress, 0x6D),
                    OpCode.mode(this, AbsoluteAddressX, 0x7D),
                    OpCode.mode(this, AbsoluteAddressY, 0x79),
                    OpCode.mode(this, ZeroPageAddress, 0x65),
                    OpCode.mode(this, ZeroPageAddressX, 0x75),
                    OpCode.mode(this, IndexedIndirectX, 0x61),
                    OpCode.mode(this, IndexedIndirectX, 0x71)
            );
        }
    },
    ALR {
        @Override
        public boolean isIllegal() {
            return true;
        }

        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(OpCode.mode(this, Value, 0x4B));
        }
    },
    ANC {
        @Override
        public boolean isIllegal() {
            return true;
        }

        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(
                    OpCode.mode(this, Value, 0x0B),
                    OpCode.mode(this, Value, 0x2B)
            );
        }
    },
    AND {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(
                    mode(this, Value, 0x29),
                    mode(this, AbsoluteAddress, 0x2D),
                    mode(this, AbsoluteAddressX, 0x3D),
                    mode(this, AbsoluteAddressY, 0x39),
                    mode(this, ZeroPageAddress, 0x25),
                    mode(this, ZeroPageAddressX, 0x35),
                    mode(this, IndexedIndirectX, 0x21),
                    mode(this, IndirectIndexedY, 0x31)
            );
        }
    },
    ANE {
        @Override
        public boolean isIllegal() {
            return true;
        }

        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(
                    OpCode.mode(this, Value, 0x8B)
            );
        }
    },
    ARR {
        @Override
        public boolean isIllegal() {
            return true;
        }

        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(
                    OpCode.mode(this, Value, 0x6B)
            );
        }
    },
    ASL {
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(
                    mode(this, Accumulator, 0x0A),
                    mode(this, AbsoluteAddress, 0x0E),
                    mode(this, AbsoluteAddressX, 0x1E),
                    mode(this, ZeroPageAddress, 0x06),
                    mode(this, ZeroPageAddressX, 0x16)
            );
        }
    },
    BCC {
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(mode(this,AddressingMode.Relative, 0x90));
        }
    },
    BCS {
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(mode(this,AddressingMode.Relative, 0xB0));
        }
    },
    BEQ {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(mode(this,AddressingMode.Relative, 0xF0));
        }
    },
    BIT {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(
                    mode(this, AbsoluteAddress, 0x2C),
                    mode(this, ZeroPageAddress, 0x24)
            );
        }
    },
    BMI {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(mode(this,AddressingMode.Relative, 0x30));
        }
    },
    BNE {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(mode(this,AddressingMode.Relative, 0xD0));
        }
    },
    BPL {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(mode(this,AddressingMode.Relative, 0x10));
        }
    },
    BRK {
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(mode(this, Implied, 0x00));
        }
    },
    BVC {
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(mode(this, Relative, 0x50));
        }
    },
    BVS {
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(mode(this, Relative, 0x70));
        }
    },
    CLC {
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(mode(this, Implied, 0x18));
        }
    },
    CLD {
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(mode(this, Implied, 0xD8));
        }
    },
    CLI {
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(mode(this, Implied, 0x58));
        }
    },
    CLV {
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(mode(this, Implied, 0xB8));
        }
    },
    CMP {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(
                    mode(this,Value, 0xC9),
                    mode(this,AbsoluteAddress, 0xCD),
                    mode(this,AbsoluteAddressX, 0xDD),
                    mode(this,AbsoluteAddressY, 0xD9),
                    mode(this,ZeroPageAddress, 0xC5),
                    mode(this,ZeroPageAddressX, 0xD5),
                    mode(this,IndexedIndirectX, 0xC1),
                    mode(this,IndirectIndexedY, 0xD1)
            );
        }
    },
    CPX {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(
                    mode(this,Value, 0xE0),
                    mode(this,AbsoluteAddress, 0xEC),
                    mode(this,ZeroPageAddress, 0xE4)
            );
        }
    },
    CPY {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(
                    mode(this,AddressingMode.Value, 0xC0),
                    mode(this, AbsoluteAddress, 0xCC),
                    mode(this, ZeroPageAddress, 0xC4)
                );
        }
    },
    DCP {
        @Override
        public boolean isIllegal() {
            return true;
        }
    },
    DEC {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(
                    mode(this,AbsoluteAddress, 0xCE),
                    mode(this,AbsoluteAddressX, 0xDE),
                    mode(this,ZeroPageAddress, 0xC6),
                    mode(this,ZeroPageAddressX, 0xD6)
            );
        }
    },
    DEX {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(mode(this,Implied, 0xCA));
        }
    },
    DEY {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(mode(this,Implied, 0x88));
        }
    },
    EOR {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(
                    mode(this,Value, 0x49),
                    mode(this,AbsoluteAddress, 0x4D),
                    mode(this,AbsoluteAddressX, 0x5D),
                    mode(this,AbsoluteAddressY, 0x59),
                    mode(this,ZeroPageAddress, 0x45),
                    mode(this,ZeroPageAddressX, 0x55),
                    mode(this,IndexedIndirectX, 0x41),
                    mode(this,IndirectIndexedY, 0x51)
            );
        }
    },
    INC {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(
                    mode(this,AbsoluteAddress, 0xEE),
                    mode(this,AbsoluteAddressX, 0xFE),
                    mode(this,ZeroPageAddress, 0xE6),
                    mode(this,ZeroPageAddressX, 0xF6)
            );
        }
    },
    INX {
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(mode(this,Implied, 0xE8));
        }
    },
    INY {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(mode(this,Implied, 0xC8));
        }
    },
    ISC {
        @Override
        public boolean isIllegal() {
            return true;
        }
    },
    JAM {
        @Override
        public boolean isIllegal() {
            return true;
        }
    },
    JMP {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(
                    mode(this,AbsoluteAddress, 0x4C),
                    mode(this,AbsoluteIndirect, 0x6C)
            );
        }
    },
    JSR {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(mode(this,AbsoluteAddress, 0x20));
        }
    },
    LAS {
        @Override
        public boolean isIllegal() {
            return true;
        }
    },
    LAX {
        @Override
        public boolean isIllegal() {
            return true;
        }
    },
    LDA {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(
                    mode(this,Value, 0xA9),
                    mode(this,AbsoluteAddress, 0xAD),
                    mode(this,AbsoluteAddressX, 0xBD),
                    mode(this,AbsoluteAddressY, 0xB9),
                    mode(this,ZeroPageAddress, 0xA5),
                    mode(this,ZeroPageAddressX, 0xB5),
                    mode(this,IndexedIndirectX, 0xA1),
                    mode(this,IndirectIndexedY, 0xB1)
            );
        }
    },
    LDX {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(
                    mode(this,Value, 0xA2),
                    mode(this,AbsoluteAddress, 0xAE),
                    mode(this,AbsoluteAddressY, 0xBE),
                    mode(this,ZeroPageAddress, 0xA6),
                    mode(this,ZeroPageAddressY, 0xB6)
            );
        }
    },
    LDY {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(
                    mode(this,Value, 0xA0),
                    mode(this,AbsoluteAddress, 0xAC),
                    mode(this,AbsoluteAddressX, 0xBC),
                    mode(this,ZeroPageAddress, 0xA4),
                    mode(this,ZeroPageAddressX, 0xB4)
            );
        }
    },
    LSR,
    NOP,
    ORA {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(
                    mode(this, Value, 0x09),
                    mode(this, AbsoluteAddress, 0x0D),
                    mode(this, AbsoluteAddressX, 0x1D),
                    mode(this, AbsoluteAddressY, 0x19),
                    mode(this, ZeroPageAddress, 0x05),
                    mode(this, ZeroPageAddressX, 0x15),
                    mode(this, IndexedIndirectX, 0x01),
                    mode(this, IndirectIndexedY, 0x11)
            );
        }
    },
    PHA {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(mode(this, Implied, 0x48));
        }
    },
    PHP {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(mode(this, Implied, 0x08));
        }
    },
    PLA {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(mode(this, Implied, 0x68));
        }
    },
    PLP,
    RLA,
    ROL {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(
                    mode(this, Accumulator, 0x2A),
                    mode(this, AbsoluteAddress, 0x2E),
                    mode(this, AbsoluteAddressX, 0x3E),
                    mode(this, ZeroPageAddress, 0x26),
                    mode(this, ZeroPageAddressX, 0x36)
            );
        }
    },
    ROR,
    RRA,
    RTI {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(mode(this,Implied, 0x40));
        }
    },
    RTS {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(mode(this,Implied, 0x60));
        }
    },
    SAX, //Illegal
    SBC {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(
                    OpCode.mode(this, Value, 0xE9),
                    OpCode.mode(this, Value, 0xEB), //This has 2 codes for the same mode
                    OpCode.mode(this, AbsoluteAddress, 0xED),
                    OpCode.mode(this, AbsoluteAddressX, 0xFD),
                    OpCode.mode(this, AbsoluteAddressY, 0xF9),
                    OpCode.mode(this, ZeroPageAddress, 0xE5),
                    OpCode.mode(this, ZeroPageAddressX, 0xF5),
                    OpCode.mode(this, IndexedIndirectX, 0xE1),
                    OpCode.mode(this, IndexedIndirectX, 0xF1)
            );
        }
    },
    SBX,
    SEC {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(mode(this, Implied, 0x38));
        }
    },
    SED {
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(mode(this, Implied, 0xF8));
        }
    },
    SEI {
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(mode(this, Implied, 0x78));
        }
    },
    SHA,
    SHX,
    SHY,
    SLO,
    SRE,
    STA {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(
                    mode(this,AbsoluteAddress, 0x8D),
                    mode(this,AbsoluteAddressX,0x9D),
                    mode(this,AbsoluteAddressY, 0x99),
                    mode(this,ZeroPageAddress , 0x85),
                    mode(this,ZeroPageAddressX , 0x95),
                    mode(this,IndexedIndirectX , 0x81),
                    mode(this,IndirectIndexedY , 0x91)
            );
        }
    },
    STX {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(
                    mode(this,AbsoluteAddress, 0x8E),
                    mode(this,ZeroPageAddress, 0x86),
                    mode(this,ZeroPageAddressY, 0x96)
            );
        }
    },

    STY {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(
                    mode(this,AbsoluteAddress, 0x8C),
                    mode(this,ZeroPageAddress, 0x84),
                    mode(this,ZeroPageAddressX, 0x94)
            );
        }
    },
    TAS, //Illegal
    TAX {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(mode(this, Implied, 0xAA));
        }
    },
    TAY {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(mode(this, Implied, 0xA8));
        }
    },
    TSX {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(mode(this, Implied, 0xBA));
        }
    },
    TXA {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(mode(this, Implied, 0x8A));
        }
    },
    TXS {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(mode(this, Implied, 0x9A));
        }
    },
    TYA {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(mode(this, Implied, 0x98));
        }
    };

    public Option<AdressingModeMapping> findByAddressingMode(AddressingMode addressingMode) {
        return addressingModeMappings()
                .find(m -> m.addressingMode == addressingMode);
    }

    private static java.util.HashMap<Byte, OpCode.AdressingModeMapping> mappings = null;

    public static Option<AdressingModeMapping> findByByteValue(byte value) {
        if (mappings == null) {
            mappings = new java.util.HashMap<>();
            List.of(values()).flatMap(opCode ->
                Try.of(() -> opCode.addressingModeMappings()).getOrElse(List.empty())).forEach( m ->
                    mappings.put(m.code, m)
            );
        }

        return Option.of(mappings.get(value));
    }

    public boolean supportAddressingMode(AddressingMode addressingMode) {
        return addressingModeMappings().map(AdressingModeMapping::addressingMode)
                .contains(addressingMode);
    }

    private static AdressingModeMapping mode(OpCode opCode, AddressingMode addressingMode, int byteValue) {
        return new AdressingModeMapping(opCode, addressingMode, (byte) byteValue);
    }

    public List<AdressingModeMapping> addressingModeMappings() {
        throw new UnsupportedOperationException(String.format("OpCode %s has no addressing modes. This means it's either illegal or not yet implemented.", this));
    }

    public boolean isIllegal() {
        return false;
    }

    record AdressingModeMapping(OpCode opCode, AddressingMode addressingMode, byte code){}
}
