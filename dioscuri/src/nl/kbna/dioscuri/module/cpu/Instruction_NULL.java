/*
 * $Revision$ $Date$ $Author$
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

import java.util.logging.Logger;

import nl.kbna.dioscuri.exception.CPUInstructionException;

public class Instruction_NULL implements Instruction {

	// Attributes
	private CPU cpu;
	
	// Logging
	private static Logger logger = Logger.getLogger("nl.kbna.dioscuri.cpu");
	
	// Constructors
	/**
	 * Construct class
	 */
	public Instruction_NULL()	{}
	
	/**
	 * Construct class
	 * @param processor
	 */
	public Instruction_NULL(CPU processor)
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
        // Throw exception for illegal nnn bits
//        byte b1 = (byte) (cpu.getByteFromCode() & 0xFF);    // Target instruction
//        System.out.println("Unknown instruction (NULL) encountered at " + cpu.getRegisterHex(0) + ":" + cpu.getRegisterHex(1) + ", next instruction=" + Integer.toHexString(b1));
        throw new CPUInstructionException("Unknown instruction (NULL) encountered at " + cpu.getRegisterHex(0) + ":" + cpu.getRegisterHex(1));
	}
}