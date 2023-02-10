package frc.robot.subsystems;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

import org.json.simple.JSONObject;

// import com.kauailabs.navx.frc.AHRS;
// import edu.wpi.first.wpilibj.SPI;

// import frc.robot.SwerveModule;
// import frc.robot.Constants;

// import edu.wpi.first.math.kinematics.ChassisSpeeds;
// import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
// import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
// import edu.wpi.first.math.geometry.Pose2d;
// import edu.wpi.first.math.geometry.Rotation2d;
// import edu.wpi.first.math.geometry.Translation2d;
// import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.util.LimelightHelpers;

public class LimelightSubsystem extends SubsystemBase {
    
    NetworkTable table;
    double tx;
    double ty;
    double ta;

    double[] targetPose_CameraSpace;
    double ry;

    //TODO: LED | LedSubsystem s_LEDSubsystem;

    //TODO: LED | public LimelightSubsystem(LedSubsystem s_LEDSubsystem){
    public LimelightSubsystem(){
        //TODO: LED | this.s_LEDSubsystem = s_LEDSubsystem;
        table = NetworkTableInstance.getDefault().getTable("limelight");
    }

    public double horizontalOffset(){
        return tx;
    }   
    public double horizontalRotation(){
        return ry;
    }

    public void setMode(int number) {
        table.getEntry("ledMode").setDouble(number);
    }

    @Override
    public void periodic() {
        tx = LimelightHelpers.getTX("");
        ty = LimelightHelpers.getTY("");
        ta = LimelightHelpers.getTA("");

        // TODO: 3D ? (experimental)
        targetPose_CameraSpace = LimelightHelpers.getTargetPose_CameraSpace("");
        ry = targetPose_CameraSpace[1];


        //post to smart dashboard periodically
        SmartDashboard.putNumber("LimelightX", tx);
        SmartDashboard.putNumber("LimelightY", ty);
        SmartDashboard.putNumber("LimelightArea", ta);

        for(int i=0; i<targetPose_CameraSpace.length; i++) {
            SmartDashboard.putNumber("targetpose_cameraspace" + i, targetPose_CameraSpace[i]);
        }
        SmartDashboard.putNumber("LimeLightRY", ry);

        //TODO: LED | s_LEDSubsystem.visionTrackingLED(area);
    }
}