package com.spartronics4915.frc2019;

import edu.wpi.first.wpilibj.Joystick;

/**
 * Contains the button mappings for the competition control board. Like the
 * drive code, one instance of the ControlBoard
 * object is created upon startup, then other methods request the singleton
 * ControlBoard instance. Implements the
 * ControlBoardInterface.
 * 
 * @see ControlBoardInterface.java
 */
public class ControlBoard implements ControlBoardInterface
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

    protected ControlBoard()
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
}
