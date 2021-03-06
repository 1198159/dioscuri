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

import dioscuri.Emulator;
import dioscuri.interfaces.Module;
import dioscuri.module.ModuleScreen;
import dioscuri.module.ModuleVideo;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An implementation of a hardware visual screen module.
 *
 * @see dioscuri.module.AbstractModule
 *      <p/>
 *      Metadata module ********************************************
 *      general.type : screen general.name : Compatible CRT/LCD computer screen
 *      general.architecture : Von Neumann general.description : General
 *      implementation of a monitor. general.creator : Tessella Support
 *      Services, Koninklijke Bibliotheek, Nationaal Archief of the Netherlands
 *      general.version : 1.0 general.keywords : screen, monitor, CRT, TFT, LCD
 *      general.relations : video general.yearOfIntroduction :
 *      general.yearOfEnding : general.ancestor : general.successor :
 *      screen.resolutionRange : unlimited screen.colorDepth : 256
 */
public class Screen extends ModuleScreen {

    // Attributes
    private ScreenPanel screenPanel;

    // TODO: these properties should be set by configuration data of ESD
    // Data and color properties
    protected byte[] pixels = null; // Array of pixel data, used in databuffer
    protected DataBuffer dataBuffer = null; // Databuffer holding pixel data,
    // used in raster
    protected SampleModel sampleModel = null; // Samplemodel holding pixel
    // storage info, used in raster
    protected WritableRaster raster = null; // Raster containing image data,
    // used in BufferedImage
    protected byte[][] palette = new byte[3][256]; // Array holding red, green,
    // blue values for ColorModel
    protected ColorModel colourModel = null; // ColourModel used in
    // BufferedImage
    protected BufferedImage image = null; // Image displayed on canvas
    protected int imageType; // Defines the type of the created image

    // Text font
    protected BufferedImage[] fontImages = new BufferedImage[256]; // Array
    // containing
    // font
    // images

    // Graphics tile
    protected BufferedImage graphicTile; // Tile used for updating in graphics
    // mode

    // Character set
    byte[] codePage = new byte[0x2000]; // Current character set; contains 16
    // bytes for each character, that are
    // interpreted as a 'bitmap'
    boolean codePageReqsUpdate; // Boolean to check if the character map has
    // been updated
    boolean[] codePageUpdateIndex = new boolean[256]; // Array corresponding to
    // codePage (with
    // 2000h/100h=2^5
    // mapping),
    // determining what character has been changed

    // Text mode attributes
    private int textRows = 25; // Number of text rows on screen
    private int textColumns = 90; // Number of text columns on screen
    private int fontWidth = 8; // Font width in pixels
    private int fontHeight = 16; // Font height in pixels
    private int screenWidth = textColumns * fontWidth; // Width of screen in
    // pixels
    private int screenHeight = textRows * fontHeight; // Height of screen in
    // pixels
    private byte horizPanning = 0; // 'Pixel shift count': number of pixels
    // video data is shifted left
    private byte vertPanning = 0; // 'Preset Row Scan': number of scanlines to
    // scroll display up (0 - Maximum Scan Line)
    private short lineCompare = 0x3FF; // Scan line where a horiz. division can
    // occur; 0x3FF indicates no division

    // Last position of cursor (in rows & columns)
    private int cursorPosPrevX = 0;
    private int cursorPosPrevY = 0;

    private int xTileSize = 16; // Tile sizes used in graphics mode
    private int yTileSize = 24; // TODO: Make these adjustable

    // Logging
    private static final Logger logger = Logger.getLogger(Screen.class.getName());

    // Constants

    // Raster, ColourModel and BufferedImage properties
    protected static final int RED = 0;
    protected static final int GREEN = 1;
    protected static final int BLUE = 2;

    /**
     * Class constructor
     *
     * @param owner
     */
    public Screen(Emulator owner) {

        // Create an initial (temporary) colourmodel, filled with black
        Arrays.fill(palette[RED], (byte) 0);
        Arrays.fill(palette[GREEN], (byte) 0);
        Arrays.fill(palette[BLUE], (byte) 0);
        colourModel = new IndexColorModel(8, 256, palette[RED], palette[GREEN],
                palette[BLUE]);

        // Fill fontImage array with images
        createCodePage437Images();

        // Instantiate graphics tile, fill with black for now
        int[] pixelArray = new int[xTileSize * yTileSize * 8];
        graphicTile = new BufferedImage(xTileSize, yTileSize,
                BufferedImage.TYPE_BYTE_INDEXED, (IndexColorModel) colourModel);
        WritableRaster ras = graphicTile.getRaster();
        for (int j = 0; j < yTileSize; j++) {
            // Zero-based so start at bits-1
            for (int k = (xTileSize - 1); k >= 0; k--) {

                pixelArray[j * xTileSize + Math.abs(k - (8 - 1))] = 0;
            }
        }
        ras.setPixels(0, 0, fontWidth, fontHeight, pixelArray);

        // Create creen canvas
        screenPanel = new ScreenPanel();

        logger.log(Level.INFO, "[" + super.getType() + "] " + getClass().getName()
                + " . AbstractModule created successfully.");
    }

    /**
     * {@inheritDoc}
     *
     * @see dioscuri.module.AbstractModule
     */
    @Override
    public boolean reset() {
        // Set screen size and internal image
        this.setScreenSize(screenWidth, screenHeight);

        logger.log(Level.INFO, "[" + super.getType() + "] AbstractModule has been reset.");
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @see dioscuri.module.AbstractModule
     */
    @Override
    public String getDump() {
        String dump = "";
        String ret = "\r\n";

        dump = "Screen status:" + ret;

        dump += "Rows: " + textRows + "; Columns: " + textColumns + ret;
        dump += "Font width: " + fontWidth + "; font height: " + fontHeight
                + ret;
        dump += "Screen width: " + screenWidth + "; screen height: "
                + screenHeight + ret;

        return dump;
    }

    /**
     * {@inheritDoc}
     *
     * @see dioscuri.module.ModuleScreen
     */
    @Override
    public JPanel getScreen() {
        return screenPanel;
    }

    /**
     * {@inheritDoc}
     *
     * @see dioscuri.module.ModuleScreen
     */
    @Override
    public int getScreenRows() {
        return textRows;
    }

    /**
     * {@inheritDoc}
     *
     * @see dioscuri.module.ModuleScreen
     */
    @Override
    public int getScreenColumns() {
        return textColumns;
    }

    /**
     * {@inheritDoc}
     *
     * @see dioscuri.module.ModuleScreen
     */
    @Override
    public int getScreenWidth() {
        return screenWidth;
    }

    /**
     * {@inheritDoc}
     *
     * @see dioscuri.module.ModuleScreen
     */
    @Override
    public int getScreenHeight() {
        return screenHeight;
    }

    /**
     * {@inheritDoc}
     *
     * @see dioscuri.module.ModuleScreen
     */
    @Override
    public void setScreenSize(int width, int height) {
        // Update image size
        this.setImage(width, height);

        // Update screen size
        screenWidth = width;
        screenHeight = height;
        screenPanel.setSize(width, height);
        screenPanel.setBackground(Color.black);

        logger.log(Level.INFO, "[" + super.getType() + "]"
                + " Size of screen has been set.");
    }

    /**
     * {@inheritDoc}
     *
     * @see dioscuri.module.ModuleScreen
     */
    @Override
    public void updateScreenSize(int newWidth, int newHeight, int newFontWidth,
                                 int newFontHeight) {
        logger.log(Level.INFO, "[" + super.getType() + "]"
                + " call to updateScreenSize() w/ fonts");

        // Check if the font size needs updating
        if (newFontHeight > 0) {
            fontHeight = newFontHeight;
            fontWidth = newFontWidth;
            textColumns = newWidth / fontWidth;
            textRows = newHeight / fontHeight;
        }

        // Check if screen size needs updating
        if ((newWidth != screenWidth) || (newHeight != screenHeight)) {
            setScreenSize(newWidth, newHeight);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see dioscuri.module.ModuleScreen
     */
    @Override
    public void clearScreen() {
        // "I see a red door and I want it painted black; No colours anymore I want them to turn black"
        byte[] pixelArray = new byte[image.getWidth() * image.getHeight()];
        Arrays.fill(pixelArray, (byte) 0);
        // TODO should local DataBuffer be used here? Or was the global variable meant to be used
        /*<DataBuffer>*/
        dataBuffer = new DataBufferByte(pixelArray,
                pixelArray.length);
        SampleModel model = new MultiPixelPackedSampleModel(
                DataBuffer.TYPE_BYTE, image.getWidth(), image.getHeight(), 8);
        WritableRaster ras = Raster.createWritableRaster(model, dataBuffer, null);

        image.setData(ras);
    }

    /**
     * {@inheritDoc}
     *
     * @see dioscuri.module.ModuleScreen
     */
    @Override
    public boolean setPaletteColour(byte index, int red, int green, int blue) {
        // Assure byte is within array bounds
        int indx = index & 0xFF;

        // Note: Only using a private colourmap. Look into using the system
        // colourmap?
        // Note: Cast from int to byte, as values are treated as 8-bit
        // unsigneds. Have seen this
        // cast in examples as well, which seem to work okay.
        palette[RED][indx] = (byte) red;
        palette[GREEN][indx] = (byte) green;
        palette[BLUE][indx] = (byte) blue;
        this.updatePalette();

        logger.log(Level.INFO, "[" + super.getType() + "]" + " Palette[" + indx
                + "] changed to: {" + (byte) red + "," + (byte) green + ","
                + (byte) blue + "}");

        // Note: always returning true because we're using private colourmap.
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @see dioscuri.module.ModuleScreen
     */
    @Override
    public void setByteInCodePage(int index, byte data) {
        // Set data
        codePage[index] = data;

        // Set codePage and codePage index to require update
        codePageUpdateIndex[index >> 5] = true;
        codePageReqsUpdate = true;

        logger.log(Level.INFO, "[" + super.getType() + "]" + " codePage[" + index
                + "] = " + data);
    }

    /**
     * {@inheritDoc}
     *
     * @see dioscuri.module.ModuleScreen
     */
    @Override
    public void updateCodePage(int start) {
        // The assumption here is that data is always copied from video memory,
        // which is hardcoded below
        ModuleVideo video = (ModuleVideo) super.getConnection(Module.Type.VIDEO);
        System.arraycopy(codePage, 0, video.getVideoBuffer(), start, 0x2000);

        // Set codePage and and all codePage indices to require update
        Arrays.fill(codePageUpdateIndex, true);
        codePageReqsUpdate = true;
    }

    /**
     * {@inheritDoc}
     *
     * @see dioscuri.module.ModuleScreen
     */
    @Override
    public void updateText(int oldText, int newText, long cursorXPos,
                           long cursorYPos, short[] textModeAttribs, int numberRows) {

        ModuleVideo video = (ModuleVideo) super.getConnection(Module.Type.VIDEO);

        logger.log(Level.INFO, String.format(
                "call :: updateText(oldText=%d, newText=%d, cursorXPos=%d, cursorYPos=%d, textModeAttribs=%s, numberRows=%d)",
                oldText, newText, cursorXPos, cursorYPos, Arrays.toString(textModeAttribs), numberRows));
        try {
            int oldLine, newLine, textBase;
            int currentChar; // 'Current character to display' index into font table
            int newForeground, newBackground; // Fore-/background colour attributes
            // for cChar
            int oldCursorPos; // Previous cursor position calculated as number of
            // bytes in memory array

            int charsPerRow; // Number of characters per row
            int numRows; // Number of rows in display

            int offset; // Current location (in bytes) in video memory array

            int x, y, xc, yc, yc2, cs_y;
            byte cfwidth, cfheight, cfheight2, font_col, font_row, font_row2;
            byte splitTextRow, splitFontRows;
            byte forceUpdate = 0, splitScreen;

            short newFullStartAddress = textModeAttribs[0];
            byte newCursorStartLine = (byte) textModeAttribs[1];
            byte newCursorEndLine = (byte) textModeAttribs[2];
            short newLineOffset = textModeAttribs[3];
            short newLineCompare = textModeAttribs[4];
            byte newHorizPanning = (byte) textModeAttribs[5];
            byte newVertPanning = (byte) textModeAttribs[6];
            byte newLineGraphics = (byte) textModeAttribs[7];
            byte newSplitHorizPanning = (byte) textModeAttribs[8];

            // Check if all character fonts are up to date
            //logger.log(Level.INFO, "codePageReqsUpdate="+codePageReqsUpdate);
            if (codePageReqsUpdate) {
                logger.log(Level.INFO, "[" + this.getType()
                        + "] Character map update. New font height: " + fontHeight
                        + "; width: " + fontWidth);
                for (int c = 0; c < 256; c++) {
                    if (codePageUpdateIndex[c]) {
                        // Create new bitmap for character
                        int[] data = new int[16];
                        for (int k = 0; k < 16; k++) {
                            data[k] = codePage[(c << 5) + k];
                        }
                        this.updateFontImage(c, data, fontWidth, fontHeight);
                        if (this.fontImages[c] == null) {
                            logger.log(Level.SEVERE, "[" + this.getType()
                                    + "] Can't create vga font " + c
                                    + " while updating codepage.");
                        }
                        codePageUpdateIndex[c] = false;
                    }
                }
                forceUpdate = 1;
                codePageReqsUpdate = false;
            }

            // Check for horizontal/vertical scrolling of image [panning]
            //logger.log(Level.INFO, "((newHorizPanning != horizPanning) || (newVertPanning != vertPanning))="+((newHorizPanning != horizPanning) || (newVertPanning != vertPanning)));
            if ((newHorizPanning != horizPanning) || (newVertPanning != vertPanning)) {
                forceUpdate = 1;
                horizPanning = newHorizPanning;
                vertPanning = newVertPanning;
            }

            //logger.log(Level.INFO, "(newLineCompare != lineCompare)="+(newLineCompare != lineCompare));
            if (newLineCompare != lineCompare) {
                forceUpdate = 1;
                lineCompare = newLineCompare;
            }

            // Invalidate character at old and new cursor location,
            // to ensure it is redrawn (old removed, new displayed)
            //logger.log(Level.INFO, "((cursorPosPrevY < textRows) && (cursorPosPrevX < textColumns))="+((cursorPosPrevY < textRows) && (cursorPosPrevX < textColumns)));
            if ((cursorPosPrevY < textRows) && (cursorPosPrevX < textColumns)) {
                // Previous cursor was drawn on screen, so invalidate its position
                // in the array
                oldCursorPos = cursorPosPrevY * newLineOffset + cursorPosPrevX * 2;
                video.setTextSnapshot(oldText + oldCursorPos, (byte) ~video.getVideoBufferByte(newText + oldCursorPos));
            }

            //logger.log(Level.INFO, "((newCursorStartLine <= newCursorEndLine) && (newCursorStartLine < fontHeight) && (cursorYPos < textRows) && (cursorXPos < textColumns))="+((newCursorStartLine <= newCursorEndLine) && (newCursorStartLine < fontHeight) && (cursorYPos < textRows) && (cursorXPos < textColumns)));
            if ((newCursorStartLine <= newCursorEndLine) && (newCursorStartLine < fontHeight) && (cursorYPos < textRows) && (cursorXPos < textColumns)) {
                // Current cursor is on screen and needs to be displayed, so
                // invalidate its position in the array
                oldCursorPos = (int) (cursorYPos * newLineOffset + cursorXPos * 2);
                video.setTextSnapshot(oldText + oldCursorPos, (byte) ~video.getVideoBufferByte(newText + oldCursorPos));
            } else {
                // No cursor on screen, so provide unreachable value
                oldCursorPos = 0xFFFF;
            }

            numRows = textRows;
            if (vertPanning != 0) {
                // Vertical scrolling required
                numRows++;
            }

            y = 0;
            cs_y = 0;
            textBase = (byte) (newText - newFullStartAddress);
            splitTextRow = (byte) ((lineCompare + vertPanning) / fontHeight);
            splitFontRows = (byte) (((lineCompare + vertPanning) % fontHeight) + 1);
            splitScreen = 0;

            // START row loop
            do {
                charsPerRow = textColumns;
                if (horizPanning != 0)
                    // Horizontal scrolling required
                    charsPerRow++;
                if (splitScreen != 0) {
                    yc = lineCompare + cs_y * fontHeight + 1;
                    font_row = 0;
                    if (numRows == 1) {
                        cfheight = (byte) ((screenHeight - lineCompare - 1) % fontHeight);
                        if (cfheight == 0) cfheight = (byte) fontHeight;
                    } else {
                        cfheight = (byte) fontHeight;
                    }
                }
                // Check for vertical scrolling of image
                else if (vertPanning != 0) {
                    if (y == 0) {
                        yc = 0;
                        font_row = vertPanning;
                        cfheight = (byte) (fontHeight - vertPanning);
                    } else {
                        yc = y * fontHeight - vertPanning;
                        font_row = 0;
                        if (numRows == 1) {
                            cfheight = vertPanning;
                        } else {
                            cfheight = (byte) fontHeight;
                        }
                    }
                } else
                // No horizontal/vertical scrolling required, determine current line
                // location
                {
                    yc = y * fontHeight;
                    font_row = 0;
                    cfheight = (byte) fontHeight;
                }
                if (!(splitScreen != 0) && (y == splitTextRow)) {
                    if (splitFontRows < cfheight)
                        cfheight = splitFontRows;
                }
                newLine = newText;
                oldLine = oldText;
                x = 0;
                offset = cs_y * newLineOffset;

                // START character loop
                do {
                    // Check for horizontal scrolling of text
                    if (horizPanning != 0) {
                        if (charsPerRow > textColumns) {
                            xc = 0;
                            font_col = horizPanning;
                            cfwidth = (byte) (fontWidth - horizPanning);
                        } else {
                            xc = x * fontWidth - horizPanning;
                            font_col = 0;
                            if (charsPerRow == 1) {
                                cfwidth = horizPanning;
                            } else {
                                cfwidth = (byte) fontWidth;
                            }
                        }
                    }
                    // No horizontal panning, determine current character position
                    else {
                        xc = x * fontWidth;
                        font_col = 0;
                        cfwidth = (byte) fontWidth;
                    }

                    // Determine if character needs to be redrawn at current
                    // position -
                    // due to forced update, new character, or character attribute
                    // change
                    if ((forceUpdate != 0)
                            || (video.getTextSnapshot(oldText) != video.getVideoBufferByte(newText))
                            || (video.getTextSnapshot(oldText + 1) != video.getVideoBufferByte(newText + 1))) {

                        // Select character (index into font table) from from video memory
                        currentChar = video.getVideoBufferByte(newText);
                        currentChar &= 0xFF; // Convert to unsigned to avoid array out of bounds

                        // Determine attributes for character - take font colour
                        // from the video's Internal Palette Index
                        newForeground = video.getAttributePaletteRegister(video.getVideoBufferByte(newText + 1) & 0x0f);
                        newBackground = video.getAttributePaletteRegister((video.getVideoBufferByte(newText + 1) & 0xf0) >> 4);

                        // Assign correct CRT colours to character
                        WritableRaster wr = this.fontImages[currentChar].getData().createCompatibleWritableRaster();

                        for (int pixX = wr.getMinX(); pixX < wr.getWidth(); pixX++) {
                            for (int pixY = wr.getMinY(); pixY < wr.getHeight(); pixY++) {

                                switch (this.fontImages[currentChar].getData().getSample(pixX, pixY, 0)) {

                                    case 0:
                                        wr.setSample(pixX, pixY, 0, newBackground);
                                        break;
                                    case 1:
                                        wr.setSample(pixX, pixY, 0, newForeground);
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }

                        // Draw 'bitmap' of character at current line, character location
                        this.image.setData(wr.createTranslatedChild(xc, yc));

                        // Display cursor (usually '_') at current location

                        if (offset == oldCursorPos) {
                            if (font_row == 0) {
                                yc2 = yc + newCursorStartLine;
                                font_row2 = newCursorStartLine;
                                cfheight2 = (byte) (newCursorEndLine - newCursorStartLine + 1);
                                if ((yc2 + cfheight2) > screenHeight) {
                                    cfheight2 = (byte) (screenHeight - yc2);
                                }
                            } else {
                                if (vertPanning > newCursorStartLine) {
                                    yc2 = yc;
                                    font_row2 = font_row;
                                    cfheight2 = (byte) (newCursorEndLine
                                            - vertPanning + 1);
                                } else {
                                    yc2 = yc + newCursorStartLine - vertPanning;
                                    font_row2 = newCursorStartLine;
                                    cfheight2 = (byte) (newCursorEndLine
                                            - newCursorStartLine + 1);
                                }
                            }
                            if (yc2 < screenHeight) {
                                // Draw cursor as 2 line high 'bitmap' underneath
                                // character
                                // This works because most characters have blank
                                // space underneath
                                WritableRaster wr2 = this.fontImages[currentChar]
                                        .getData().createChild(
                                                this.image.getMinX(),
                                                this.image.getMinY(), cfwidth,
                                                cfheight2, xc, yc2, null)
                                        .createCompatibleWritableRaster();
                                for (int pix_x = wr2.getMinX(); pix_x < wr2.getWidth(); pix_x++) {
                                    for (int pix_y = wr2.getMinY(); pix_y < wr2.getHeight(); pix_y++) {
                                        // Set all values to foreground colour
                                        wr2.setSample(pix_x, pix_y, 0, newForeground);
                                    }
                                }
                                this.image.setData(wr2.createTranslatedChild(xc, yc2));
                            }
                        }
                    }
                    // Increment character location, as well as locations in display memory
                    x++;
                    newText += 2;
                    oldText += 2;
                    offset += 2;
                } while (--charsPerRow != 0);
                // END character loop

                if (!(splitScreen != 0) && (y == splitTextRow)) {
                    newText = textBase;
                    forceUpdate = 1;
                    cs_y = 0;
                    if (newSplitHorizPanning != 0) {
                        horizPanning = 0;
                    }
                    numRows = ((screenHeight - lineCompare + fontHeight - 2) / fontHeight) + 1;
                    splitScreen = 1;
                } else {
                    y++;
                    cs_y++;
                    newText = newLine + newLineOffset;
                    oldText = oldLine + newLineOffset;
                }
            } while (--numRows != 0);
            // END row loop

            // Retain values for next update
            horizPanning = newHorizPanning;
            cursorPosPrevX = (int) cursorXPos;
            cursorPosPrevY = (int) cursorYPos;

            // Refresh screen
            screenPanel.repaint();
        } catch (RasterFormatException e) {
            // TODO fix the RasterFormatException properly
            logger.log(Level.INFO, "RasterFormatException details: " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see dioscuri.module.ModuleScreen
     */
    @Override
    public void updateGraphicsTile(byte[] tile, int x0, int y0) {

        int x, y, y_size;

        if ((y0 + yTileSize) > screenHeight) {
            y_size = screenHeight - y0;
        } else {
            y_size = yTileSize;
        }

        WritableRaster ras = graphicTile.getRaster();

        // Only support 8 bits/pixel at the moment
        for (y = 0; y < y_size; y++) {
            for (x = 0; x < xTileSize; x++) {
                // Set each colour (as found in the tile) in the graphicsTile
                ras.setSample(x, y, 0, tile[y * xTileSize + x]);
            }
        }
        // Draw tile at current location
        this.image.setData(ras.createTranslatedChild(x0, y0));
        screenPanel.repaint();
    }

    /**
     * Set an image with data on screen
     *
     * @param width  Width of the image in pixels
     * @param height Height of the image in pixels
     */
    private void setImage(int width, int height) {
        // Create a data buffer using the byte buffer of pixel data.
        pixels = new byte[width * height];
        Arrays.fill(pixels, (byte) 0);

        dataBuffer = new DataBufferByte(pixels, pixels.length);

        // Prepare a sample model that specifies 8-bits/pixel
        // int bitMasks[] = new int[]{(byte)0x0f};
        sampleModel = new MultiPixelPackedSampleModel(DataBuffer.TYPE_BYTE,
                width, height, 8);

        // Create a raster using the sample model and data buffer, located at
        // (0,0) (null)
        raster = Raster.createWritableRaster(sampleModel, dataBuffer, null);

        // Combine the colour model and raster into a buffered image (not
        // alpha-premultiplied; no properties)
        image = new BufferedImage(colourModel, raster, false, null);

        // Display image (will be automatically refreshed)
        screenPanel.setImage(image);
    }

    /**
     * Refresh the on-screen image
     */
    private void updateImage() {
        image = new BufferedImage(colourModel, raster, false, null);
        screenPanel.setImage(image);
    }

    /**
     * Update the colourModel used in the image. Do this after the palette
     * values have been changed
     */
    private void updatePalette() {
        colourModel = new IndexColorModel(8, 256, palette[RED], palette[GREEN],
                palette[BLUE]);
        // This requires the image to be redrawn on the canvas to reflect
        // changes in the colourmodel:
        this.updateImage();
    }

    /**
     * Create character images from code page 437 hex values
     */
    private void createCodePage437Images() {
        // Default code page 437 values
        int fntWidth = 8;
        int fntHeight = 16;

        // Create image using data from standard font table
        for (int i = 0; i < CodePage437.codePageArray.length; i++) {
            int[] pxels = new int[fntWidth * fntHeight];
            fontImages[i] = new BufferedImage(fntWidth, fntHeight,
                    BufferedImage.TYPE_BYTE_BINARY);
            WritableRaster rastr = fontImages[i].getRaster();

            // Translate each hex value (16 values) to binary (8 bits/hex value)
            for (int j = 0; j < fntHeight; j++) {
                for (int k = 0; k < fntWidth; k++) {
                    pxels[j * fntWidth + k] = ((CodePage437.codePageArray[i][j]) >>> k) & 0x01;
                }
            }
            rastr.setPixels(0, 0, fntWidth, fntHeight, pxels);

            if (fontImages[i] == null) {
                logger.log(Level.SEVERE, "[" + super.getType() + "]"
                        + " Can't create  font [" + i + "]");
            }
        }
    }

    /**
     * Replace a BufferedImage in the font table with new data
     *
     * @param index      Index of the new character into the font table
     * @param fontData   Array of hex values representing the character
     * @param fontWidth  Width of the character in pixels
     * @param fontHeight Height of the character in pixels
     */
    private void updateFontImage(int index, int[] fontData, int fontWidth,
                                 int fontHeight) {
        int bits = 8;
        int[] pixelArray = new int[fontWidth * fontHeight * bits];

        // Use an IndexColorModel here to allow different colour text
        fontImages[index] = new BufferedImage(fontWidth, fontHeight,
                BufferedImage.TYPE_BYTE_INDEXED, (IndexColorModel) colourModel);
        WritableRaster rster = fontImages[index].getRaster();

        // Translate each hex value (16 values) to binary (8 bits/hex value), in
        // reverse order
        for (int j = 0; j < fontHeight; j++) {
            // Zero-based so start at bits-1
            for (int k = (bits - 1); k >= 0; k--) {

                pixelArray[j * fontWidth + Math.abs(k - (bits - 1))] = ((fontData[j]) >>> k) & 0x01;
            }
        }
        rster.setPixels(0, 0, fontWidth, fontHeight, pixelArray);
    }

}
