package com.spartronics4915.frc2019.auto.actions;

import com.spartronics4915.frc2019.Constants;
import com.spartronics4915.frc2019.RobotState;
import com.spartronics4915.lib.math.Pose2d;
import com.spartronics4915.lib.math.Translation2d;

import edu.wpi.first.wpilibj.Timer;

/**
 * Convert field coordinates as if you've started from a different origin.
 * For example, if my auto mode always starts at blue A, but some of them
 * work where you can place the robot at red A, this will correct your
 * coordinates (after you've run the path) so they actually represent
 * where the robot is on the field.
 * 
 * @author declan
 */
public class CorrectPoseForOriginAction extends RunOnceAction
{

    public enum FieldLandmarks {
        A, B, C,
    }
    
    public enum FieldSides {
        BLUE, RED,
    }
    
    public class FieldPosition {
        public FieldLandmarks landmark;
        public FieldSides side;
    }
    
    private FieldPosition mFrom;
    private FieldPosition mTo;
    
    public CorrectPoseForOriginAction(FieldPosition from, FieldPosition to) {
        mFrom = from;
        mTo = to;
    }

    @Override
    public void runOnce()
    {
        Pose2d robotTransform = RobotState.getInstance().getFieldToVehicle(Timer.getFPGATimestamp());
        if (mFrom.side != mTo.side) {
            robotTransform = new Pose2d(new Translation2d(Constants.kFieldWidth - robotTransform.getTranslation().x(),
                robotTransform.getTranslation().y()), robotTransform.getRotation());
//            if (mFrom.side == FieldSides.BLUE) TODO
//                robotTransform.getRotation().rotateBy(Rotation2d.fromRadians(-Math.PI/2));
//            else
//                robotTransform.getRotation().rotateBy(Rotation2d.fromRadians(Math.PI/2));
        }
        if ((mFrom.landmark == FieldLandmarks.A && mTo.landmark == FieldLandmarks.C) || (mFrom.landmark == FieldLandmarks.C && mTo.landmark == FieldLandmarks.C)) {
            robotTransform = new Pose2d(new Translation2d(robotTransform.getTranslation().x(),
                Constants.kFieldHeight - robotTransform.getTranslation().y()), robotTransform.getRotation());
//            if (mFrom.landmark == FieldLandmarks.C)
//                robotTransform.getRotation().rotateBy(Rotation2d.fromRadians(-Math.PI/2));
//            else
//                robotTransform.getRotation().rotateBy(Rotation2d.fromRadians(Math.PI/2));
        }
    }

}
