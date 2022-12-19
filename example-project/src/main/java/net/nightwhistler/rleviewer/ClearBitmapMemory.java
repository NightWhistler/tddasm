package net.nightwhistler.rleviewer;

import io.vavr.collection.List;
import net.nightwhistler.tddasm.mos65xx.Operand;
import net.nightwhistler.tddasm.mos65xx.ProgramBuilder;
import net.nightwhistler.tddasm.mos65xx.ProgramElement;

import static net.nightwhistler.tddasm.mos65xx.Operand.address;
import static net.nightwhistler.tddasm.mos65xx.Operand.value;
import static net.nightwhistler.tddasm.mos65xx.Operand.zeroPage;

public interface ClearBitmapMemory {
    int VIC_REG_1=0xD011;
    int VIC_REG_2=0xD016;
    int VIC_BITMAP_VECTOR=0xD018;
    Operand.TwoByteAddress BITMAP_DATA_START=address(0x2000);
    Operand.TwoByteAddress BITMAP_DATA_END= address(0x3F3F);
    Operand.OneByteAddress MEM_VECTOR_LOW=zeroPage(0xFB);
    Operand.OneByteAddress MEM_VECTOR_HIGH=zeroPage(0xFC);
    int KERNAL_CLEAR_SCR=0x544;
    Operand.ByteValue CLEAR_VALUE=value(0x0F);


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

    static ProgramBuilder fillMemory() {

        //This is a subroutine to clear 8000 memory addresses,
        return new ProgramBuilder().
              label("fill_memory")
                .ldx(value(BITMAP_DATA_START.lowByte()))
                .stx(MEM_VECTOR_LOW)
                .ldx(value(BITMAP_DATA_START.highByte()))
                .stx(MEM_VECTOR_HIGH)
                .ldy(value(0x00)) // Start Y register at 0
                .lda(CLEAR_VALUE)
              .label("inner_loop")
                // We store in the low byte of the memory vector, but the instruction will also
                // read the next (high) byte
                .sta(MEM_VECTOR_LOW.indirectIndexedY())
                .iny() //We increase y, until it rolls over to 0
                .bne("inner_loop") //As long as we haven't rolled over, loop
                .inc(MEM_VECTOR_HIGH)
                .ldx(MEM_VECTOR_HIGH)
                .cpx(value(BITMAP_DATA_END.highByte()))
                .bne("inner_loop")
              .label("last_loop")
                .sta(MEM_VECTOR_LOW.indirectIndexedY())
                .iny()
                .cpy(value(BITMAP_DATA_END.lowByte())) //This time we don't increase until 0, but the last address
                .bne("last_loop") //As long as we haven't rolled over, loop
                .sta(MEM_VECTOR_LOW.indirectIndexedY()) //Last item
                .rts();
    }
}
