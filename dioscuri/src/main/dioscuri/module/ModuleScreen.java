/* $Revision: 160 $ $Date: 2009-08-17 12:56:40 +0000 (ma, 17 aug 2009) $ $Author: blohman $ 
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

package dioscuri.module;

import dioscuri.interfaces.Module;

import javax.swing.*;

/**
 * Abstract class representing a generic screen module.
 */
public abstract class ModuleScreen extends AbstractModule {

    /**
     *
     */
    public ModuleScreen() {
        super(Module.Type.SCREEN,
                Module.Type.VIDEO);
    }

    /**
     * Return a reference to the actual screen
     *
     * @return -
     */
    public abstract JPanel getScreen();

    /**
     * Clear screen from any output
     */
    public abstract void clearScreen();

    /**
     * Return the number of rows on screen (text based)
     *
     * @return -
     */
    public abstract int getScreenRows();

    /**
     * Return the number of columns on screen (text based)
     *
     * @return -
     */
    public abstract int getScreenColumns();

    /**
     * Return width of screen in number of pixels
     *
     * @return -
     */
    public abstract int getScreenWidth();

    /**
     * Return height of screen in number of pixels
     *
     * @return -
     */
    public abstract int getScreenHeight();

    /**
     * Set the screen size in number of pixels
     *
     * @param width
     * @param height
     */
    public abstract void setScreenSize(int width, int height);

    /**
     * Update screen size
     *
     * @param screenWidth
     * @param screenHeight
     * @param fontWidth
     * @param fontHeight
     */
    public abstract void updateScreenSize(int screenWidth, int screenHeight,
                                          int fontWidth, int fontHeight);

    /**
     * Update the code page The code page is the character encoding table
     *
     * @param startAddress
     */
    public abstract void updateCodePage(int startAddress);

    /**
     * Set a byte in Code page The code page is the character encoding table
     *
     * @param index
     * @param data
     */
    public abstract void setByteInCodePage(int index, byte data);

    /**
     * Set a particular colour in palette with RGB-values
     *
     * @param index
     * @param red
     * @param green
     * @param blue
     * @return -
     */
    public abstract boolean setPaletteColour(byte index, int red, int green, int blue);

    /**
     * Update a tile on screen with given bytes Graphics mode. A tile is a part
     * of the screenbuffer
     *
     * @param tile
     * @param startPositionX
     * @param startPositionY
     */
    public abstract void updateGraphicsTile(byte[] tile, int startPositionX,
                                            int startPositionY);

    /**
     * Update text on screen at given position Text mode. Selected text will
     * replace existing text at given position
     *
     * @param oldText
     * @param newText
     * @param cursorYPos
     * @param cursorXPos
     * @param numberOfRows
     * @param textModeAttribs
     */
    public abstract void updateText(int oldText, int newText, long cursorXPos,
                                    long cursorYPos, short[] textModeAttribs, int numberOfRows);
}
