package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.networktables.DoublePublisher;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.TiltConstants;

public class TiltSubsystem extends SubsystemBase
{
    private VictorSPX tiltMotor = new VictorSPX(TiltConstants.kTiltCANId);

    private DutyCycleEncoder encoder = new DutyCycleEncoder(TiltConstants.kTiltDutyCycleEncoderDIOId);

    private PIDController pidController = new PIDController(
        TiltConstants.kTiltPositionP, 
        TiltConstants.kTiltPositionI, 
        TiltConstants.kTiltPositionD);

    private final NetworkTableInstance instance = NetworkTableInstance.getDefault();
    private final DoublePublisher encoderRotationsPublisher;
    private final DoublePublisher encoderAbsolutePositionPublisher;
    private final DoublePublisher encoderDistancePublisher;
    private final DoublePublisher encoderDistancePerRotationPublisher;
    private final DoublePublisher encoderFrequencyPublisher;
    private final DoublePublisher encoderPositionOffsetPublisher;
    private final DoublePublisher encoderScaledRotationsPublisher;

    private final DoublePublisher pidErrorPublisher;
    private final DoublePublisher pidSetpointPublisher;

    public TiltSubsystem()
    {
        tiltMotor.setInverted(true);
        tiltMotor.setNeutralMode(NeutralMode.Brake);

        encoder.setDistancePerRotation(TiltConstants.kTiltEncoderPositionFactor);

        pidController.setTolerance(TiltConstants.kPIDTolerance);

        encoderRotationsPublisher = instance.getDoubleTopic("TiltSubsystem/Encoder/RelativeRotations").publish();
        encoderAbsolutePositionPublisher = instance.getDoubleTopic("TiltSubsystem/Encoder/AbsolutePosition").publish();
        encoderDistancePublisher = instance.getDoubleTopic("TiltSubsystem/Encoder/Distance").publish();
        encoderDistancePerRotationPublisher = instance.getDoubleTopic("TiltSubsystem/Encoder/DistancePerRotations").publish();
        encoderFrequencyPublisher = instance.getDoubleTopic("TiltSubsystem/Encoder/Frequency").publish();
        encoderPositionOffsetPublisher = instance.getDoubleTopic("TiltSubsystem/Encoder/PositionOffset").publish();
        encoderScaledRotationsPublisher = instance.getDoubleTopic("TiltSubsystem/Encoder/ScaledPosition").publish();


        pidErrorPublisher = instance.getDoubleTopic("TiltSubsystem/PID/Error").publish();
        pidSetpointPublisher = instance.getDoubleTopic("TiltSubsystem/PID/Setpoint").publish();

        Timer.delay(3);
        getOffset();
  
    }

    public void setTiltMotor(double desiredPower)
    {
        tiltMotor.set(ControlMode.PercentOutput, desiredPower);
    }

    /**
     * Method that sets the desired position of the tilt subsystem.
     * 
     * @param desiredPosition set desired position in radians
     */
    public void setTiltPosition(double desiredPosition)
    {
        tiltMotor.set(ControlMode.PercentOutput, 
            pidController.calculate(getScaledPosition(), desiredPosition));
    }

    public void setPidSetpoint(double setpoint)
    {
        pidController.setSetpoint(setpoint);
    }

    public boolean getPidAtSetpoint()
    {
        return pidController.atSetpoint();
    }

    public double getScaledPosition()
    {
        return (encoder.getAbsolutePosition() - encoder.getPositionOffset()) * TiltConstants.kTiltEncoderPositionFactor;
    }

    public void getOffset()
    {
        encoder.setPositionOffset(encoder.getAbsolutePosition());
    }

    @Override
    public void periodic()
    {
        encoderRotationsPublisher.set(encoder.get());
        encoderAbsolutePositionPublisher.set(encoder.getAbsolutePosition());
        encoderDistancePublisher.set(encoder.getDistance());
        encoderDistancePerRotationPublisher.set(encoder.getDistancePerRotation());
        encoderFrequencyPublisher.set(encoder.getFrequency());
        encoderPositionOffsetPublisher.set(encoder.getPositionOffset());
        encoderScaledRotationsPublisher.set(getScaledPosition());

        pidErrorPublisher.set(pidController.getPositionError());
        pidSetpointPublisher.set(pidController.getSetpoint());
    }
}
