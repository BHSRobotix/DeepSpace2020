package org.usfirst.frc2876.DeepSpace2019.commands;

import org.usfirst.frc2876.DeepSpace2019.Robot;
import org.usfirst.frc2876.DeepSpace2019.utils.PixyLinePID;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.command.Command;

public class PixyLine extends Command {
  PixyLinePID pixyPID;

  public PixyLine() {
    requires(Robot.vision);
    requires(Robot.driveTrain);
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    pixyPID = Robot.vision.currentPixy();
    pixyPID.lineController.reset();
    pixyPID.lineController.setSetpoint(35);
    pixyPID.lineController.enable();
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    // double out = Robot.vision.lineController.get();
    double out = pixyPID.lineController.get();
    double baseVelocity = 0;// Robot.driveTrain.MAX_RPM*.3;

    XboxController xbox = Robot.oi.getXboxController();
    double speed = xbox.getY(Hand.kLeft);
    double rotate = -xbox.getX(Hand.kRight);

    // Only apply steering correction if we see a line and are moving forward.
    double MAX_PIXY_ROTATE = .7;
    if (Math.abs(speed) > .1 && Robot.vision.isVectorPresent()) {
      rotate = -out * MAX_PIXY_ROTATE;
    }
    // if robot is to the left of the line and coming in at sharp off angle you get
    // vector like: (32 14) (69 29) xdiff=-37 ydiff=-15

    // if robot is to the right of the line and coming in at sharp off angle you get
    // vector like: (13 42) (50 21) xdiff=-37 ydiff=21

    double MAX_PIXY_SPEED = .25;
    double MAX_NO_PIXY_SPEED = .4;
    if (Robot.vision.isVectorPresent()) {
      if (speed > MAX_PIXY_SPEED) {
        speed = MAX_PIXY_SPEED;
      } else if (speed < -MAX_PIXY_SPEED) {
        speed = -MAX_PIXY_SPEED;
      }
    } else {
      if (speed > MAX_NO_PIXY_SPEED) {
        speed = MAX_NO_PIXY_SPEED;
      } else if (speed < -MAX_NO_PIXY_SPEED) {
        speed = -MAX_NO_PIXY_SPEED;
      }
    }
    if(Robot.driveTrain.isHatchForward()){
      Robot.vision.updateShuffleDrivetrainOutputs(speed, rotate);
      Robot.driveTrain.setVelocityArcadeJoysticks(speed, rotate);
    } else {
      Robot.vision.updateShuffleDrivetrainOutputs(-speed, rotate);
      Robot.driveTrain.setVelocityArcadeJoysticks(-speed, rotate);
    }

  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {

    return false;
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
    pixyPID.lineController.disable();
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
    end();
  }

}
