package com.spartronics4915.frc2019;

import com.spartronics4915.frc2019.ControlBoardInterface;

import edu.wpi.first.wpilibj.Joystick;

/**
 * Contains the button mappings for the backup drive stick 
 * and the mechanism controller.
 * A singleton.
 * Implements the ControlBoardInterface.
 * 
 * @see ControlBoardInterface.java
 */

public class BackupControlBoard implements ControlBoardInterface
{
    private static ControlBoardInterface mInstance = null;
    
    private static final boolean kUseBackupDrivestick = false;

    public static ControlBoardInterface getInstance()
    {
        if (mInstance == null)
        {
            if(kUseBackupDrivestick)
            {
                mInstance = new BackupControlBoard();
            }
            else
            {
                mInstance = new ControlBoard();
            }
        }
        return mInstance;
    }

    private final Joystick mDrivestick;
    private final Joystick mButtonBoard;

    protected BackupControlBoard()
    {
        mDrivestick = new Joystick(0);
        mButtonBoard = new Joystick(1);
    }

    @Override
    public double getThrottle() {
        return mDrivestick.getY();
    }

    @Override
    public double getTurn() {
        return mDrivestick.getX();
    }

    @Override
    public boolean getQuickTurn() {
        return mDrivestick.getRawButton(1);
    }

    @Override
    public boolean getSlowDrive() {
        return mDrivestick.getRawButton(14);
    }

    @Override
    public boolean getSwitchTurretMode() {
        return mDrivestick.getRawButtonReleased(1);
    }
}
