package dioscuri.module.cpu;

import dioscuri.AbstractInstructionTest;
import org.junit.Test;

import static org.junit.Assert.*;

public class Instruction_CMP_GbEbTest extends AbstractInstructionTest {

    public Instruction_CMP_GbEbTest() throws Exception {
        super(80448, "CMP_GbEb.bin");
    }

    /*
    * Test method for 'com.tessella.emulator.module.cpu.Instruction_CMP_GbEb.execute()'
    */
    @Test
    public void testExecute() {
        String AX_ERROR = "AX contains wrong value";
        String CX_ERROR = "CX contains wrong value";
        String DX_ERROR = "DX contains wrong value";
        String OF_ERROR = "OF incorrect";
        String SF_ERROR = "SF incorrect";
        String ZF_ERROR = "ZF incorrect";
        String AF_ERROR = "AF incorrect";
        String PF_ERROR = "PF incorrect";
        String CF_ERROR = "CF incorrect";

        // Load memory, registers with pre-arranged values (5 instructions)
        cpu.startDebug(); // MOV AX, 0x0680
        cpu.startDebug(); // MOV [0000], AX
        cpu.startDebug(); // MOV AX, 0x0085
        assertEquals(AX_ERROR, (byte) 0x00, cpu.getRegisterValue("AX")[0]);
        assertEquals(AX_ERROR, (byte) 0x85, cpu.getRegisterValue("AX")[1]);
        cpu.startDebug(); // MOV CX, 0xFF7F
        assertEquals(CX_ERROR, (byte) 0xFF, cpu.getRegisterValue("CX")[0]);
        assertEquals(CX_ERROR, (byte) 0x7F, cpu.getRegisterValue("CX")[1]);
        cpu.startDebug(); // MOV DX, 0x8586
        assertEquals(DX_ERROR, (byte) 0x85, cpu.getRegisterValue("DX")[0]);
        assertEquals(DX_ERROR, (byte) 0x86, cpu.getRegisterValue("DX")[1]);

        assertFalse(OF_ERROR, cpu.getFlagValue('O'));
        assertFalse(SF_ERROR, cpu.getFlagValue('S'));
        assertFalse(ZF_ERROR, cpu.getFlagValue('Z'));
        assertFalse(AF_ERROR, cpu.getFlagValue('A'));
        assertFalse(PF_ERROR, cpu.getFlagValue('P'));
        assertFalse(CF_ERROR, cpu.getFlagValue('C'));

        // Test CMP
        cpu.startDebug(); // CMP AH, [BX+SI]; 00 - 80, test OF, SF, CF
        assertTrue(OF_ERROR, cpu.getFlagValue('O'));
        assertTrue(SF_ERROR, cpu.getFlagValue('S'));
        assertTrue(CF_ERROR, cpu.getFlagValue('C'));

        cpu.startDebug(); // CMP AL, [BX+DI+01]      ; 85 - 06, test OF, SF, AF, PF flags
        assertTrue(OF_ERROR, cpu.getFlagValue('O'));
        assertFalse(SF_ERROR, cpu.getFlagValue('S'));
        assertTrue(AF_ERROR, cpu.getFlagValue('A'));
        assertFalse(PF_ERROR, cpu.getFlagValue('P'));
        assertFalse(CF_ERROR, cpu.getFlagValue('C'));

        cpu.startDebug(); // MOV BP, 0x0007          ; Set BP to 7
        cpu.startDebug(); // CMP DH, [BP+0x0100]     ; 85 - 85, test ZF
        assertFalse(OF_ERROR, cpu.getFlagValue('O'));
        assertFalse(SF_ERROR, cpu.getFlagValue('S'));
        assertTrue(ZF_ERROR, cpu.getFlagValue('Z'));
        assertFalse(AF_ERROR, cpu.getFlagValue('A'));
        assertTrue(PF_ERROR, cpu.getFlagValue('P'));

        cpu.startDebug(); // CMP DH, DL      ; 85 - 86, test AF, CF, overflow
        assertTrue(AF_ERROR, cpu.getFlagValue('A'));
        assertTrue(CF_ERROR, cpu.getFlagValue('C'));

        cpu.startDebug(); // CMP CL, CH      ; 7F - FF, test OF, CF
        assertTrue(OF_ERROR, cpu.getFlagValue('O'));
        assertTrue(CF_ERROR, cpu.getFlagValue('C'));

        cpu.startDebug(); // HLT         
    }
}
