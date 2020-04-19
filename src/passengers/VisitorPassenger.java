package passengers;

import elevators.Elevator;
import cecs277.Simulation;
import events.PassengerNextDestinationEvent;

/**
 * A VisitorPassenger has a single destination and a single duration (in seconds), which is how long the Visitor
 * will "disappear" for after departing the elevator on their destination floor. After that duration, the Visitor
 * will reappear on the original destination floor, set its new destination to floor 1, then leave the building when it
 * arrives on floor 1.
 */
public class VisitorPassenger extends Passenger {
	// TODO: add fields, constructors, and accessors to implement this class.
    // [DONE]

	private int mDestination;
	private int mDuration;

	public VisitorPassenger(int destinationFloor, int durationTime) {
		super();
		this.mDestination = destinationFloor;
		this.mDuration = durationTime;
	}
	
	@Override
	public int getDestination() {
		return this.mDestination;
	}

	public int getDuration() { return this.mDuration; }
	
	// TODO: implement this template method variant. A Visitor will join an elevator whose passenger count is less than its capacity.
    // [DONE]
    @Override
	protected boolean willBoardElevator(Elevator elevator) {
		return (elevator.getPassengerCount() < elevator.getCapacity());
	}
	
	/*
	 TODO: implement this template method variant, which is called when the passenger is leaving the elevator it
	 is on. A Visitor that is departing on floor 1 just leaves the building, printing a message to System.out.
	 A visitor that is departing on any other floor sets their new destination to floor 1, and then schedules a
	 PassengerNextDestinationEvent to occur when they are supposed to "reappear" (their duration field).
	*/
	// [ATTEMPTED]

	@Override
	protected void leavingElevator(Elevator elevator) {
		if (elevator.getCurrentFloor().getNumber() == 1){
			System.out.println("Passenger left on Floor 1");
		}
		else {
			this.mDestination = 1;
			Simulation s = elevator.getBuilding().getSimulation();
			PassengerNextDestinationEvent ev = new PassengerNextDestinationEvent(s.currentTime() + mDuration,
					this,	elevator.getCurrentFloor());
			s.scheduleEvent(ev);

		/* Example of how to schedule a PassengerNextDestinationEvent:
		Simulation s = elevator.getBuilding().getSimulation();
		PassengerNextDestinationEvent ev = new PassengerNextDestinationEvent(s.currentTime() + 10, this,
		 elevator.getCurrentFloor());
		s.scheduleEvent(ev);

		Schedules this passenger to reappear on this floor 10 seconds from now.
		 */
		}
	}
	
	// TODO: return "Visitor heading to floor {destination}", replacing {destination} with the floor number.
	@Override
	public String toString() {
		return "visitor with destination " + this.mDestination;
	}
	
	@Override
	public void elevatorDecelerating(Elevator elevator) {
		// Don't care.
	}
}
