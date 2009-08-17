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

import java.util.logging.Level;
import java.util.logging.Logger;

	/**
	 * Intel opcode 0E<BR>
	 * Push general register CS onto stack SS:SP.<BR>
	 * Flags modified: none
	 */
public class Instruction_PUSH_CS implements Instruction {

	// Attributes
	private CPU cpu;
	
	// Logging
	private static Logger logger = Logger.getLogger("nl.kbna.dioscuri.module.cpu");

	
	// Constructors
	/**
	 * Class constructor 
	 * 
	 */
	public Instruction_PUSH_CS()	{}
	
	/**
	 * Class constructor specifying processor reference
	 * 
	 * @param processor	Reference to CPU class
	 */
	public Instruction_PUSH_CS(CPU processor)
	{
		this();
		
		// Create reference to cpu class
		cpu = processor;
	}

	
	// Methods
	
	/**
	 * This pushes the word in CS onto stack top SS:SP
	 */
	public void execute()
	{
        // Push extra register first, if 32 bit instruction
        if (cpu.doubleWord)
        {
        	logger.log(Level.WARNING, "[" + cpu.getType() + "] Instruction PUSH_CS: 32-bits not supported");
        }      

		// Get word at CS and assign to SS:SP 
		cpu.setWordToStack(cpu.cs);
	}
}