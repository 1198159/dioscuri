/*
 * $Revision: 1.2 $ $Date: 2007-08-27 07:47:13 $ $Author: blohman $
 * 
 * Copyright (C) 2007  National Library of the Netherlands, Nationaal Archief of the Netherlands
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * For more information about this project, visit
 * http://dioscuri.sourceforge.net/
 * or contact us via email:
 * jrvanderhoeven at users.sourceforge.net
 * blohman at users.sourceforge.net
 * 
 * Developed by:
 * Nationaal Archief               <www.nationaalarchief.nl>
 * Koninklijke Bibliotheek         <www.kb.nl>
 * Tessella Support Services plc   <www.tessella.com>
 *
 * Project Title: DIOSCURI
 *
 */

package nl.kbna.dioscuri.module.cpu;

	/**
	 * Intel opcode 35<BR>
	 * Logical XOR of immediate word and AX.<BR>
	 * Flags modified: OF, SF, ZF, AF, PF, CF
	 */
public class Instruction_XOR_AXIv implements Instruction {

	// Attributes
	private CPU cpu;
	
	// Constructors
	/**
	 * Class constructor
	 */
	public Instruction_XOR_AXIv()	{}
	
	/**
	 * Class constructor specifying processor reference
	 * 
	 * @param processor	Reference to CPU class
	 */
	public Instruction_XOR_AXIv(CPU processor)
	{
		this();
		
		// Create reference to cpu class
		cpu = processor;
	}

	
	// Methods
	
	/**
	 * Logical XOR of immediate word and AL.<BR>
	 * OF and CF are cleared. AF is undefined.
	 */
	public void execute()
	{
		
		// Reset appropriate flags
		cpu.flags[CPU.REGISTER_FLAGS_OF] = false;
		cpu.flags[CPU.REGISTER_FLAGS_CF] = false;
		// Intel docs state AF remains undefined, but MS-DOS debug.exe clears AF
		cpu.flags[CPU.REGISTER_FLAGS_AF] = false;
		
		// Get immediate byte and XOR with AL, storing result in AL.
		cpu.ax[CPU.REGISTER_GENERAL_LOW] ^= cpu.getByteFromCode();
		// Get immediate byte and XOR with AH, storing result in AH.
		cpu.ax[CPU.REGISTER_GENERAL_HIGH] ^= cpu.getByteFromCode();

        if (cpu.doubleWord)
        {
            // Get immediate byte and XOR with eAL, storing result in eAL.
            cpu.eax[CPU.REGISTER_GENERAL_LOW] ^= cpu.getByteFromCode();
            // Get immediate byte and XOR with eAH, storing result in eAH.
            cpu.eax[CPU.REGISTER_GENERAL_HIGH] ^= cpu.getByteFromCode();
        }
		
		// Test ZF
		cpu.flags[CPU.REGISTER_FLAGS_ZF] = cpu.ax[CPU.REGISTER_GENERAL_HIGH] == 0 && cpu.ax[CPU.REGISTER_GENERAL_LOW] == 0 ? true : false;
		// Test SF (set when MSB is 1, occurs when AH >= 0x80)
		cpu.flags[CPU.REGISTER_FLAGS_SF] = cpu.ax[CPU.REGISTER_GENERAL_HIGH] < 0 ? true : false;
		// Set PF, only applies to AL
		cpu.flags[CPU.REGISTER_FLAGS_PF] = Util.checkParityOfByte(cpu.ax[CPU.REGISTER_GENERAL_LOW]);
        
        if (cpu.doubleWord)
        {
            // Need to check extended registers with flags
            // Test ZF
            cpu.flags[CPU.REGISTER_FLAGS_ZF] = cpu.flags[CPU.REGISTER_FLAGS_ZF] && cpu.eax[CPU.REGISTER_GENERAL_HIGH] == 0 && cpu.eax[CPU.REGISTER_GENERAL_LOW] == 0 ? true : false;
            // Test SF (set when MSB is 1, occurs when AH >= 0x80)
            cpu.flags[CPU.REGISTER_FLAGS_SF] = cpu.eax[CPU.REGISTER_GENERAL_HIGH] < 0 ? true : false;
        }
	}
}
