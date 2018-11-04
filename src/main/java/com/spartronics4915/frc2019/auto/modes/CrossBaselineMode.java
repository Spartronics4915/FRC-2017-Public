package com.spartronics4915.frc2019.auto.modes;

import com.spartronics4915.frc2019.auto.AutoModeBase;
import com.spartronics4915.frc2019.auto.AutoModeEndedException;
import com.spartronics4915.frc2019.auto.actions.DrivePathAction;
import com.spartronics4915.frc2019.auto.actions.ResetPoseFromPathAction;
import com.spartronics4915.frc2019.paths.CrossBaselinePath;
import com.spartronics4915.frc2019.paths.PathContainer;

public class CrossBaselineMode extends AutoModeBase
{

    @Override
    protected void routine() throws AutoModeEndedException
    {
        PathContainer path = new CrossBaselinePath();
        runAction(new ResetPoseFromPathAction(path));
        runAction(new DrivePathAction(path));
    }
    
}
