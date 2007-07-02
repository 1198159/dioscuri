/*
 * $Revision: 1.1 $ $Date: 2007-07-02 14:31:38 $ $Author: blohman $
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

import nl.kbna.dioscuri.exception.CPUInstructionException;

/**
 * Intel opcode 64<BR>
 * Segment selector FS. Override the segment selector for the next opcode.<BR>
 * Flags modified: none
 */
public class Instruction_SEG_FS implements Instruction {

	// Attributes
	private CPU cpu;
	
    
	// Constructors
	/**
	 * Construct class
	 */
	public Instruction_SEG_FS()	{}
	
	/**
	 * Construct class
	 * @param processor
	 */
	public Instruction_SEG_FS(CPU processor)
	{
		this();
		
		// Create reference to cpu class
		cpu = processor;
	}

	
	// Methods
	
	/**
	 * Execute instruction
	 * @throws CPUInstructionException 
	 */
	public void execute() throws CPUInstructionException
	{
        // FIXME: FS and GS are undefined for the 80186. So do nothing here,
        // which is likely to lead to disastrous results...
        throw new CPUInstructionException("Segment FS override not implemented (not part of 8086 instruction set");
	}
}
