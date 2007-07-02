/*
 * $Revision: 1.1 $ $Date: 2007-07-02 14:31:27 $ $Author: blohman $
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

package nl.kbna.dioscuri.module;

/**
 * Interface representing a generic hardware module.
 *  
 */

public abstract class ModuleVideo extends ModuleDevice
{
    // Methods
    /**
     * Video read mode implementations
     */
    public abstract byte readMode(int addr);
    
    /**
     * Video write mode implementations
     * @param address
     * @param data
     */
    public abstract void writeMode(int address, byte data);
    
    /**
     * Returns a pointer to the whole video buffer
     * 
     * @return byte[] containing the video buffer
     */
    public abstract byte[] getVideoBuffer();
    
    /**
     * Returns a byte from video buffer at position index
     * 
     * @param int index
     * 
     * @return byte from video buffer
     */
    public abstract byte getVideoBufferByte(int index);
    
    /**
     * Stores a byte in video buffer at position index
     * 
     * @param int index
     * @param byte data
     */
    public abstract void setVideoBufferByte(int index, byte data);

    /**
     * Returns a byte from text snapshot at position index
     * 
     * @param int index
     * 
     * @return byte from textsnapshot
     */
    public abstract byte getTextSnapshot(int index);
    
    /**
     * Stores a byte in text snapshot at position index
     * 
     * @param int index
     * @param byte data
     */
    public abstract void setTextSnapshot(int index, byte data);
    
    /**
     * Returns a byte from attribute palette register at position index
     * 
     * @param int index
     * 
     * @return byte from register
     */
    public abstract byte getAttributePaletteRegister(int index);
}
