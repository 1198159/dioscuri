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

/*
 * Information used in this module was taken from:
 * - http://en.wikipedia.org/wiki/AT_Attachment
 * - http://bochs.sourceforge.net/techspec/IDE-reference.txt
 */

package dioscuri.module.ata;

import dioscuri.interfaces.Module;
import dioscuri.module.ModuleMotherboard;
import dioscuri.module.ModulePIC;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class representing a single ATA channel
 */
public class ATAChannel {

    private static final Logger logger = Logger.getLogger(ATAChannel.class.getName());
    public static final int IDE_MASTER_INDEX = 0;
    public static final int IDE_SLAVE_INDEX = 1;

    private ATADrive[] drives = new ATADrive[ATAConstants.MAX_NUMBER_DRIVES_PER_CHANNEL];

    private int ioAddress1;
    private int ioAddress2;

    private int irqNumber; // Interrupt number assigned by PIC

    private int defaultIoAddress1;
    private int defaultIoAddress2;

    private ATA parent;

    private int selectedDriveIndex;

    /**
     * Class constructor.
     *
     * @param parent         the parent IDE object
     * @param defaultIoAddr1
     * @param defaultIoAddr2
     */
    public ATAChannel(ATA parent, int defaultIoAddr1, int defaultIoAddr2) {

        this.parent = parent;

        // Initialise drives initially to be disabled

        for (int i = 0; i < this.drives.length; i++) {
            this.drives[i] = new ATADrive(ATADriveType.NONE, parent, false);
        }

        this.ioAddress1 = defaultIoAddr1;
        this.ioAddress2 = defaultIoAddr2;

        this.defaultIoAddress1 = defaultIoAddr1;
        this.defaultIoAddress2 = defaultIoAddr2;

        this.irqNumber = -1;

        this.selectedDriveIndex = 0;

    }

    /**
     * Get cur selected IDE Driver Controller
     *
     * @return IDE Driver Controller
     */
    public ATADriveController getSelectedController() {
        ATADriveController curDriveController = this.getSelectedDrive()
                .getControl();

        return curDriveController;
    }

    /**
     * Reset the channel.
     *
     * @return true if reset successful
     */
    public boolean reset() {

        ModulePIC pic = (ModulePIC) parent.getConnection(Module.Type.PIC);
        ModuleMotherboard motherboard = (ModuleMotherboard) parent.getConnection(Module.Type.MOTHERBOARD);

        this.ioAddress1 = this.defaultIoAddress1;
        this.ioAddress2 = this.defaultIoAddress2;

        // Request IRQ number
        this.irqNumber = pic.requestIRQNumber(this.parent);

        if (this.irqNumber > -1) {
            logger.log(Level.CONFIG, this.parent.getType()
                    + " -> IRQ number set to: " + irqNumber);
            // Make sure no interrupt is pending
            pic.clearIRQ(irqNumber);
        } else {
            logger.log(Level.WARNING, this.parent.getType()
                    + " -> Request of IRQ number failed.");
        }

        if (this.ioAddress1 != 0) {

            //parent.getMotherboard().setIOPort(this.ioAddress1, this.parent);

            motherboard.setIOPort(this.ioAddress1, this.parent);

            for (int address = 0x1; address <= 0x7; address++) {

                motherboard.setIOPort(this.ioAddress1 + address, this.parent);
            }
        }

        // We don't want to register addresses 0x3f6 and 0x3f7 as they are
        // handled by the floppy controller

        if ((this.ioAddress2 != 0) && (this.ioAddress2 != 0x3f0)) {
            for (int address = 0x6; address <= 0x7; address++) {

                motherboard.setIOPort(this.ioAddress2 + address, this.parent);
            }
        }

        selectedDriveIndex = IDE_MASTER_INDEX;

        // Reset drives
        for (int i = 0; i < this.drives.length; i++) {
            this.drives[i].reset();
        }

        return true;
    }

    /**
     * @return -
     */
    public boolean isSelectedDrivePresent() {
        if (drives[selectedDriveIndex].getDriveType() == ATADriveType.NONE) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Set a disk including disk image.
     *
     * @param drive the drive object to set
     */
    public void setDisk(ATADrive drive) {
        int driveIndex = this.getDriveIndex(drive);
        this.drives[driveIndex] = drive;
    }

    private int getDriveIndex(ATADrive drive) {
        int driveIndex;
        if (drive.isMaster) {
            driveIndex = 0;
        } else {
            driveIndex = 1;
        }

        return driveIndex;
    }

    /**
     * Get the drives.
     *
     * @return the drives
     */
    public ATADrive[] getDrives() {
        return this.drives;
    }

    /**
     * Get the IO address 1.
     *
     * @return the IO address 1
     */
    public int getIoAddress1() {
        return this.ioAddress1;
    }

    /**
     * Get the IO address 2
     *
     * @return the IO address 2
     */
    public int getIoAddress2() {
        return ioAddress2;
    }

    /**
     * @return -
     */
    public int getIrqNumber() {
        return this.irqNumber;
    }

    /**
     * @return -
     */
    public ATADrive getSelectedDrive() {
        return this.drives[selectedDriveIndex];
    }

    /**
     * @return -
     */
    public int getSelectedDriveIndex() {
        return this.selectedDriveIndex;
    }

    /**
     * @param theSelectedDriveIndex
     */
    public void setSelectedDriveIndex(int theSelectedDriveIndex) {
        this.selectedDriveIndex = theSelectedDriveIndex;
    }

    /**
     * @return -
     */
    public boolean isMasterDrivePresent() {
        boolean isMasterPresent = false;
        if (getDrives()[IDE_MASTER_INDEX] != null
                && getDrives()[IDE_MASTER_INDEX].getDriveType() != ATADriveType.NONE) {
            isMasterPresent = true;
        }

        return isMasterPresent;
    }

    /**
     * @return -
     */
    public boolean isSlaveDrivePresent() {
        boolean isSlavePresent = false;
        if (getDrives()[IDE_SLAVE_INDEX] != null
                && getDrives()[IDE_SLAVE_INDEX].getDriveType() != ATADriveType.NONE) {
            isSlavePresent = true;
        }

        return isSlavePresent;
    }

    /**
     * @return -
     */
    public boolean isAnyDrivePresent() {

        boolean isAnyDrivePresent = false;

        if (isMasterDrivePresent() || isSlaveDrivePresent()) {
            isAnyDrivePresent = true;
        }

        return isAnyDrivePresent;
    }

    /**
     * @return -
     */
    public boolean isSlaveSelected() {
        return selectedDriveIndex == IDE_SLAVE_INDEX;
    }

    /**
     * @return -
     */
    public boolean isMasterSelected() {
        return selectedDriveIndex == IDE_MASTER_INDEX;
    }
}
