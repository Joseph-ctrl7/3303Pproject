3303 Project
Contributors: Joseph Anyia, Israel Okonkwo, Toluwalope Ajisola, 
              Mehdi Khan, and Amith Kumar Das Orko
Classes: 

Elevator - This class is controlled by the ElevatorSubsystem. It handles movements such as turning on lamps(button pressed or direction lamps), opening/closing of elevator door and going up or down.

ElevatorSubsystem - This class manages sending and receiving of requests from the scheduler and notifies the Elevator class to perform specific tasks.

Floor - This class is controlled by the Floorsubsystem. It handles floor requests and it has it's specific floor number, car button and floor lamps. 

FloorSubsystem- This class manages sending and receiving of requests from the Scheduler. The FloorSubsystem reads in events using the following format : Time, floor or elevator number, and button. 

Scheduler - This class is the bridge between the Elevator and the Floor systems. It receives request from the FloorSubsystem and also replies to requests from the Elevator when there is work to do.

TestCases - This class is used to run tests on the whole system.

State -  This class describes when the elevator is starting, moving, stopping and when the doors are opening and closing, whether the elevator has reached its destination, stopped at a floor and opened its doors

EndState - This is a subclass of State describing when the doors are opening and when passengers are entering, where the elevator is heading to stop and let passengers in

UI - This class contains the GUI interface. It consists of icons depicting when elevator doors open/close, a checkbox that shows the elevator status (✅ == working elevator, ❌ == elevator not working), an elevator arrival LED that turns green when the elevator and a checkbox that shows the current floor the elevator is at.

Time - This class contains the timer for the faults.


System Flow:
-	Floor requests an elevator and sends it to the scheduler
-	Scheduler decides which elevator to send the request
-	Scheduler relays request to the elevator subsystem
-	Elevator subsystem relays it to the appropriate elevator
-	Elevator that receives the request responds to the scheduler and updates it about it's status


