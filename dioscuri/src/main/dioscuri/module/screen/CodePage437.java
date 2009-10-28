/* $Revision: 159 $ $Date: 2009-08-17 12:52:56 +0000 (ma, 17 aug 2009) $ $Author: blohman $ 
 * 
 * Copyright (C) 2007-2009  National Library of the Netherlands, 
 *                          Nationaal Archief of the Netherlands, 
 *                          Planets
 *                          KEEP
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 *
 * For more information about this project, visit
 * http://dioscuri.sourceforge.net/
 * or contact us via email:
 *   jrvanderhoeven at users.sourceforge.net
 *   blohman at users.sourceforge.net
 *   bkiers at users.sourceforge.net
 * 
 * Developed by:
 *   Nationaal Archief               <www.nationaalarchief.nl>
 *   Koninklijke Bibliotheek         <www.kb.nl>
 *   Tessella Support Services plc   <www.tessella.com>
 *   Planets                         <www.planets-project.eu>
 *   KEEP                            <www.keep-project.eu>
 * 
 * Project Title: DIOSCURI
 */

package dioscuri.module.screen;

// The table below was taken from Bochs code (http://bochs.sourceforge.net/), vga.bitmap.h

/**
 * 16-byte/character representation of the original character set of the IBM PC (ca. 1981)
 * See http://en.wikipedia.org/wiki/Code_page_437 for a complete description
 *
 */
public class CodePage437
{
    
    //  Initial font map; each row is a 16-byte representation of a character from code page 437
    final static int[][] codePageArray = new int[][]{ 
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x7e, 0x81, 0xa5, 0x81, 0x81, 0xa5, 0x99, 0x81, 0x81, 0x7e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x7e, 0xff, 0xdb, 0xff, 0xff, 0xdb, 0xe7, 0xff, 0xff, 0x7e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x36, 0x7f, 0x7f, 0x7f, 0x7f, 0x3e, 0x1c, 0x08, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x08, 0x1c, 0x3e, 0x7f, 0x3e, 0x1c, 0x08, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x18, 0x3c, 0x3c, 0xe7, 0xe7, 0xe7, 0x18, 0x18, 0x3c, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x18, 0x3c, 0x7e, 0xff, 0xff, 0x7e, 0x18, 0x18, 0x3c, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x18, 0x3c, 0x3c, 0x18, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xe7, 0xc3, 0xc3, 0xe7, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x3c, 0x66, 0x42, 0x42, 0x66, 0x3c, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0xff, 0xff, 0xff, 0xff, 0xff, 0xc3, 0x99, 0xbd, 0xbd, 0x99, 0xc3, 0xff, 0xff, 0xff, 0xff, 0xff },
        { 0x00, 0x00, 0x78, 0x60, 0x70, 0x58, 0x1e, 0x33, 0x33, 0x33, 0x33, 0x1e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x3c, 0x66, 0x66, 0x66, 0x66, 0x3c, 0x18, 0x7e, 0x18, 0x18, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0xfc, 0xcc, 0xfc, 0x0c, 0x0c, 0x0c, 0x0c, 0x0e, 0x0f, 0x07, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0xfe, 0xc6, 0xfe, 0xc6, 0xc6, 0xc6, 0xc6, 0xe6, 0xe7, 0x67, 0x03, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x18, 0x18, 0xdb, 0x3c, 0xe7, 0x3c, 0xdb, 0x18, 0x18, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x01, 0x03, 0x07, 0x0f, 0x1f, 0x7f, 0x1f, 0x0f, 0x07, 0x03, 0x01, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x40, 0x60, 0x70, 0x78, 0x7c, 0x7f, 0x7c, 0x78, 0x70, 0x60, 0x40, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x18, 0x3c, 0x7e, 0x18, 0x18, 0x18, 0x7e, 0x3c, 0x18, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x66, 0x66, 0x66, 0x66, 0x66, 0x66, 0x66, 0x00, 0x66, 0x66, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0xfe, 0xdb, 0xdb, 0xdb, 0xde, 0xd8, 0xd8, 0xd8, 0xd8, 0xd8, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x3e, 0x63, 0x06, 0x1c, 0x36, 0x63, 0x63, 0x36, 0x1c, 0x30, 0x63, 0x3e, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x7f, 0x7f, 0x7f, 0x7f, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x18, 0x3c, 0x7e, 0x18, 0x18, 0x18, 0x7e, 0x3c, 0x18, 0x7e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x18, 0x3c, 0x7e, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x7e, 0x3c, 0x18, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x18, 0x30, 0x7f, 0x30, 0x18, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x0c, 0x06, 0x7f, 0x06, 0x0c, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x03, 0x03, 0x03, 0x7f, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x14, 0x36, 0x7f, 0x36, 0x14, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x08, 0x1c, 0x1c, 0x3e, 0x3e, 0x7f, 0x7f, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x7f, 0x7f, 0x3e, 0x3e, 0x1c, 0x1c, 0x08, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x18, 0x3c, 0x3c, 0x3c, 0x18, 0x18, 0x18, 0x00, 0x18, 0x18, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x66, 0x66, 0x66, 0x24, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x36, 0x36, 0x7f, 0x36, 0x36, 0x36, 0x7f, 0x36, 0x36, 0x00, 0x00, 0x00, 0x00 },
        { 0x18, 0x18, 0x3e, 0x63, 0x43, 0x03, 0x3e, 0x60, 0x60, 0x61, 0x63, 0x3e, 0x18, 0x18, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x43, 0x63, 0x30, 0x18, 0x0c, 0x06, 0x63, 0x61, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x1c, 0x36, 0x36, 0x1c, 0x6e, 0x3b, 0x33, 0x33, 0x33, 0x6e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x0c, 0x0c, 0x0c, 0x06, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x30, 0x18, 0x0c, 0x0c, 0x0c, 0x0c, 0x0c, 0x0c, 0x18, 0x30, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x0c, 0x18, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x18, 0x0c, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x66, 0x3c, 0xff, 0x3c, 0x66, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x18, 0x18, 0x7e, 0x18, 0x18, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x18, 0x18, 0x18, 0x0c, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x7f, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x18, 0x18, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x40, 0x60, 0x30, 0x18, 0x0c, 0x06, 0x03, 0x01, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x1c, 0x36, 0x63, 0x63, 0x6b, 0x6b, 0x63, 0x63, 0x36, 0x1c, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x18, 0x1c, 0x1e, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x7e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x3e, 0x63, 0x60, 0x30, 0x18, 0x0c, 0x06, 0x03, 0x63, 0x7f, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x3e, 0x63, 0x60, 0x60, 0x3c, 0x60, 0x60, 0x60, 0x63, 0x3e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x30, 0x38, 0x3c, 0x36, 0x33, 0x7f, 0x30, 0x30, 0x30, 0x78, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x7f, 0x03, 0x03, 0x03, 0x3f, 0x60, 0x60, 0x60, 0x63, 0x3e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x1c, 0x06, 0x03, 0x03, 0x3f, 0x63, 0x63, 0x63, 0x63, 0x3e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x7f, 0x63, 0x60, 0x60, 0x30, 0x18, 0x0c, 0x0c, 0x0c, 0x0c, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x3e, 0x63, 0x63, 0x63, 0x3e, 0x63, 0x63, 0x63, 0x63, 0x3e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x3e, 0x63, 0x63, 0x63, 0x7e, 0x60, 0x60, 0x60, 0x30, 0x1e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x18, 0x18, 0x00, 0x00, 0x00, 0x18, 0x18, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x18, 0x18, 0x00, 0x00, 0x00, 0x18, 0x18, 0x0c, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x60, 0x30, 0x18, 0x0c, 0x06, 0x0c, 0x18, 0x30, 0x60, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x7e, 0x00, 0x00, 0x7e, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x06, 0x0c, 0x18, 0x30, 0x60, 0x30, 0x18, 0x0c, 0x06, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x3e, 0x63, 0x63, 0x30, 0x18, 0x18, 0x18, 0x00, 0x18, 0x18, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x3e, 0x63, 0x63, 0x7b, 0x7b, 0x7b, 0x3b, 0x03, 0x3e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x08, 0x1c, 0x36, 0x63, 0x63, 0x7f, 0x63, 0x63, 0x63, 0x63, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x3f, 0x66, 0x66, 0x66, 0x3e, 0x66, 0x66, 0x66, 0x66, 0x3f, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x3c, 0x66, 0x43, 0x03, 0x03, 0x03, 0x03, 0x43, 0x66, 0x3c, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x1f, 0x36, 0x66, 0x66, 0x66, 0x66, 0x66, 0x66, 0x36, 0x1f, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x7f, 0x66, 0x46, 0x16, 0x1e, 0x16, 0x06, 0x46, 0x66, 0x7f, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x7f, 0x66, 0x46, 0x16, 0x1e, 0x16, 0x06, 0x06, 0x06, 0x0f, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x3c, 0x66, 0x43, 0x03, 0x03, 0x7b, 0x63, 0x63, 0x66, 0x5c, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x63, 0x63, 0x63, 0x63, 0x7f, 0x63, 0x63, 0x63, 0x63, 0x63, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x3c, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x3c, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x78, 0x30, 0x30, 0x30, 0x30, 0x30, 0x33, 0x33, 0x33, 0x1e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x67, 0x66, 0x66, 0x36, 0x1e, 0x1e, 0x36, 0x66, 0x66, 0x67, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x0f, 0x06, 0x06, 0x06, 0x06, 0x06, 0x06, 0x46, 0x66, 0x7f, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x63, 0x77, 0x7f, 0x7f, 0x6b, 0x63, 0x63, 0x63, 0x63, 0x63, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x63, 0x67, 0x6f, 0x7f, 0x7b, 0x73, 0x63, 0x63, 0x63, 0x63, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x3e, 0x63, 0x63, 0x63, 0x63, 0x63, 0x63, 0x63, 0x63, 0x3e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x3f, 0x66, 0x66, 0x66, 0x3e, 0x06, 0x06, 0x06, 0x06, 0x0f, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x3e, 0x63, 0x63, 0x63, 0x63, 0x63, 0x63, 0x6b, 0x7b, 0x3e, 0x30, 0x70, 0x00, 0x00 },
        { 0x00, 0x00, 0x3f, 0x66, 0x66, 0x66, 0x3e, 0x36, 0x66, 0x66, 0x66, 0x67, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x3e, 0x63, 0x63, 0x06, 0x1c, 0x30, 0x60, 0x63, 0x63, 0x3e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x7e, 0x7e, 0x5a, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x3c, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x63, 0x63, 0x63, 0x63, 0x63, 0x63, 0x63, 0x63, 0x63, 0x3e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x63, 0x63, 0x63, 0x63, 0x63, 0x63, 0x63, 0x36, 0x1c, 0x08, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x63, 0x63, 0x63, 0x63, 0x6b, 0x6b, 0x6b, 0x7f, 0x77, 0x36, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x63, 0x63, 0x36, 0x3e, 0x1c, 0x1c, 0x3e, 0x36, 0x63, 0x63, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x66, 0x66, 0x66, 0x66, 0x3c, 0x18, 0x18, 0x18, 0x18, 0x3c, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x7f, 0x63, 0x61, 0x30, 0x18, 0x0c, 0x06, 0x43, 0x63, 0x7f, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x3c, 0x0c, 0x0c, 0x0c, 0x0c, 0x0c, 0x0c, 0x0c, 0x0c, 0x3c, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x01, 0x03, 0x07, 0x0e, 0x1c, 0x38, 0x70, 0x60, 0x40, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x3c, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x3c, 0x00, 0x00, 0x00, 0x00 },
        { 0x08, 0x1c, 0x36, 0x63, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xff, 0x00, 0x00 },
        { 0x0c, 0x0c, 0x18, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x1e, 0x30, 0x3e, 0x33, 0x33, 0x33, 0x6e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x07, 0x06, 0x06, 0x1e, 0x36, 0x66, 0x66, 0x66, 0x66, 0x3e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x3e, 0x63, 0x03, 0x03, 0x03, 0x63, 0x3e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x38, 0x30, 0x30, 0x3c, 0x36, 0x33, 0x33, 0x33, 0x33, 0x6e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x3e, 0x63, 0x7f, 0x03, 0x03, 0x63, 0x3e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x1c, 0x36, 0x26, 0x06, 0x0f, 0x06, 0x06, 0x06, 0x06, 0x0f, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x6e, 0x33, 0x33, 0x33, 0x33, 0x33, 0x3e, 0x30, 0x33, 0x1e, 0x00 },
        { 0x00, 0x00, 0x07, 0x06, 0x06, 0x36, 0x6e, 0x66, 0x66, 0x66, 0x66, 0x67, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x18, 0x18, 0x00, 0x1c, 0x18, 0x18, 0x18, 0x18, 0x18, 0x3c, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x60, 0x60, 0x00, 0x70, 0x60, 0x60, 0x60, 0x60, 0x60, 0x60, 0x66, 0x66, 0x3c, 0x00 },
        { 0x00, 0x00, 0x07, 0x06, 0x06, 0x66, 0x36, 0x1e, 0x1e, 0x36, 0x66, 0x67, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x1c, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x3c, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x37, 0x7f, 0x6b, 0x6b, 0x6b, 0x6b, 0x63, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x3b, 0x66, 0x66, 0x66, 0x66, 0x66, 0x66, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x3e, 0x63, 0x63, 0x63, 0x63, 0x63, 0x3e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x3b, 0x66, 0x66, 0x66, 0x66, 0x66, 0x3e, 0x06, 0x06, 0x0f, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x6e, 0x33, 0x33, 0x33, 0x33, 0x33, 0x3e, 0x30, 0x30, 0x78, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x3b, 0x6e, 0x66, 0x06, 0x06, 0x06, 0x0f, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x3e, 0x63, 0x06, 0x1c, 0x30, 0x63, 0x3e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x08, 0x0c, 0x0c, 0x3f, 0x0c, 0x0c, 0x0c, 0x0c, 0x6c, 0x38, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x33, 0x33, 0x33, 0x33, 0x33, 0x33, 0x6e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x66, 0x66, 0x66, 0x66, 0x66, 0x3c, 0x18, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x63, 0x63, 0x6b, 0x6b, 0x6b, 0x7f, 0x36, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x63, 0x36, 0x1c, 0x1c, 0x1c, 0x36, 0x63, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x63, 0x63, 0x63, 0x63, 0x63, 0x63, 0x7e, 0x60, 0x30, 0x1f, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x7f, 0x33, 0x18, 0x0c, 0x06, 0x63, 0x7f, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x70, 0x18, 0x18, 0x18, 0x0e, 0x18, 0x18, 0x18, 0x18, 0x70, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x18, 0x18, 0x18, 0x18, 0x00, 0x18, 0x18, 0x18, 0x18, 0x18, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x0e, 0x18, 0x18, 0x18, 0x70, 0x18, 0x18, 0x18, 0x18, 0x0e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x6e, 0x3b, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x08, 0x1c, 0x36, 0x63, 0x63, 0x63, 0x7f, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x3c, 0x66, 0x43, 0x03, 0x03, 0x03, 0x43, 0x66, 0x3c, 0x30, 0x60, 0x3e, 0x00, 0x00 },
        { 0x00, 0x00, 0x33, 0x00, 0x00, 0x33, 0x33, 0x33, 0x33, 0x33, 0x33, 0x6e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x30, 0x18, 0x0c, 0x00, 0x3e, 0x63, 0x7f, 0x03, 0x03, 0x63, 0x3e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x08, 0x1c, 0x36, 0x00, 0x1e, 0x30, 0x3e, 0x33, 0x33, 0x33, 0x6e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x33, 0x00, 0x00, 0x1e, 0x30, 0x3e, 0x33, 0x33, 0x33, 0x6e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x06, 0x0c, 0x18, 0x00, 0x1e, 0x30, 0x3e, 0x33, 0x33, 0x33, 0x6e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x1c, 0x36, 0x1c, 0x00, 0x1e, 0x30, 0x3e, 0x33, 0x33, 0x33, 0x6e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x3c, 0x66, 0x06, 0x06, 0x66, 0x3c, 0x30, 0x60, 0x3c, 0x00, 0x00, 0x00 },
        { 0x00, 0x08, 0x1c, 0x36, 0x00, 0x3e, 0x63, 0x7f, 0x03, 0x03, 0x63, 0x3e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x63, 0x00, 0x00, 0x3e, 0x63, 0x7f, 0x03, 0x03, 0x63, 0x3e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x06, 0x0c, 0x18, 0x00, 0x3e, 0x63, 0x7f, 0x03, 0x03, 0x63, 0x3e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x66, 0x00, 0x00, 0x1c, 0x18, 0x18, 0x18, 0x18, 0x18, 0x3c, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x18, 0x3c, 0x66, 0x00, 0x1c, 0x18, 0x18, 0x18, 0x18, 0x18, 0x3c, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x06, 0x0c, 0x18, 0x00, 0x1c, 0x18, 0x18, 0x18, 0x18, 0x18, 0x3c, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x63, 0x00, 0x08, 0x1c, 0x36, 0x63, 0x63, 0x7f, 0x63, 0x63, 0x63, 0x00, 0x00, 0x00, 0x00 },
        { 0x1c, 0x36, 0x1c, 0x00, 0x1c, 0x36, 0x63, 0x63, 0x7f, 0x63, 0x63, 0x63, 0x00, 0x00, 0x00, 0x00 },
        { 0x18, 0x0c, 0x06, 0x00, 0x7f, 0x66, 0x06, 0x3e, 0x06, 0x06, 0x66, 0x7f, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x33, 0x6e, 0x6c, 0x7e, 0x1b, 0x1b, 0x76, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x7c, 0x36, 0x33, 0x33, 0x7f, 0x33, 0x33, 0x33, 0x33, 0x73, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x08, 0x1c, 0x36, 0x00, 0x3e, 0x63, 0x63, 0x63, 0x63, 0x63, 0x3e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x63, 0x00, 0x00, 0x3e, 0x63, 0x63, 0x63, 0x63, 0x63, 0x3e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x06, 0x0c, 0x18, 0x00, 0x3e, 0x63, 0x63, 0x63, 0x63, 0x63, 0x3e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x0c, 0x1e, 0x33, 0x00, 0x33, 0x33, 0x33, 0x33, 0x33, 0x33, 0x6e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x06, 0x0c, 0x18, 0x00, 0x33, 0x33, 0x33, 0x33, 0x33, 0x33, 0x6e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x63, 0x00, 0x00, 0x63, 0x63, 0x63, 0x63, 0x63, 0x63, 0x7e, 0x60, 0x30, 0x1e, 0x00 },
        { 0x00, 0x63, 0x00, 0x3e, 0x63, 0x63, 0x63, 0x63, 0x63, 0x63, 0x63, 0x3e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x63, 0x00, 0x63, 0x63, 0x63, 0x63, 0x63, 0x63, 0x63, 0x63, 0x3e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x18, 0x18, 0x3c, 0x66, 0x06, 0x06, 0x06, 0x66, 0x3c, 0x18, 0x18, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x1c, 0x36, 0x26, 0x06, 0x0f, 0x06, 0x06, 0x06, 0x06, 0x67, 0x3f, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x66, 0x66, 0x3c, 0x18, 0x7e, 0x18, 0x7e, 0x18, 0x18, 0x18, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x1f, 0x33, 0x33, 0x1f, 0x23, 0x33, 0x7b, 0x33, 0x33, 0x33, 0x63, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x70, 0xd8, 0x18, 0x18, 0x18, 0x7e, 0x18, 0x18, 0x18, 0x18, 0x18, 0x1b, 0x0e, 0x00, 0x00 },
        { 0x00, 0x18, 0x0c, 0x06, 0x00, 0x1e, 0x30, 0x3e, 0x33, 0x33, 0x33, 0x6e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x30, 0x18, 0x0c, 0x00, 0x1c, 0x18, 0x18, 0x18, 0x18, 0x18, 0x3c, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x18, 0x0c, 0x06, 0x00, 0x3e, 0x63, 0x63, 0x63, 0x63, 0x63, 0x3e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x18, 0x0c, 0x06, 0x00, 0x33, 0x33, 0x33, 0x33, 0x33, 0x33, 0x6e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x6e, 0x3b, 0x00, 0x3b, 0x66, 0x66, 0x66, 0x66, 0x66, 0x66, 0x00, 0x00, 0x00, 0x00 },
        { 0x6e, 0x3b, 0x00, 0x63, 0x67, 0x6f, 0x7f, 0x7b, 0x73, 0x63, 0x63, 0x63, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x3c, 0x36, 0x36, 0x7c, 0x00, 0x7e, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x1c, 0x36, 0x36, 0x1c, 0x00, 0x3e, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x0c, 0x0c, 0x00, 0x0c, 0x0c, 0x06, 0x03, 0x63, 0x63, 0x3e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x7f, 0x03, 0x03, 0x03, 0x03, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x7f, 0x60, 0x60, 0x60, 0x60, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x03, 0x03, 0x43, 0x63, 0x33, 0x18, 0x0c, 0x06, 0x3b, 0x61, 0x30, 0x18, 0x7c, 0x00, 0x00 },
        { 0x00, 0x03, 0x03, 0x43, 0x63, 0x33, 0x18, 0x0c, 0x66, 0x73, 0x79, 0x7c, 0x60, 0x60, 0x00, 0x00 },
        { 0x00, 0x00, 0x18, 0x18, 0x00, 0x18, 0x18, 0x18, 0x3c, 0x3c, 0x3c, 0x18, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x6c, 0x36, 0x1b, 0x36, 0x6c, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x1b, 0x36, 0x6c, 0x36, 0x1b, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x88, 0x22, 0x88, 0x22, 0x88, 0x22, 0x88, 0x22, 0x88, 0x22, 0x88, 0x22, 0x88, 0x22, 0x88, 0x22 },
        { 0xaa, 0x55, 0xaa, 0x55, 0xaa, 0x55, 0xaa, 0x55, 0xaa, 0x55, 0xaa, 0x55, 0xaa, 0x55, 0xaa, 0x55 },
        { 0xbb, 0xee, 0xbb, 0xee, 0xbb, 0xee, 0xbb, 0xee, 0xbb, 0xee, 0xbb, 0xee, 0xbb, 0xee, 0xbb, 0xee },
        { 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18 },
        { 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x1f, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18 },
        { 0x18, 0x18, 0x18, 0x18, 0x18, 0x1f, 0x18, 0x1f, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18 },
        { 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6f, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x7f, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x1f, 0x18, 0x1f, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18 },
        { 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6f, 0x60, 0x6f, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c },
        { 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x7f, 0x60, 0x6f, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c },
        { 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6f, 0x60, 0x7f, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x7f, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x18, 0x18, 0x18, 0x18, 0x18, 0x1f, 0x18, 0x1f, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x1f, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18 },
        { 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0xf8, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0xff, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xff, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18 },
        { 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0xf8, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xff, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0xff, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18 },
        { 0x18, 0x18, 0x18, 0x18, 0x18, 0xf8, 0x18, 0xf8, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18 },
        { 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0xec, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c },
        { 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0xec, 0x0c, 0xfc, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0xfc, 0x0c, 0xec, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c },
        { 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0xef, 0x00, 0xff, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0xff, 0x00, 0xef, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c },
        { 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0xec, 0x0c, 0xec, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0xff, 0x00, 0xff, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0xef, 0x00, 0xef, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c },
        { 0x18, 0x18, 0x18, 0x18, 0x18, 0xff, 0x00, 0xff, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0xff, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0xff, 0x00, 0xff, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xff, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c },
        { 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0xfc, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x18, 0x18, 0x18, 0x18, 0x18, 0xf8, 0x18, 0xf8, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0xf8, 0x18, 0xf8, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xfc, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c },
        { 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0xff, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c, 0x6c },
        { 0x18, 0x18, 0x18, 0x18, 0x18, 0xff, 0x18, 0xff, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18 },
        { 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x1f, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xf8, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18 },
        { 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff },
        { 0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 0x0f },
        { 0xf0, 0xf0, 0xf0, 0xf0, 0xf0, 0xf0, 0xf0, 0xf0, 0xf0, 0xf0, 0xf0, 0xf0, 0xf0, 0xf0, 0xf0, 0xf0 },
        { 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x6e, 0x3b, 0x1b, 0x1b, 0x1b, 0x3b, 0x6e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x1e, 0x33, 0x33, 0x33, 0x1b, 0x33, 0x63, 0x63, 0x63, 0x33, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x7f, 0x63, 0x63, 0x03, 0x03, 0x03, 0x03, 0x03, 0x03, 0x03, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x7f, 0x36, 0x36, 0x36, 0x36, 0x36, 0x36, 0x36, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x7f, 0x63, 0x06, 0x0c, 0x18, 0x0c, 0x06, 0x63, 0x7f, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x7e, 0x1b, 0x1b, 0x1b, 0x1b, 0x1b, 0x0e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x66, 0x66, 0x66, 0x66, 0x66, 0x3e, 0x06, 0x06, 0x03, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x6e, 0x3b, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x7e, 0x18, 0x3c, 0x66, 0x66, 0x66, 0x3c, 0x18, 0x7e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x1c, 0x36, 0x63, 0x63, 0x7f, 0x63, 0x63, 0x36, 0x1c, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x1c, 0x36, 0x63, 0x63, 0x63, 0x36, 0x36, 0x36, 0x36, 0x77, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x78, 0x0c, 0x18, 0x30, 0x7c, 0x66, 0x66, 0x66, 0x66, 0x3c, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x7e, 0xdb, 0xdb, 0xdb, 0x7e, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0xc0, 0x60, 0x7e, 0xdb, 0xdb, 0xcf, 0x7e, 0x06, 0x03, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x38, 0x0c, 0x06, 0x06, 0x3e, 0x06, 0x06, 0x06, 0x0c, 0x38, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x3e, 0x63, 0x63, 0x63, 0x63, 0x63, 0x63, 0x63, 0x63, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x7f, 0x00, 0x00, 0x7f, 0x00, 0x00, 0x7f, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x18, 0x18, 0x7e, 0x18, 0x18, 0x00, 0x00, 0xff, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x0c, 0x18, 0x30, 0x60, 0x30, 0x18, 0x0c, 0x00, 0x7e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x30, 0x18, 0x0c, 0x06, 0x0c, 0x18, 0x30, 0x00, 0x7e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x70, 0xd8, 0xd8, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18 },
        { 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x18, 0x1b, 0x1b, 0x1b, 0x0e, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x18, 0x18, 0x00, 0x7e, 0x00, 0x18, 0x18, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x6e, 0x3b, 0x00, 0x6e, 0x3b, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x1c, 0x36, 0x36, 0x1c, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x18, 0x18, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x18, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0xf0, 0x30, 0x30, 0x30, 0x30, 0x30, 0x37, 0x36, 0x36, 0x3c, 0x38, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x1b, 0x36, 0x36, 0x36, 0x36, 0x36, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x0e, 0x1b, 0x0c, 0x06, 0x13, 0x1f, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x3e, 0x3e, 0x3e, 0x3e, 0x3e, 0x3e, 0x3e, 0x00, 0x00, 0x00, 0x00, 0x00 },
        { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
        };
    }
