package net.nightwhistler.rleviewer;

import io.vavr.collection.List;
import net.nightwhistler.tddasm.mos65xx.ProgramElement;

import static net.nightwhistler.tddasm.mos65xx.AddressingMode.IndirectIndexedY;
import static net.nightwhistler.tddasm.mos65xx.AddressingMode.Value;
import static net.nightwhistler.tddasm.mos65xx.Label.label;
import static net.nightwhistler.tddasm.mos65xx.OpCode.INY;
import static net.nightwhistler.tddasm.mos65xx.OpCode.LDA;
import static net.nightwhistler.tddasm.mos65xx.OpCode.LDY;
import static net.nightwhistler.tddasm.mos65xx.OpCode.STA;
import static net.nightwhistler.tddasm.mos65xx.Operation.operation;

public interface ClearBitmapMemory {
    int VIC_REG_1=0xD011;
    int VIC_REG_2=0xD016;
    int VIC_BITMAP_VECTOR=0xD018;
    int BITMAP_DATA_START=0x2000;
    int BITMAP_DATA_END=0x3F3F;
    int MEM_VECTOR_LOW=0xFB;
    int MEM_VECTOR_HIGH=0xFC;
    int KERNAL_CLEAR_SCR=0x544;
    int CLEAR_VALUE=0x0F;


    /**
     * *=$0801   ; Starting Address BASIC + 1 =&gt; SYS 2049
     *
    *
     *  jsr .fill_memory
     *  lda #01
     *  jsr .set_video_mem
     *  rts
     */
    /**
     * .fill_memory
     *  ; We want to clear addresses $2000 - $3F3F, so 8,000 addresses
     * ; Start y at 0
     *  ldy #$00 ; start the loop at $FF so the first iny gives 0
     * .outer_loop:
     *  lda #CLEAR_VALUE
     * .inner_loop:
     *  ; We store in the low byte of the memory vector, but the instruction will also
     *  ; read the next (high) byte
     *    sta (MEM_VECTOR_LOW), y
     *    iny; we increase y, until it rolls over to 0
     *    beq .end_inner_loop ; If the roll-over occurred jump out of the loop
     *    cpy #<BITMAP_DATA_END ; When the low byte matches the end, check the high byte
     *    bne .inner_loop
     *
     *    lda #>BITMAP_DATA_END ; Check against high byte of bitmap vec
     *    cmp MEM_VECTOR_HIGH
     *    beq .loop_end
     *    lda #CLEAR_VALUE
     *    jmp .inner_loop
     * .end_inner_loop:
     *  ; After a roll-over we increase the high byte by 1
     *  inc MEM_VECTOR_HIGH
     *  jmp .outer_loop
     *
     * .loop_end:
     *  sty MEM_VECTOR_LOW ; Store the y register, so the vector points to the final address for verification
     *  lda #CLEAR_VALUE
     *  sta BITMAP_DATA_END ; Our loop ends 1 address early
     *  rts
     *
    */

    static List<ProgramElement> fillMemory() {
        return List.of(
                //This is a subroutine to clear 8000 memory addresses,
                //from the
                label("fill_memory"),
                operation(LDY, Value, 0x00),
                label("outer_loop"),
                operation(LDA, Value, CLEAR_VALUE),
                label("inner_loop"),
                // We store in the low byte of the memory vector, but the instruction will also
                // read the next (high) byte
                operation(STA, IndirectIndexedY, MEM_VECTOR_LOW),
                operation(INY) //Increase Y until it rolls over to 0
//                operation(BEQ, )

        );
    }
}
