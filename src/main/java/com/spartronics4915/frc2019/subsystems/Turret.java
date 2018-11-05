package com.spartronics4915.frc2019.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.spartronics4915.frc2019.Constants;
import com.spartronics4915.frc2019.RobotState;
import com.spartronics4915.frc2019.lidar.LidarProcessor;
import com.spartronics4915.frc2019.loops.Loop;
import com.spartronics4915.frc2019.loops.Looper;
import com.spartronics4915.lib.drivers.TalonSRX4915;
import com.spartronics4915.lib.math.Pose2d;
import com.spartronics4915.lib.math.Rotation2d;
import com.spartronics4915.lib.math.Translation2d;

import edu.wpi.first.wpilibj.Timer;

public class Turret extends Subsystem {

    private static Turret mInstance = null;

    public static Turret getInstance()
    {
        if (mInstance == null)
        {
            mInstance = new Turret();
        }
        return mInstance;
    }

    public enum WantedState {
        DISABLED,
        FOLLOW_ODOMETRY,
        FOLLOW_LIDAR,
    }

    private enum SystemState {
        DISABLING,
        FOLLOWING,
    }

    private static final boolean kOutputInverted = false;

    private TalonSRX4915 mMotor;
    private LidarProcessor mLidar;
    private RobotState mOdometry;
    private WantedState mWantedState = WantedState.DISABLED;
    private SystemState mSystemState = SystemState.DISABLING;
    private boolean mUseLidar = false;

    private Turret() {
        mLidar = LidarProcessor.getInstance();
        mOdometry = RobotState.getInstance();

        mMotor = new TalonSRX4915(Constants.kTurretMotorId);
        mMotor.configMotorAndSensor(kOutputInverted /* Is motor output inverted */, FeedbackDevice.QuadEncoder,
            kOutputInverted /* Is sensor inverted */, Constants.kTurretEncoderCodesPerRev);
        mMotor.setBrakeMode(true);
    }

    private Loop mLoop = new Loop()
    {
        @Override
        public void onStart(double timestamp)
        {
            synchronized (Turret.this)
            {
                mWantedState = WantedState.DISABLED;
                mSystemState = SystemState.DISABLING;
            }
        }

        @Override
        public void onLoop(double timestamp)
        {
            synchronized (Turret.this)
            {
                SystemState newState = mSystemState;
                switch (mSystemState)
                {
                    case FOLLOWING:
                        Pose2d pose;
                        if (mUseLidar) {
                            pose = mLidar.doICP();
                        } else {
                            pose = mOdometry.getFieldToVehicle(Timer.getFPGATimestamp());
                        }

                        mMotor.set(ControlMode.Position, calculateTurretRevolutions(pose, Constants.kTurretTargetFieldPosition));
                        break;
                    case DISABLING:
                        stop();
                    default:
                        newState = defaultStateTransfer();
                }
                mSystemState = newState;
            }
        }

        @Override
        public void onStop(double timestamp)
        {
            synchronized (Turret.this) {
                stop();
            }
        }
    };

    private double calculateTurretRevolutions(Pose2d robotPose, Translation2d targetTranslation) {
        double angle = Math.asin(Math.abs(robotPose.getTranslation().x() - targetTranslation.x())/robotPose.getTranslation().distance(targetTranslation));
        return Math.toDegrees(angle) / 360;
    }

    private SystemState defaultStateTransfer()
    {
        SystemState newState = mSystemState;
        switch (mWantedState)
        {
            case DISABLED:
                newState = SystemState.DISABLING;
                break;
            case FOLLOW_LIDAR:
                newState = SystemState.FOLLOWING;
                mUseLidar = true;
                break;
            case FOLLOW_ODOMETRY:
                newState = SystemState.FOLLOWING;
                mUseLidar = false;
                break;
            default:
                newState = SystemState.DISABLING;
                break;
        }
        return newState;
    }

    public synchronized void setWantedState(WantedState wantedState)
    {
        logNotice("Wanted state to " + wantedState.toString());
        mWantedState = wantedState;
    }

    public synchronized WantedState getWantedState() {
        return mWantedState;
    }

    @Override
    public void registerEnabledLoops(Looper enabledLooper) {
        enabledLooper.register(mLoop);
    }

    @Override
    public void stop() {
        mMotor.stopMotor();
    }

    @Override
    public void zeroSensors() {
        // Encoder only gets reset on startup
    }

    @Override
    public boolean checkSystem(String variant) {
        logNotice("Check system not implemented");
        return false;
    }

    @Override
    public void outputToSmartDashboard() {
        dashboardPutBoolean("turretUsingLIDAR", mUseLidar);
    }

}