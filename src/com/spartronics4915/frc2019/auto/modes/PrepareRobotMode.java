package com.spartronics4915.frc2019.auto.modes;

import com.spartronics4915.frc2019.auto.AutoModeBase;
import com.spartronics4915.frc2019.auto.AutoModeEndedException;
import com.spartronics4915.frc2019.auto.actions.ActuateHarvesterAction;
import com.spartronics4915.frc2019.auto.actions.PrintDebugAction;
import com.spartronics4915.frc2019.subsystems.Harvester;

public class PrepareRobotMode extends AutoModeBase
{

    @Override
    protected void routine() throws AutoModeEndedException
    {
        runAction(new ActuateHarvesterAction(Harvester.WantedState.STOW));
        runAction(new PrintDebugAction("Doing nothing... You can let the compressor run."));
    }

}
