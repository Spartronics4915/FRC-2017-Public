package com.spartronics4915.frc2018.subsystems;

import com.spartronics4915.frc2018.Constants;
import com.spartronics4915.frc2018.loops.Loop;
import com.spartronics4915.frc2018.loops.Looper;
import com.spartronics4915.lib.util.Logger;
import com.spartronics4915.lib.util.Util;
import com.spartronics4915.lib.util.drivers.LazySolenoid;
import com.spartronics4915.lib.util.drivers.SpartIRSensor;
import com.spartronics4915.lib.util.drivers.TalonSRX4915;
import com.spartronics4915.lib.util.drivers.TalonSRX4915Factory;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The harvester is a set of two collapsible rollers that pull in and hold
 * a cube. This cube is then in a position where it can be picked up by the
 * articulated grabber.
 */
public class Harvester extends Subsystem
{

    private static Harvester sInstance = null;
    private static final boolean kSolenoidOpen = true;
    private static final boolean kSolenoidClose = false;
    private static final double kCubeMinDistanceInches = 0;
    private static final double kCubeMaxDistanceInches = 0;

    public static Harvester getInstance()
    {
        if (sInstance == null)
        {
            sInstance = new Harvester();
        }
        return sInstance;
    }

    public enum SystemState
    {
        CLOSING,
        OPENING,
        HARVESTING,
        EJECTING,
        HUGGING,
        PREHARVESTING,
        DISABLING,
    }

    public enum WantedState
    {
        OPEN,
        HARVEST,
        EJECT,
        PREHARVEST,
        DISABLE,
    }

    private SystemState mSystemState = SystemState.DISABLING;
    private WantedState mWantedState = WantedState.DISABLE;
    private SpartIRSensor mCubeHeldSensor = null;
    private LazySolenoid mSolenoid = null;
    private TalonSRX4915 mMotorRight = null;
    private TalonSRX4915 mMotorLeft = null;

    // Actuators and sensors should be initialized as private members with a value of null here

    private Harvester()
    {
        boolean success = true;

        // Instantiate your actuator and sensor objects here
        // If !mMyMotor.isValid() then success should be set to false

        try
        {
            mSolenoid = new LazySolenoid(Constants.kHarvesterSolenoidId); // Changes value of Solenoid
            mCubeHeldSensor = new SpartIRSensor(Constants.kGrabberCubeDistanceRangeFinderId);
            mMotorRight = TalonSRX4915Factory.createDefaultMotor(Constants.kHarvesterRightMotorId); // change value of motor
            mMotorLeft = TalonSRX4915Factory.createDefaultMotor(Constants.kHarvesterLeftMotorId); // change value of motor
            mMotorRight.configOutputPower(true, 0.5, 0, 0.5, 0, -0.5);
            mMotorLeft.configOutputPower(true, 0.5, 0, 0.5, 0, -0.5);
            mMotorRight.setInverted(true);
            
            if (!mMotorRight.isValid())
            {
                logError("Right Motor is invalid");
                success = false;
            }
            if (!mMotorLeft.isValid())
            {
                logError("Left Motor is invalid");
                success = false;
            }
            if (!mSolenoid.isValid())
            {
                logError("Solenoid is invalid");
                success = false;
            }
        }
        catch (Exception e)
        {
            logError("Couldn't instantiate hardware objects");
            Logger.logThrowableCrash(e);
            success = false;
        }

        logInitialized(success);
    }

    private Loop mLoop = new Loop()
    {

        @Override
        public void onStart(double timestamp)
        {
            synchronized (Harvester.this)
            {

                if (mSystemState == SystemState.DISABLING)
                    mSystemState = SystemState.CLOSING;
            }
        }

        @Override
        public void onLoop(double timestamp)
        {
            synchronized (Harvester.this)
            {
                SystemState newState; // calls the wanted handle case for the given systemState
                switch (mSystemState)
                {
                    case CLOSING:
                        newState = handleClosing();
                        break;
                    case OPENING:
                        newState = handleOpening();
                        break;
                    case PREHARVESTING:
                        newState = handlePreharvesting();
                        break;
                    case HARVESTING:
                        newState = handleHarvesting();
                        break;
                    case EJECTING:
                        newState = handleEjecting();
                        break;
                    case HUGGING:
                        newState = handleHugging();
                        break;
                    case DISABLING:
                        newState = handleClosing();
                        break;
                    default:
                        newState = handleClosing();
                }
                if (newState != mSystemState)
                {
                    logInfo("Harvester state from " + mSystemState + "to" + newState);
                    dashboardPutState(mSystemState.toString());
                    mSystemState = newState;
                }
            }
        }

        @Override
        public void onStop(double timestamp)
        {
            synchronized (Harvester.this)
            {
                stop();
            }
        }

    };

    private SystemState defaultStateTransfer() // transitions the systemState given what the wantedState is
    {

        switch (mWantedState)
        {
            case OPEN:
                return SystemState.OPENING;
            case PREHARVEST:
                return SystemState.PREHARVESTING;
            case HARVEST:
                return SystemState.HARVESTING;
            case EJECT:
                return SystemState.EJECTING;
            default:
                return mSystemState;

        }
    }

    private SystemState handleClosing()
    {
        //motors off and bars in
        mSolenoid.set(kSolenoidClose);
        mMotorLeft.set(0.0);
        mMotorRight.set(0.0);
        if (mWantedState == WantedState.OPEN || mWantedState == WantedState.PREHARVEST)
        {
            return defaultStateTransfer();
        }
        return SystemState.CLOSING; // all defaultStateTransfers return the wanted state
    }

    private SystemState handleOpening()
    {
        //motors off and bars out
        mSolenoid.set(kSolenoidOpen);
        mMotorLeft.set(0.0);
        mMotorRight.set(0.0);
        if (mWantedState == WantedState.HARVEST || mWantedState == WantedState.PREHARVEST)
        {
            return defaultStateTransfer();
        }
        return SystemState.OPENING;
    }

    private SystemState handlePreharvesting()
    {
        //motors on and bars out
        mSolenoid.set(kSolenoidOpen);
        mMotorLeft.set(1.0);
        mMotorRight.set(1.0);
        if (mWantedState == WantedState.HARVEST)
        {
            return defaultStateTransfer();
        }
        return SystemState.PREHARVESTING;
    }

    private SystemState handleHarvesting()
    {
        //motors on forward and bars closing, hug when cube is gone
        mSolenoid.set(kSolenoidClose);
        mMotorLeft.set(1.0);
        mMotorRight.set(1.0);
        if (isCubeHeld())
        {
            return SystemState.HUGGING;  // checks if cube is in the robot and will transitions to hugging when the cube is fully in
        }
        else
        {
            return defaultStateTransfer();
        }
    }

    private SystemState handleEjecting()
    {
        //motors in reverse and bars closing, close when cube is gone
        mSolenoid.set(kSolenoidClose);
        mMotorRight.set(-1.0);
        mMotorLeft.set(-1.0);
        if (!isCubeHeld()) 
        { //Cube is gone!  Transition to Open (turn off motor) to prevent damage
            setWantedState(WantedState.OPEN);
            return SystemState.OPENING;
        }
        else
        {
            return defaultStateTransfer();
        }
    }

    private SystemState handleHugging()
    {
        //motors off and bars closing go to closed when cube is gone
        mSolenoid.set(kSolenoidClose);
        mMotorLeft.set(0.0);
        mMotorRight.set(0.0);
        if (mWantedState == WantedState.HARVEST || mWantedState == WantedState.OPEN || mWantedState == WantedState.EJECT)
        {
            return defaultStateTransfer();
        }
        return SystemState.HUGGING;
    }

    public void setWantedState(WantedState wantedState)
    {
        mWantedState = wantedState;
        dashboardPutWantedState(mWantedState.toString());
    }
    
    private boolean isCubeHeld()
    {
        return mCubeHeldSensor.isTargetInDistanceRange(kCubeMinDistanceInches, kCubeMaxDistanceInches);
    }

    @Override
    public void outputToSmartDashboard()
    {
        dashboardPutState(mSystemState.toString());
        dashboardPutWantedState(mWantedState.toString());
        dashboardPutBoolean("mSolenoid", mSolenoid.get());
        dashboardPutBoolean("IRSensor CubeHeld", isCubeHeld());
        dashboardPutNumber("MotorRight", mMotorRight.get());
        dashboardPutNumber("MotorLeft", mMotorLeft.get());
        dashboardPutNumber("Cube Distance: ", mCubeHeldSensor.getDistance());
    }

    @Override
    public synchronized void stop()
    {
        mSolenoid.set(kSolenoidClose);
        mMotorLeft.set(0.0);
        mMotorRight.set(0.0);
        mSystemState = SystemState.DISABLING;
    }

    @Override
    public void zeroSensors()
    {
    }

    @Override
    public void registerEnabledLoops(Looper enabledLooper)
    {
        enabledLooper.register(mLoop);
    }

    @Override
    public boolean checkSystem(String variant)
    {
        boolean success = true;
        if (!isInitialized())
        {
            logWarning("can't check un-initialized system");
            return false;
        }
        logNotice("checkSystem (" + variant + ") ------------------");

        try
        {
            boolean allTests = variant.equalsIgnoreCase("all") || variant.equals("");
            if (variant.equals("basic") || allTests)
            {
                logNotice("basic check ------");
                logNotice("  mMotorRight:\n" + mMotorRight.dumpState());
                logNotice("  mMotorLeft:\n" + mMotorLeft.dumpState());
                logNotice("  mSolenoid: " + mSolenoid.get());
                logNotice("  isCubeHeld: " + isCubeHeld());
            }
            if (variant.equals("solenoid") || allTests)
            {
                logNotice("solenoid check ------");
                logNotice("on 4s");
                mSolenoid.set(kSolenoidOpen);
                Timer.delay(4.0);
                logNotice("  isCubeHeld: " + isCubeHeld());
                logNotice("off");
                mSolenoid.set(kSolenoidClose);
            }
            if (variant.equals("motors") || allTests)
            {
                logNotice("motors check ------");
                logNotice("open arms (2s)");
                mSolenoid.set(kSolenoidOpen);
                Timer.delay(2.0);

                logNotice("left motor fwd .5 (4s)"); // in
                mMotorLeft.set(.5);
                Timer.delay(4.0);
                logNotice("  current: " + mMotorLeft.getOutputCurrent());
                mMotorLeft.set(0);

                logNotice("right motor fwd .5 (4s)");
                mMotorRight.set(.5);
                Timer.delay(4.0);
                logNotice("  current: " + mMotorRight.getOutputCurrent());
                mMotorRight.set(0);

                logNotice("both motors rev .5 (4s)"); // out
                mMotorLeft.set(-.5);
                mMotorRight.set(-.5);
                Timer.delay(4.0);
                logNotice("  left current: " + mMotorLeft.getOutputCurrent());
                logNotice("  right current: " + mMotorRight.getOutputCurrent());
                mMotorLeft.set(0);
                mMotorRight.set(0);

                Timer.delay(.5); // let motors spin down
                mSolenoid.set(kSolenoidClose);
            }
            if (variant.equals("IRSensor") || allTests)
            {
                logNotice("SensorCheck");
                logNotice("Is Cube Held?");
                logNotice("Cube Distance: " + mCubeHeldSensor.getDistance());
            }
        }
        catch (Throwable e)
        {
            success = false;
            logException("checkSystem", e);
        }

        logNotice("--- finished ---------------------------");
        return success;
    }
}
