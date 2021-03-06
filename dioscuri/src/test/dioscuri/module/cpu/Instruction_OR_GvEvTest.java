package dioscuri.module.cpu;

import dioscuri.AbstractInstructionTest;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Bram Lohman\n@author Bart Kiers
 */
public class Instruction_OR_GvEvTest extends AbstractInstructionTest {

    /**
     * @throws Exception
     */
    public Instruction_OR_GvEvTest() throws Exception {
        super(80448, "OR_GvEv.bin");
    }

    /*
    * Test method for 'com.tessella.emulator.module.cpu.Instruction_OR_GvEv.execute()'
    */

    /**
     *
     */
    @Test
    public void testExecute() {
        String AX_ERROR = "AX contains wrong value";
        String DX_ERROR = "DX contains wrong value";
        String OF_ERROR = "OF incorrect";
        String SF_ERROR = "SF incorrect";
        String ZF_ERROR = "ZF incorrect";
        String AF_ERROR = "AF incorrect";
        String PF_ERROR = "PF incorrect";
        String CF_ERROR = "CF incorrect";

        assertFalse(OF_ERROR, cpu.getFlagValue('O'));
        assertFalse(SF_ERROR, cpu.getFlagValue('S'));
        assertFalse(ZF_ERROR, cpu.getFlagValue('Z'));
        assertFalse(AF_ERROR, cpu.getFlagValue('A'));
        assertFalse(PF_ERROR, cpu.getFlagValue('P'));
        assertFalse(CF_ERROR, cpu.getFlagValue('C'));

        // Load memory with pre-arranged values
        cpu.startDebug(); // MOV AX, 0x55AA
        cpu.startDebug(); // MOV [0000], AX
        cpu.startDebug(); // MOV [0002], AX
        cpu.startDebug(); // MOV AX, 0xAA55  ; Move AA55 into AX
        cpu.startDebug(); // MOV DX, 0xAA55  ; Move AA55 into DX  
        assertEquals(AX_ERROR, (byte) 0xAA, cpu.getRegisterValue("AX")[0]);
        assertEquals(AX_ERROR, (byte) 0x55, cpu.getRegisterValue("AX")[1]);
        assertEquals(DX_ERROR, (byte) 0xAA, cpu.getRegisterValue("DX")[0]);
        assertEquals(DX_ERROR, (byte) 0x55, cpu.getRegisterValue("DX")[1]);


        // OR reg,mem
        cpu.startDebug(); // OR AX, [BX+SI]  ; OR reg and mem, store in AX (ffff)
        assertEquals(AX_ERROR, (byte) 0xFF, cpu.getRegisterValue("AX")[0]);
        assertEquals(AX_ERROR, (byte) 0xFF, cpu.getRegisterValue("AX")[1]);
        assertTrue(SF_ERROR, cpu.getFlagValue('S'));
        assertTrue(PF_ERROR, cpu.getFlagValue('P'));
        cpu.startDebug(); // INC AX          ; Reset AX
        assertEquals(AX_ERROR, (byte) 0x00, cpu.getRegisterValue("AX")[0]);
        assertEquals(AX_ERROR, (byte) 0x00, cpu.getRegisterValue("AX")[1]);

        // OR reg,mem+8b
        cpu.startDebug(); // OR DX, [BX+DI+02]       ; OR reg and mem+8b, store in DX (ffff)
        assertEquals(DX_ERROR, (byte) 0xFF, cpu.getRegisterValue("DX")[0]);
        assertEquals(DX_ERROR, (byte) 0xFF, cpu.getRegisterValue("DX")[1]);
        assertTrue(SF_ERROR, cpu.getFlagValue('S'));
        assertTrue(PF_ERROR, cpu.getFlagValue('P'));
        cpu.startDebug(); // INC DX                  ; Reset DX
        assertEquals(DX_ERROR, (byte) 0x00, cpu.getRegisterValue("DX")[0]);
        assertEquals(DX_ERROR, (byte) 0x00, cpu.getRegisterValue("DX")[1]);

        // OR reg,mem+16b
        cpu.startDebug(); // INC BP                  ; Set BP to 1
        cpu.startDebug(); // OR DX, [BP+0x0100]      ; OR reg and mem+16b, store in AL (55aa)
        assertEquals(DX_ERROR, (byte) 0x55, cpu.getRegisterValue("DX")[0]);
        assertEquals(DX_ERROR, (byte) 0xAA, cpu.getRegisterValue("DX")[1]);
        assertFalse(SF_ERROR, cpu.getFlagValue('S'));
        assertTrue(PF_ERROR, cpu.getFlagValue('P'));

        // OR reg, reg
        cpu.startDebug(); // OR AX, BX       ; OR 2 registers (00), check flags
        assertEquals(AX_ERROR, (byte) 0x00, cpu.getRegisterValue("AX")[0]);
        assertEquals(AX_ERROR, (byte) 0x00, cpu.getRegisterValue("AX")[1]);
        assertFalse(SF_ERROR, cpu.getFlagValue('S'));
        assertTrue(ZF_ERROR, cpu.getFlagValue('Z'));
        assertTrue(PF_ERROR, cpu.getFlagValue('P'));

        cpu.startDebug(); // HLT             ; Stop execution

    }
}
