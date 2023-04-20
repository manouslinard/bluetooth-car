/*
  Install Adafruit Motor Shield Library before uploading this code.
  AFMotor Library https://learn.adafruit.com/adafruit-motor-shield/library-install
  Tutorial on how to install: https://youtu.be/vooJEyco1J4
  Important notice: Switch off the battery switch before connecting the Arduino board to pc.
*/

#include <AFMotor.h>
#include <SoftwareSerial.h>

SoftwareSerial bluetoothSerial(9, 10); // RX, TX

//initial motors pin
AF_DCMotor motor1(1, MOTOR12_1KHZ);
AF_DCMotor motor2(2, MOTOR12_1KHZ);
AF_DCMotor motor3(3, MOTOR34_1KHZ);
AF_DCMotor motor4(4, MOTOR34_1KHZ);

const int trigPin = A0;
const int echoPin = A1;
bool ultraPilot = false;
long max_obs_dist = 30;
int rot_delay_ultraPilot = 1; // seconds

int motor1_speed = 150;
int motor2_speed = 255;
int motor3_speed = 255;
int motor4_speed = 210;

int motor_rotate_speed = 150;

char command;

bool followLine = false;
int dark_value = 500; // above this value -> detects dark line.
int sensor_limit = 100;
const int sensorMiddle = A5; // Analog input pin for the sensor
int sensorMiddleValue = 0; // Variable to store the sensor value
const int sensorRight = A4; // Analog input pin for the sensor
int sensorRightValue = 0; // Variable to store the sensor value
const int sensorLeft = A3; // Analog input pin for the sensor
int sensorLeftValue = 0; // Variable to store the sensor value
void setup()
{
  bluetoothSerial.begin(9600);  //Set the baud rate to your Bluetooth module.
  Serial.begin(9600); // Initialize serial communication at 9600 bits per second
  pinMode(trigPin, OUTPUT);
  pinMode(echoPin, INPUT);
}

void loop() {
  //Serial.println(analogRead(sensorMiddle)); // Print the value to the serial monitor
  //Serial.println(measureDist());
  if (bluetoothSerial.available() > 0) {
    command = bluetoothSerial.read();

    Stop(); //initialize with motors stoped
    if (command != 'S') {
      Serial.println(command);
    }
    if (followLine) {
      // Serial.println("Following Line");
      followLineFunc();
      if (command == 'x'){
        Serial.println("Unfollow Line");
        followLine = false;
      }
    } 
    else if (ultraPilot) {
      ultraPilotFunc();
      if (command == 'v'){
        Serial.println("Stop autopilot");
        ultraPilot = false;
        Stop();
      }
    }
    else {
      switch (command) {
        case 'F':
          forward();
          break;
        case 'B':
          back();
          break;
        case 'L':
          left();
          break;
        case 'R':
          right();
          break;
        case 'X':
          Serial.println("Follow Line");
          followLine = true;
          break;
        case 'V':
          // auto-pilot with obstacle avoidance.
          ultraPilot = true;
          break;
      }
    }
  }
}

void followLineFunc()
{
  // Serial.println(command);
  int middle_value = analogRead(sensorMiddle);
  int right_value = analogRead(sensorRight);
  int left_value = analogRead(sensorLeft);

  // if sensor's value is greater than dark value -> sensor is on black line 
  if (middle_value >= dark_value) {
    forward();
  } else if (left_value >= dark_value && right_value < dark_value) {
    left();
  } else if (left_value < dark_value && right_value >= dark_value) {
    right();
  } else {
    // goes right when line not found.
    right();
  }

}


long measureDist(){
  digitalWrite(trigPin, LOW);
  delayMicroseconds(2);
  
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);
  
  long duration = pulseIn(echoPin, HIGH);
  
  // Calculate distance in cm
  long distance_cm = duration / 29 / 2;
  
  // Serial.print("Distance: ");
  // Serial.print(distance_cm);
  // Serial.println(" cm");
  return distance_cm;
}

void ultraPilotFunc(){
  if (!ultraPilot) {  // due to delay, this is mandatory.
    return;
  }
  long obs_dist = measureDist();
  if (obs_dist > max_obs_dist) {
    forward();
  } else {
    right();
    delay(rot_delay_ultraPilot * 1000);
  }
}

void left()
{
  motor1.setSpeed(255); //Define maximum velocity
  motor1.run(BACKWARD);  //rotate the motor clockwise
  motor2.setSpeed(motor_rotate_speed); //Define maximum velocity
  motor2.run(BACKWARD);  //rotate the motor clockwise
  motor3.setSpeed(motor_rotate_speed); //Define maximum velocity
  motor3.run(FORWARD);  //rotate the motor clockwise
  motor4.setSpeed(motor_rotate_speed); //Define maximum velocity
  motor4.run(FORWARD);  //rotate the motor clockwise
}

void right()
{
  motor1.setSpeed(motor_rotate_speed); //Define maximum velocity
  motor1.run(FORWARD); //rotate the motor anti-clockwise
  motor2.setSpeed(motor_rotate_speed); //Define maximum velocity
  motor2.run(FORWARD); //rotate the motor anti-clockwise
  motor3.setSpeed(motor_rotate_speed); //Define maximum velocity
  motor3.run(BACKWARD); //rotate the motor anti-clockwise
  motor4.setSpeed(motor_rotate_speed); //Define maximum velocity
  motor4.run(BACKWARD); //rotate the motor anti-clockwise
}

void forward()
{
  motor1.setSpeed(motor1_speed); //Define maximum velocity
  motor1.run(FORWARD); //rotate the motor anti-clockwise
  motor2.setSpeed(motor2_speed); //Define maximum velocity
  motor2.run(FORWARD); //rotate the motor anti-clockwise
  motor3.setSpeed(motor3_speed); //Define maximum velocity
  motor3.run(FORWARD);  //rotate the motor clockwise
  motor4.setSpeed(motor4_speed); //Define maximum velocity
  motor4.run(FORWARD);  //rotate the motor clockwise
}

void back()
{
  motor1.setSpeed(motor1_speed); //Define maximum velocity
  motor1.run(BACKWARD);  //rotate the motor clockwise
  motor2.setSpeed(motor2_speed); //Define maximum velocity
  motor2.run(BACKWARD);  //rotate the motor clockwise
  motor3.setSpeed(motor3_speed); //Define maximum velocity
  motor3.run(BACKWARD); //rotate the motor anti-clockwise
  motor4.setSpeed(motor4_speed); //Define maximum velocity
  motor4.run(BACKWARD); //rotate the motor anti-clockwise
}

void Stop()
{
  motor1.setSpeed(0);  //Define minimum velocity
  motor1.run(RELEASE); //stop the motor when release the button
  motor2.setSpeed(0);  //Define minimum velocity
  motor2.run(RELEASE); //rotate the motor clockwise
  motor3.setSpeed(0);  //Define minimum velocity
  motor3.run(RELEASE); //stop the motor when release the button
  motor4.setSpeed(0);  //Define minimum velocity
  motor4.run(RELEASE); //stop the motor when release the button
}
