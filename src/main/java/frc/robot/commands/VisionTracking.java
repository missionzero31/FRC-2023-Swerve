package frc.robot.commands;

import frc.robot.Constants;
import frc.robot.subsystems.LimelightSubsystem;
import edu.wpi.first.math.geometry.Translation2d;
import frc.robot.subsystems.Swerve;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class VisionTracking extends CommandBase {

    private final Swerve s_Swerve;
    LimelightSubsystem s_Limelight;
    private final Boolean fieldRelative;

    private Translation2d translation;
    private double rotation;

    public VisionTracking(Swerve s_Swerve, LimelightSubsystem s_Limelight){
        this.s_Swerve = s_Swerve;
        this.s_Limelight = s_Limelight;
        fieldRelative = Constants.CustomConstants.fieldRelative;
    }

    @Override
    public void execute() {
        translation = new Translation2d(0, 0);
        rotation = -1*s_Limelight.horizontalOffset()/2;
        s_Swerve.drive(translation, rotation, fieldRelative);
    }
    
}