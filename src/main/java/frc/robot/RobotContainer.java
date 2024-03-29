// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import frc.robot.autos.PathPlannerAuto;
import frc.robot.commands.DriveElevator;
import frc.robot.commands.RotateArm;
//import frc.robot.autos.forward;
import frc.robot.commands.TeleopSwerve;
import frc.robot.commands.VisionTracking;
import frc.robot.commands.setIntake;
import frc.robot.commands.Positions.StowPosition;
import frc.robot.commands.Positions.Intake.IntakeFromGroundPosition;
import frc.robot.commands.DriveClaw;
import frc.robot.subsystems.ArmSubsystem;
import frc.robot.subsystems.ClawSubsystem;
import frc.robot.subsystems.ElevatorSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
//TODO: LED | import frc.robot.subsystems.LedSubsystem;
import frc.robot.subsystems.LimelightSubsystem;
import frc.robot.subsystems.Swerve;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.util.AutonManager;
import frc.util.Controller;

/*
 * This class is where the bulk of the robot should be declared.  Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls).  Instead, the structure of the robot
 * (including subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
  //Autonomous manager import
  private final AutonManager autonManager = new AutonManager();

  // The robot's subsystems
  private final Swerve s_Swerve = new Swerve();
  //TODO: LED | private final LedSubsystem s_Led = new LedSubsystem(120);
  //TODO: LED | private final LimelightSubsystem Limelight = new LimelightSubsystem(s_Led);
  private final LimelightSubsystem Limelight = new LimelightSubsystem();

  private final ElevatorSubsystem Elevator = new ElevatorSubsystem();
  private final ArmSubsystem Arm = new ArmSubsystem(Elevator);
  private final ClawSubsystem Claw = new ClawSubsystem();
  private final IntakeSubsystem Intake = new IntakeSubsystem();


  // The driver's controller
  private final Controller driver = new Controller(0);
  private final Controller operator = new Controller(1);

  public static boolean isAutoTargetOn = false;

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {
    addAutonomousChoices();
    autonManager.displayChoices();
    // Configure the button bindings
    configureButtonBindings();
  }

  private void addAutonomousChoices() {
    autonManager.addOption("Do Nothing", new InstantCommand());
    autonManager.addOption("PathPlanner Auto", new PathPlannerAuto(s_Swerve));
}

  /**
   * Use this method to define your button->command mappings. Buttons can be
   * created by
   * instantiating a {@link edu.wpi.first.wpilibj.GenericHID} or one of its
   * subclasses ({@link
   * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then calling
   * passing it to a
   * {@link JoystickButton}.
   */
  private void configureButtonBindings() {
        // The left stick controls translation of the robot.
        // Turning is controlled by the X axis of the right stick.s
    s_Swerve.setDefaultCommand(new TeleopSwerve(s_Swerve, driver));
    driver.buttonA.onTrue(new InstantCommand(() -> s_Swerve.toggleSwerveMode()));
    driver.buttonY.onTrue(new InstantCommand(() -> s_Swerve.zeroGyro()));
    driver.buttonX.onTrue(new VisionTracking(s_Swerve, Limelight));

    Elevator.setDefaultCommand(new DriveElevator(operator, Elevator));
    Arm.setDefaultCommand(new RotateArm(operator, Arm));
    Claw.setDefaultCommand(new DriveClaw(operator, Claw));
    Intake.setDefaultCommand(new setIntake(operator, Intake));

    operator.buttonA.onTrue(new StowPosition(Elevator, Arm, Claw));
    operator.buttonX.onTrue(new IntakeFromGroundPosition(Elevator, Arm, Claw));

  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    return autonManager.getSelected();
  }
}
