package events;

import buildings.Building;
import passengers.Passenger;
import cecs277.Simulation;
import passengers.VisitorPassenger;
import passengers.WorkerPassenger;

import java.util.List;
import java.util.ArrayList;

import java.util.Random;

/**
 * A simulation event that adds a new random passenger on floor 1, and then schedules the next spawn event.
 */
public class SpawnPassengerEvent extends SimulationEvent {
	private static long SPAWN_MEAN_DURATION = 10_800;
	private static long SPAWN_STDEV_DURATION = 3_600;

	// After executing, will reference the Passenger object that was spawned.
	private Passenger mPassenger;
	private Building mBuilding;

	public SpawnPassengerEvent(long scheduledTime, Building building) {
		super(scheduledTime);
		mBuilding = building;
	}

	@Override
	public String toString() {
		return super.toString() + "Adding " + mPassenger + " to floor 1.";
	}

	@Override
	public void execute(Simulation sim) {
		Random r = mBuilding.getSimulation().getRandom();

		// 75% of all passengers are normal Visitors.
		if (r.nextInt(4) <= 2) {
			mPassenger = getVisitor();
		}
		else {
			mPassenger = getWorker();
		}
		mBuilding.getFloor(1).addWaitingPassenger(mPassenger);

		/*
		 TODO: schedule the new SpawnPassengerEvent with the simulation. Construct a new SpawnPassengerEvent
		 with a scheduled time that is X seconds in the future, where X is a uniform random integer from
		 1 to 30 inclusive.
		*/
		//[ATTEMPTED]
		int futureAppearance = r.nextInt(30) + 1;

		Simulation s = this.mBuilding.getSimulation();
		SpawnPassengerEvent ev = new SpawnPassengerEvent(s.currentTime() + futureAppearance, mBuilding);
		s.scheduleEvent(ev);

	}


	private Passenger getVisitor() {
		/*
		 TODO: construct a VisitorPassenger and return it.
		 The visitor should have a random destination floor that is not floor 1 (generate a random int from 2 to N).
		 The visitor's visit duration should follow a NORMAL (GAUSSIAN) DISTRIBUTION with a mean of 1 hour
		 and a standard deviation of 20 minutes.
		 */
		//[DONE]

		Random r = mBuilding.getSimulation().getRandom();
		int floorCount = this.mBuilding.getFloorCount();
		SPAWN_MEAN_DURATION = 3600;
		SPAWN_STDEV_DURATION = 1200;
		//subtract 1 from floor count to get range [0, floorcount - 2 ]
		//add 2 so range is [2, floorcount]
		int visitorDestination = r.nextInt(this.mBuilding.getFloorCount() - 1) + 2;

			double visitorDuration = (r.nextGaussian() * SPAWN_STDEV_DURATION) + SPAWN_MEAN_DURATION;

		VisitorPassenger visitor = new VisitorPassenger(visitorDestination, (int) Math.round(visitorDuration));

		// Look up the documentation for the .nextGaussian() method of the Random class.

		return visitor;
	}

	private Passenger getWorker() {
		/*
		TODO: construct and return a WorkerPassenger. A Worker requires a list of destinations and a list of durations.
		To generate the list of destinations, first generate a random number from 2 to 5 inclusive. Call this "X",
		how many floors the worker will visit before returning to floor 1.
		X times, generate an integer from 2 to N (number of floors) that is NOT THE SAME as the previously-generated floor.
		Add those X integers to a list.
		To generate the list of durations, generate X integers using a NORMAL DISTRIBUTION with a mean of 10 minutes
		and a standard deviation of 3 minutes.
		 */
		Random r = mBuilding.getSimulation().getRandom();

		//Random number from 2-5
		int floorsVisiting = r.nextInt(4) + 2;
		List<Integer> destinations = new ArrayList<>();
		List<Long> durations = new ArrayList<>();
		SPAWN_STDEV_DURATION = 180;
		SPAWN_MEAN_DURATION = 600;
		int tempDestination = r.nextInt(mBuilding.getFloorCount()-1) + 2;
		destinations.add(tempDestination);
		for (int i = 1; i < floorsVisiting; i++){
			//Random number 2-(number of floors)
			while(tempDestination == destinations.get(destinations.size()-1)) {
				tempDestination = r.nextInt(mBuilding.getFloorCount()-1) + 2;
			}
			destinations.add(tempDestination);
		}
		for (int i = 0; i < floorsVisiting; i++) {
			double tempDuration = (r.nextGaussian() * SPAWN_STDEV_DURATION) + SPAWN_MEAN_DURATION;
			durations.add((Long) Math.round(tempDuration));
		}

		WorkerPassenger worker = new WorkerPassenger(destinations, durations);
		//System.out.println(worker.getDestination());
		//System.out.println(worker.getDuration());

		// Look up the documentation for the .nextGaussian() method of the Random class.

		return worker;
	}
}
