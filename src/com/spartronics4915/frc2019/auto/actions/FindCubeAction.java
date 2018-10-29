package com.spartronics4915.frc2019.auto.actions;

import com.spartronics4915.frc2019.subsystems.Drive;


public class FindCubeAction implements Action
{
    Drive mDrive;
    
    @Override
    public boolean isFinished()
    {
        return false;
    }

    @Override
    public void update()
    {
    }

    @Override
    public void done()
    {
        mDrive.stop();
    }

    @Override
    public void start()
    {
        mDrive = Drive.getInstance();
        mDrive.setWantSearchForCube();
    }

}
