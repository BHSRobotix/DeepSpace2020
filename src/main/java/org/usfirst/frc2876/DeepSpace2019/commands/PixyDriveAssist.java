package org.usfirst.frc2876.DeepSpace2019.commands;

import org.usfirst.frc2876.DeepSpace2019.Robot;
import org.usfirst.frc2876.DeepSpace2019.utils.PixyLinePID;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class PixyDriveAssist extends Command {
  PixyLinePID pixyPID;

  public PixyDriveAssist() {

    requires(Robot.driveTrain);
    requires(Robot.vision);

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
    XboxController xbox = Robot.oi.getXboxController();
    double speed = xbox.getY(Hand.kLeft);
    double rotate = -xbox.getX(Hand.kRight);
    if (Robot.vision.isVectorPresent()) {
      if (Math.abs(speed) >= .1 ) {
        double MAX_SPEED = .2;
        if (speed >= MAX_SPEED) {
          speed = MAX_SPEED;
        } else if (speed <= MAX_SPEED){
          speed = -MAX_SPEED;
        }
        double turn = -pixyPID.lineController.get();
        Robot.vision.updateShuffleDrivetrainOutputs(speed, turn);
        Robot.driveTrain.setVelocityArcadeJoysticks(speed, turn);
      }
    } else {
      double MAX_NO_PIXY_SPEED = .4;
      if (speed > MAX_NO_PIXY_SPEED) {
        speed = MAX_NO_PIXY_SPEED;
      } else if (speed < -MAX_NO_PIXY_SPEED) {
        speed = -MAX_NO_PIXY_SPEED;
      }
      Robot.vision.updateShuffleDrivetrainOutputs(speed, rotate);
      Robot.driveTrain.setVelocityArcadeJoysticks(speed, rotate);
    }
    /*
     * if no see vector{ drive normally }
     * 
     * if not driving{ dont drive }
     * 
     * if see vector
     * if driving, turn ignore manual turning if conflicting limit top
     * 
     * else if not driving do nothing
     * speed }
     */

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
