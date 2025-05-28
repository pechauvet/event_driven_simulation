/**
 * 
 */
package eds.examples.simpleoffice;

import java.util.Random;

import eds.core.ASimUnit;
import eds.core.EDSimulator;
import eds.core.EDSimulatorEvt;
import eds.core.EDSimulatorListener;

/**
* Consider a queue in front of an office, open for 8 hours a day.
* The queue is FIFO-like, and the service time at the office follows a normal distribution
* with a mean of 4 minutes and a standard deviation of 1 minute. Users have an inter-arrival 
* time of uniform distribution between 2 and 5 minutes. Every user in the queue is served, 
* even after closing. All times are expressed in seconds.
 * 
 * @author Pierre E. Chauvet
 * @see    ASimUnit
 *
 */
public final class SimpleOffice extends ASimUnit {

	private transient Random random=new Random();
	private transient boolean closing;
	// State variables
	private boolean office; // office status (true=free, false=busy)
	private int queue; // number of users in the queue
	// Observation variables
	private int nb_users_day; // number of users served per day
	private int nb_users_closing; // number of users served after closing
	// Parameter values of this system
	private float duration_opening; // opening hours (1 working day)
	private float service_mean; // average length of service
	private float service_std; // standard deviation of length of service
	private float arrival_min; // minimum inter-arrival time
	private float arrival_max; // maximum inter-arrival time
	// Final statistics
	private float nb_users_day_mean=0; // average number of users served per day
	private float nb_users_closing_mean=0; // average number of users served after closing

	/**
	 * 
	 */
	public SimpleOffice() {
		// Default parameter values (in seconds)
		duration_opening=8*60*60;
		service_mean=240;
		service_std=60;
		arrival_min=120;
		arrival_max=300;
	}

	/* (non-Javadoc)
	 * @see eds.core.ASimUnit#init(float, float)
	 */
	@Override
	public boolean init(double beginTime, double endTime) {
		  // Initialization of variables
		  closing=false;
		  office=true;
		  queue=0;
		  nb_users_day=0;
		  nb_users_closing=0;
		  // Initializing the event stack (the scheduler)
		  addEvent(duration_opening,"Closing");
		  addEvent(arrival_min+(arrival_max-arrival_min)*random.nextFloat(),"UserEntrance");
		  return true;
	}

	/* (non-Javadoc)
	 * @see eds.core.ASimUnit#play(java.lang.String)
	 */
	@Override
	public boolean play(String action) {
		if(action.equals("UserEntrance")) {
			if(queue>0) {queue++;}
			else {
				if(office==false) {queue=1;}
				else {
					office=false;
					nb_users_day++;
					if(closing) {nb_users_closing++;}
					addEvent(service_std*(float)random.nextGaussian()+service_mean,"EndService");
				}
			}
			if(!closing) {addEvent(arrival_min+(arrival_max-arrival_min)*random.nextFloat(),"UserEntrance");}
		}
		else if (action.equals("EndService")) {
			if(queue>0) {
				queue--;
				office=false;
				nb_users_day++;
				if(closing) {nb_users_closing++;}
				addEvent(service_std*(float)random.nextGaussian()+service_mean,"EndService");
			}
			else office=true;
		}
		else if(action.equals("Closing")) {closing=true;}
		return true;
	}

	/**
	 * @return the duration_opening
	 */
	public final float getDuration_opening() {
		return duration_opening;
	}

	/**
	 * @param duration_opening the duration_opening to set
	 */
	public final void setDuration_opening(float duration_opening) {
		this.duration_opening = duration_opening;
	}

	/**
	 * @return the service_mean
	 */
	public final float getService_mean() {
		return service_mean;
	}

	/**
	 * @param service_mean the service_mean to set
	 */
	public final void setService_mean(float service_mean) {
		this.service_mean = service_mean;
	}

	/**
	 * @return the service_std
	 */
	public final float getService_std() {
		return service_std;
	}

	/**
	 * @param service_std the service_std to set
	 */
	public final void setService_std(float service_std) {
		this.service_std = service_std;
	}

	/**
	 * @return the arrival_min
	 */
	public final float getArrival_min() {
		return arrival_min;
	}

	/**
	 * @param arrival_min the arrival_min to set
	 */
	public final void setArrival_min(float arrival_min) {
		this.arrival_min = arrival_min;
	}

	/**
	 * @return the arrival_max
	 */
	public final float getArrival_max() {
		return arrival_max;
	}

	/**
	 * @param arrival_max the arrival_max to set
	 */
	public final void setArrival_max(float arrival_max) {
		this.arrival_max = arrival_max;
	}

	/**
	 * @return the office
	 */
	public final boolean isOffice() {
		return office;
	}

	/**
	 * @return the queue
	 */
	public final int getQueue() {
		return queue;
	}

	/**
	 * @return the nb_users_day
	 */
	public final int getNb_users_day() {
		return nb_users_day;
	}

	/**
	 * @return the nb_users_closing
	 */
	public final int getNb_users_closing() {
		return nb_users_closing;
	}

	/**
	 * @return the nb_users_day_mean
	 */
	public final float getNb_users_day_mean() {
		return nb_users_day_mean;
	}

	/**
	 * @return the nb_users_closing_mean
	 */
	public final float getNb_users_closing_mean() {
		return nb_users_closing_mean;
	}

	/**
	 * @param nb_users_day_mean the nb_users_day_mean to set
	 */
	public final void setNb_users_day_mean(float nb_users_day_mean) {
		this.nb_users_day_mean = nb_users_day_mean;
	}

	/**
	 * @param nb_users_closing_mean the nb_users_closing_mean to set
	 */
	public final void setNb_users_closing_mean(float nb_users_closing_mean) {
		this.nb_users_closing_mean = nb_users_closing_mean;
	}

	/**
	 * The main program
	 * @param args
	 */
	public static void main(String[] args) {
		final SimpleOffice model=new SimpleOffice();
		final EDSimulator simulator=new EDSimulator(0,model.getDuration_opening());
		simulator.addSimUnit(model);
		simulator.setNbRuns(2);
		simulator.addSimulatorListener(new EDSimulatorListener() {
			@Override
			public void simulationStepped(EDSimulatorEvt e) {
				int p=e.getPercent();
				if(p>100) p=100;
				System.out.println(e.getMessage()+": "+p+"%");
			}
			@Override
			public void simulationTerminated(EDSimulatorEvt e) {
				model.nb_users_day_mean=model.nb_users_day_mean/simulator.getRunsCount();
				model.nb_users_closing_mean=model.nb_users_closing_mean/simulator.getRunsCount();
				System.out.println(e.getMessage());
				System.out.println("Average number of users per day = "+model.nb_users_day_mean);
				System.out.println("Average number of users after closing = "+model.nb_users_closing_mean);
				System.out.println("Simulation duration = "+simulator.getDuration()+"s");
			}
			@Override
			public void simulationRunEnded(EDSimulatorEvt e) {
				System.out.println(e.getMessage());
				System.out.println("Run # "+e.getPercent());
				System.out.println("Number of users per day = "+model.nb_users_day);
				System.out.println("Number of users after closing = "+model.nb_users_closing);
				model.nb_users_day_mean+=model.nb_users_day;
				model.nb_users_closing_mean+=model.nb_users_closing;
			}	
		});
		model.setNb_users_day_mean(0);
		model.setNb_users_closing_mean(0);
		simulator.start();
	}

}
