package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.networktables.DoublePublisher;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.PowerDistribution;
//import edu.wpi.first.wpilibj.PowerDistribution.ModuleType;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ClimberConstants;

public class ClimberSubsystem extends SubsystemBase
{
    private VictorSPX climber1 = new WPI_VictorSPX(ClimberConstants.kClimber1CANId);
    private VictorSPX climber2 = new WPI_VictorSPX(ClimberConstants.kClimber2CANId);
    private PowerDistribution PDH = new PowerDistribution();
    private final NetworkTableInstance instance;
    private final DoublePublisher climber1CurrentPublisher;
    private final DoublePublisher climber2CurrentPublisher;

    public ClimberSubsystem()
    {
        climber1.setNeutralMode(NeutralMode.Brake);
        climber2.setNeutralMode(NeutralMode.Brake);
        //PDH = new PowerDistribution(1, ModuleType.kRev);
        instance = NetworkTableInstance.getDefault();
        climber1CurrentPublisher = instance.getDoubleTopic("/ClimberSubsystem/MotorsInfo/Climber1/Current").publish();
        climber2CurrentPublisher = instance.getDoubleTopic("/ClimberSubsystem/MotorsInfo/Climber2/Current").publish();
    }

    public double getCurrent(int num) 
    {
        return PDH.getCurrent(num);
    }

    public void setClimberSpeeds(double num)     
    {
        climber1.set(ControlMode.PercentOutput, num);
        climber2.set(ControlMode.PercentOutput, num);
    }

    @Override
    public void periodic()
    {
        climber1CurrentPublisher.set(getCurrent(ClimberConstants.kClimber1PDH));
        climber2CurrentPublisher.set(getCurrent(ClimberConstants.kClimber2PDH));
    }
}