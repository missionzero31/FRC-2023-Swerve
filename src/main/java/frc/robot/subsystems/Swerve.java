package frc.robot.subsystems;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;

import frc.robot.SwerveModule;
import frc.robot.Constants;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Swerve extends SubsystemBase {
    public SwerveDriveOdometry swerveOdometry;
    public SwerveModule[] mSwerveMods;
    public AHRS gyro;

    // High speed swerve?
    public Boolean swerveHighSpeedMode;

    public Swerve() {

        swerveHighSpeedMode = true;

        gyro = new AHRS(SPI.Port.kMXP);
        zeroGyro();
        
        
        swerveOdometry = new SwerveDriveOdometry(Constants.Swerve.swerveKinematics, getYaw());

        // create SweveModule for each swerve drive and putting the in mswervemods array
        mSwerveMods = new SwerveModule[] {
            new SwerveModule(0, Constants.Swerve.Mod0.constants),
            new SwerveModule(1, Constants.Swerve.Mod1.constants),
            new SwerveModule(2, Constants.Swerve.Mod2.constants),
            new SwerveModule(3, Constants.Swerve.Mod3.constants)
        };
    }

    public void drive(Translation2d translation, double rotation, boolean fieldRelative, boolean isOpenLoop) {
        SwerveModuleState[] swerveModuleStates =
            Constants.Swerve.swerveKinematics.toSwerveModuleStates(
                // fieldRelative ? ChassisSpeeds.fromFieldRelativeSpeeds(
                //                     translation.getX(), 
                //                     translation.getY(), 
                //                     rotation, 
                //                     getYaw()
                //                 )
                //                 : new ChassisSpeeds(
                //                     translation.getX(), 
                //                     translation.getY(), 
                //                     rotation)
                //                 );
                fieldRelative ? new ChassisSpeeds (
                    translation.getX() * Math.cos(gyro.getYaw() * (Math.PI/180)) + translation.getY() * Math.sin(gyro.getYaw() * (Math.PI/180)),
                    -translation.getX() * Math.sin(gyro.getYaw() * (Math.PI/180)) + translation.getY() * Math.cos(gyro.getYaw() * (Math.PI/180)),
                    rotation
                )
                : new ChassisSpeeds(
                    translation.getX(), 
                    translation.getY(), 
                    rotation)
                );
        SwerveDriveKinematics.desaturateWheelSpeeds(swerveModuleStates, Constants.Swerve.maxSpeed);

        for(SwerveModule mod : mSwerveMods){
            mod.setDesiredState(swerveModuleStates[mod.moduleNumber], isOpenLoop);
        }
    }    

    /* Used by SwerveControllerCommand in Auto */
    public void setModuleStates(SwerveModuleState[] desiredStates) {
        SwerveDriveKinematics.desaturateWheelSpeeds(desiredStates, Constants.Swerve.maxSpeed);
        
        for(SwerveModule mod : mSwerveMods){
            mod.setDesiredState(desiredStates[mod.moduleNumber], false);
        }
    }    

    public Pose2d getPose() {
        return swerveOdometry.getPoseMeters();
    }

    public void resetOdometry(Pose2d pose) {
        swerveOdometry.resetPosition(pose, getYaw());
    }

    public SwerveModuleState[] getStates(){
        SwerveModuleState[] states = new SwerveModuleState[4];
        for(SwerveModule mod : mSwerveMods){
            states[mod.moduleNumber] = mod.getState();
        }
        return states;
    }

    public void zeroGyro(){
        //gyro.reset();
        gyro.zeroYaw();
    }

    public Rotation2d getYaw() {
        double yaw = gyro.getAngle();
        return (Constants.Swerve.invertGyro) ? Rotation2d.fromDegrees(360 - yaw) : Rotation2d.fromDegrees(yaw);
    }

    // alternates between high speed and low speed mode
    public void toggleSwerveMode() {
        swerveHighSpeedMode = !swerveHighSpeedMode;
    }

    @Override
    public void periodic(){
        swerveOdometry.update(getYaw(), getStates());
        
        SmartDashboard.putNumber("Gyroscope Yaw", gyro.getYaw());
        SmartDashboard.putNumber("Gyroscope Angle", gyro.getAngle());
        SmartDashboard.putNumber("Gyroscope Pitch", gyro.getPitch());

        for(SwerveModule mod : mSwerveMods){
            SmartDashboard.putNumber("Mod " + mod.moduleNumber + " Cancoder", mod.getCanCoder().getDegrees());
            SmartDashboard.putNumber("Mod " + mod.moduleNumber + " Integrated", mod.getState().angle.getDegrees());
            SmartDashboard.putNumber("Mod " + mod.moduleNumber + " Velocity", mod.getState().speedMetersPerSecond);   
            System.out.println("Mod " + mod.moduleNumber + " Integrated Angle: " + mod.getState().angle.getDegrees());
        }
        System.out.println("\n");
    }
}