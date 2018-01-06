package com.spartronics4915.lib.util.drivers;

import edu.wpi.first.wpilibj.MotorSafety;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;

/**
 * Creates WPI_TalonSRX objects and configures all the parameters we care about to factory defaults. Closed-loop and sensor
 * parameters are not set, as these are expected to be set by the application.
 */
public class TalonSRXFactory {

    public static class Configuration {
        public LimitSwitchNormal NORMAL_SWITCH_STATE = LimitSwitchNormal.Disabled;
        public double MAX_OUTPUT_VOLTAGE = 12;
        public double NOMINAL_PERCENT = 0;
        public double PEAK_PERCENT = 12;
        public boolean ENABLE_BRAKE = false;
        public boolean ENABLE_CURRENT_LIMIT = false;
        public boolean ENABLE_SOFT_LIMIT = false;
        public int CURRENT_LIMIT = 0;
        public double EXPIRATION_TIMEOUT_SECONDS = MotorSafety.DEFAULT_SAFETY_EXPIRATION;
        public int FORWARD_SOFT_LIMIT = 0;
        public boolean INVERTED = false;
        public double NOMINAL_CLOSED_LOOP_VOLTAGE = 12;
        public double REVERSE_SOFT_LIMIT = 0;
        public boolean SAFETY_ENABLED = false;
        public int PID_IDX = 0; // 0 for primary closed loop. 1 is cascaded, which we don't want (integrates with the pigeon)
        public int TIMEOUT_MS = 20;

        public int CONTROL_FRAME_PERIOD_MS = 5;
        public int MOTION_CONTROL_FRAME_PERIOD_MS = 100;
        public int GENERAL_STATUS_FRAME_RATE_MS = 5;
        public int FEEDBACK_STATUS_FRAME_RATE_MS = 100;
        public int QUAD_ENCODER_STATUS_FRAME_RATE_MS = 100;
        public int ANALOG_TEMP_VBAT_STATUS_FRAME_RATE_MS = 100;
        public int PULSE_WIDTH_STATUS_FRAME_RATE_MS = 100;

        public VelocityMeasPeriod VELOCITY_MEASUREMENT_PERIOD = VelocityMeasPeriod.Period_100Ms;
        public int VELOCITY_MEASUREMENT_ROLLING_AVERAGE_WINDOW = 64;

        public double VOLTAGE_COMPENSATION_RAMP_RATE = 0;
        public double VOLTAGE_RAMP_RATE = 0;
    }

    private static final Configuration kDefaultConfiguration = new Configuration();
    private static final Configuration kSlaveConfiguration = new Configuration();

    static {
        kSlaveConfiguration.CONTROL_FRAME_PERIOD_MS = 1000;
        kSlaveConfiguration.MOTION_CONTROL_FRAME_PERIOD_MS = 1000;
        kSlaveConfiguration.GENERAL_STATUS_FRAME_RATE_MS = 1000;
        kSlaveConfiguration.FEEDBACK_STATUS_FRAME_RATE_MS = 1000;
        kSlaveConfiguration.QUAD_ENCODER_STATUS_FRAME_RATE_MS = 1000;
        kSlaveConfiguration.ANALOG_TEMP_VBAT_STATUS_FRAME_RATE_MS = 1000;
        kSlaveConfiguration.PULSE_WIDTH_STATUS_FRAME_RATE_MS = 1000;
    }

    // Create a WPI_TalonSRX with the default (out of the box) configuration.
    public static WPI_TalonSRX createDefaultTalon(int id) {
        return createTalon(id, kDefaultConfiguration);
    }

    public static WPI_TalonSRX createPermanentSlaveTalon(int id, int master_id) {
        final WPI_TalonSRX talon = createTalon(id, kSlaveConfiguration);
        talon.set(ControlMode.Follower, master_id);
        return talon;
    }

    public static WPI_TalonSRX createTalon(int id, Configuration config) {
        // TODO: It appears that all the methods that start with config are the persistent ones,
        // which of course are the ones we care about here. We should change to factory resetting
        // all of the config methods, and ignore the set/clear/etc. methods if we determine
        // my surmise to be correct. Right now I just want this to compile.
        WPI_TalonSRX talon = new LazyCANTalon(id, config.CONTROL_FRAME_PERIOD_MS);
        talon.changeMotionControlFramePeriod(config.MOTION_CONTROL_FRAME_PERIOD_MS);
        talon.setIntegralAccumulator(0, config.PID_IDX, config.TIMEOUT_MS);
        talon.clearMotionProfileHasUnderrun(config.TIMEOUT_MS);
        talon.clearMotionProfileTrajectories();
        talon.clearStickyFaults(config.TIMEOUT_MS);
        talon.configForwardLimitSwitchSource(LimitSwitchSource.Deactivated, config.NORMAL_SWITCH_STATE, config.TIMEOUT_MS);
//        talon.configMaxOutputVoltage(config.MAX_OUTPUT_VOLTAGE); XXX: I can't find a way to do this with the new API
        talon.configNominalOutputForward(config.NOMINAL_PERCENT, config.TIMEOUT_MS);
        talon.configNominalOutputReverse(-config.NOMINAL_PERCENT, config.TIMEOUT_MS);
        talon.configPeakOutputForward(config.PEAK_PERCENT, config.TIMEOUT_MS);
        talon.configPeakOutputReverse(-config.PEAK_PERCENT, config.TIMEOUT_MS);
        talon.configReverseLimitSwitchSource(LimitSwitchSource.Deactivated, config.NORMAL_SWITCH_STATE, config.TIMEOUT_MS);
        talon.setNeutralMode(NeutralMode.Brake);
        talon.enableCurrentLimit(config.ENABLE_CURRENT_LIMIT);
        talon.configForwardSoftLimitEnable(config.ENABLE_SOFT_LIMIT, config.TIMEOUT_MS);
        talon.configReverseSoftLimitEnable(config.ENABLE_SOFT_LIMIT, config.TIMEOUT_MS);
//        talon.enableZeroSensorPositionOnForwardLimit(false);
//        talon.enableZeroSensorPositionOnIndex(false, false); XXX: I can't find any equivalents for these in the new api.
//        talon.enableZeroSensorPositionOnReverseLimit(false);
        talon.setInverted(false); // XXX: Does this do the same thing as reverseOutput and reverseSensor? Documentation seems to say so.
        talon.configContinuousCurrentLimit(config.CURRENT_LIMIT, config.TIMEOUT_MS);
        talon.setExpiration(config.EXPIRATION_TIMEOUT_SECONDS);
        talon.configForwardSoftLimitThreshold(config.FORWARD_SOFT_LIMIT, config.TIMEOUT_MS);
        talon.setInverted(config.INVERTED);
//        talon.setNominalClosedLoopVoltage(config.NOMINAL_CLOSED_LOOP_VOLTAGE); XXX: This doesn't exist in the new api?! What a surprise!
        talon.setSelectedSensorPosition(0, config.PID_IDX, config.TIMEOUT_MS);
        talon.selectProfileSlot(0, config.PID_IDX);
        talon.configReverseSoftLimitThreshold(config.FORWARD_SOFT_LIMIT, config.TIMEOUT_MS);
        talon.setSafetyEnabled(config.SAFETY_ENABLED);
        talon.configVelocityMeasurementPeriod(config.VELOCITY_MEASUREMENT_PERIOD, config.TIMEOUT_MS);
        talon.configVelocityMeasurementWindow(config.VELOCITY_MEASUREMENT_ROLLING_AVERAGE_WINDOW, config.TIMEOUT_MS);
        talon.configClosedloopRamp(config.VOLTAGE_RAMP_RATE, config.TIMEOUT_MS);
        talon.configOpenloopRamp(config.VOLTAGE_RAMP_RATE, config.TIMEOUT_MS);

        talon.setStatusFramePeriod(StatusFrameEnhanced.Status_1_General, config.GENERAL_STATUS_FRAME_RATE_MS, config.TIMEOUT_MS);
        talon.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0 , config.FEEDBACK_STATUS_FRAME_RATE_MS, config.TIMEOUT_MS); // XXX: was Feedback
        talon.setStatusFramePeriod(StatusFrameEnhanced.Status_3_Quadrature , config.QUAD_ENCODER_STATUS_FRAME_RATE_MS, config.TIMEOUT_MS);
        talon.setStatusFramePeriod(StatusFrameEnhanced.Status_4_AinTempVbat,
                config.ANALOG_TEMP_VBAT_STATUS_FRAME_RATE_MS, config.TIMEOUT_MS);

        return talon;
    }

    /**
     * Run this on a fresh talon to produce good values for the defaults.
     */
    public static String getFullTalonInfo(WPI_TalonSRX talon) {
//        StringBuilder sb = new StringBuilder().append("isRevLimitSwitchClosed = ")
//                .append(talon.isRevLimitSwitchClosed()).append("\n").append("getBusVoltage = ")
//                .append(talon.getBusVoltage()).append("\n").append("isForwardSoftLimitEnabled = ")
//                .append(talon.isForwardSoftLimitEnabled()).append("\n").append("getFaultRevSoftLim = ")
//                .append(talon.getFaultRevSoftLim()).append("\n").append("getStickyFaultOverTemp = ")
//                .append(talon.getStickyFaultOverTemp()).append("\n").append("isZeroSensorPosOnFwdLimitEnabled = ")
//                .append(talon.isZeroSensorPosOnFwdLimitEnabled()).append("\n")
//                .append("getMotionProfileTopLevelBufferCount = ").append(talon.getMotionProfileTopLevelBufferCount())
//                .append("\n").append("getNumberOfQuadIdxRises = ").append(talon.getNumberOfQuadIdxRises()).append("\n")
//                .append("getInverted = ").append(talon.getInverted()).append("\n")
//                .append("getPulseWidthRiseToRiseUs = ").append(talon.getPulseWidthRiseToRiseUs()).append("\n")
//                .append("getError = ").append(talon.getError()).append("\n").append("isSensorPresent = ")
//                .append(talon.isSensorPresent(FeedbackDevice.CTRE_MagEncoder_Relative)).append("\n")
//                .append("isControlEnabled = ").append(talon.isControlEnabled()).append("\n").append("getTable = ")
//                //.append(talon.getTable()).append("\n") // XXX: Sendable::getTable method has disappeared
//                .append("isEnabled = ").append(talon.isEnabled()).append("\n")
//                .append("isZeroSensorPosOnRevLimitEnabled = ").append(talon.isZeroSensorPosOnRevLimitEnabled())
//                .append("\n").append("isSafetyEnabled = ").append(talon.isSafetyEnabled()).append("\n")
//                .append("getOutputVoltage = ").append(talon.getOutputVoltage()).append("\n").append("getTemperature = ")
//                .append(talon.getTemperature()).append("\n").append("getSmartDashboardType = ")
//                // .append(talon.getSmartDashboardType()).append("\n") // XXX: Sendable::getSmartDashboardType has disappeared
//                .append("getPulseWidthPosition = ")
//                .append(talon.getPulseWidthPosition()).append("\n").append("getOutputCurrent = ")
//                .append(talon.getOutputCurrent()).append("\n").append("get = ").append(talon.get()).append("\n")
//                .append("isZeroSensorPosOnIndexEnabled = ").append(talon.isZeroSensorPosOnIndexEnabled()).append("\n")
//                .append("getMotionMagicCruiseVelocity = ").append(talon.getMotionMagicCruiseVelocity()).append("\n")
//                .append("getStickyFaultRevSoftLim = ").append(talon.getStickyFaultRevSoftLim()).append("\n")
//                .append("getFaultRevLim = ").append(talon.getFaultRevLim()).append("\n").append("getEncPosition = ")
//                .append(talon.getEncPosition()).append("\n").append("getIZone = ").append(talon.getIZone()).append("\n")
//                .append("getAnalogInPosition = ").append(talon.getAnalogInPosition()).append("\n")
//                .append("getFaultUnderVoltage = ").append(talon.getFaultUnderVoltage()).append("\n")
//                .append("getCloseLoopRampRate = ").append(talon.getCloseLoopRampRate()).append("\n")
//                .append("toString = ").append(talon.toString()).append("\n")
//                // .append("getMotionMagicActTrajPosition =
//                // ").append(talon.getMotionMagicActTrajPosition()).append("\n")
//                .append("getF = ").append(talon.getF()).append("\n").append("getClass = ").append(talon.getClass())
//                .append("\n").append("getAnalogInVelocity = ").append(talon.getAnalogInVelocity()).append("\n")
//                .append("getI = ").append(talon.getI()).append("\n").append("isReverseSoftLimitEnabled = ")
//                .append(talon.isReverseSoftLimitEnabled()).append("\n")
//                // .append("getPIDSourceType = ").append(talon.getPIDSourceType()).append("\n") // XXX Sendable change
//                .append("getEncVelocity = ")
//                .append(talon.getEncVelocity()).append("\n").append("GetVelocityMeasurementPeriod = ")
//                .append(talon.GetVelocityMeasurementPeriod()).append("\n").append("getP = ").append(talon.getP())
//                .append("\n").append("GetVelocityMeasurementWindow = ").append(talon.GetVelocityMeasurementWindow())
//                .append("\n").append("getDeviceID = ").append(talon.getDeviceID()).append("\n")
//                .append("getStickyFaultRevLim = ").append(talon.getStickyFaultRevLim()).append("\n")
//                // .append("getMotionMagicActTrajVelocity =
//                // ").append(talon.getMotionMagicActTrajVelocity()).append("\n")
//                .append("getReverseSoftLimit = ").append(talon.getReverseSoftLimit()).append("\n").append("getD = ")
//                .append(talon.getD()).append("\n").append("getFaultOverTemp = ").append(talon.getFaultOverTemp())
//                .append("\n").append("getForwardSoftLimit = ").append(talon.getForwardSoftLimit()).append("\n")
//                .append("GetFirmwareVersion = ").append(talon.GetFirmwareVersion()).append("\n")
//                .append("getLastError = ").append(talon.getLastError()).append("\n").append("isAlive = ")
//                .append(talon.isAlive()).append("\n").append("getPinStateQuadIdx = ").append(talon.getPinStateQuadIdx())
//                .append("\n").append("getAnalogInRaw = ").append(talon.getAnalogInRaw()).append("\n")
//                .append("getFaultForLim = ").append(talon.getFaultForLim()).append("\n").append("getSpeed = ")
//                .append(talon.getSpeed()).append("\n").append("getStickyFaultForLim = ")
//                .append(talon.getStickyFaultForLim()).append("\n").append("getFaultForSoftLim = ")
//                .append(talon.getFaultForSoftLim()).append("\n").append("getStickyFaultForSoftLim = ")
//                .append(talon.getStickyFaultForSoftLim()).append("\n").append("getClosedLoopError = ")
//                .append(talon.getClosedLoopError()).append("\n").append("getSetpoint = ").append(talon.getSetpoint())
//                .append("\n").append("isMotionProfileTopLevelBufferFull = ")
//                .append(talon.isMotionProfileTopLevelBufferFull()).append("\n").append("getDescription = ")
//                .append(talon.getDescription()).append("\n").append("hashCode = ").append(talon.hashCode()).append("\n")
//                .append("isFwdLimitSwitchClosed = ").append(talon.isFwdLimitSwitchClosed()).append("\n")
//                .append("getPinStateQuadA = ").append(talon.getPinStateQuadA()).append("\n")
//                .append("getPinStateQuadB = ").append(talon.getPinStateQuadB()).append("\n").append("GetIaccum = ")
//                .append(talon.GetIaccum()).append("\n").append("getFaultHardwareFailure = ")
//                .append(talon.getFaultHardwareFailure()).append("\n").append("pidGet = ").append(talon.pidGet())
//                .append("\n").append("getBrakeEnableDuringNeutral = ").append(talon.getBrakeEnableDuringNeutral())
//                .append("\n").append("getStickyFaultUnderVoltage = ").append(talon.getStickyFaultUnderVoltage())
//                .append("\n").append("getPulseWidthVelocity = ").append(talon.getPulseWidthVelocity()).append("\n")
//                .append("GetNominalClosedLoopVoltage = ").append(talon.GetNominalClosedLoopVoltage()).append("\n")
//                .append("getPosition = ").append(talon.getPosition()).append("\n").append("getExpiration = ")
//                .append(talon.getExpiration()).append("\n").append("getPulseWidthRiseToFallUs = ")
//                .append(talon.getPulseWidthRiseToFallUs()).append("\n")
//                // .append("createTableListener = ").append(talon.createTableListener()).append("\n")
//                .append("getControlMode = ").append(talon.getControlMode()).append("\n")
//                .append("getMotionMagicAcceleration = ").append(talon.getMotionMagicAcceleration()).append("\n")
//                .append("getControlMode = ").append(talon.getControlMode());
        // TODO: Port these over and set everything in config to _actual_ factory defaults.
        return "TODO";
    }
}
