3303 Project
Contributors: Joseph Anyia, Israel Okonkwo, Tolu Ajisola, 
              Mehdi Khan, and Amith Kumar Das Orko
Classes: 

Elevator - This class is controlled by the ElevatorSubsystem. It handles movements such as turning on lamps(button pressed or direction lamps), opening/closing of elevator door and going up or down.

ElevatorSubsystem - This class manages sending and receiving of requests from the scheduler and notifies the Elevator class to perform specific tasks.

Floor - This class is controlled by the Floorsubsystem. It handles floor requests and it has it's specific floor number, car button and floor lamps. 

FloorSubsystem- This class manages sending and receiving of requests from the Scheduler. The FloorSubsystem reads in events using the following format : Time, floor or elevator number, and button. 

Scheduler - This class is the bridge between the Elevator and the Floor systems. It receives request from the FloorSubsystem and also replies to requests from the Elevator when there is work to do.
