/**
 * 
 */
package eds.examples.admission;

import java.util.Random;

import eds.core.ASimUnit;

/**
 * The admission part (by one medical secretary) of the medical analysis laboratory.
 * 
 * @author Pierre E. Chauvet
 * @see    ASimUnit
 */
public final class Admission extends ASimUnit {

	private transient Random random=new Random();
	private transient boolean closing;
	// The examination office model
	private ASimUnit examination;
	// State variables
	private boolean status; // admission status (true=free, false=busy)
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
	private float delay_to_exam; // time delay between admission and examination

	/**
	 * 
	 */
	public Admission(ASimUnit examination) {
		this.examination=examination;
		// Default parameter values (in seconds)
		duration_opening=8*60*60;
		service_mean=4*60;
		service_std=60;
		arrival_min=2*60;
		arrival_max=5*60;
		delay_to_exam=60;
	}

	@Override
	public boolean init(double beginTime, double endTime) {
		  // Initialization of variables
		  closing=false;
		  status=true;
		  queue=0;
		  nb_users_day=0;
		  nb_users_closing=0;
		  // Initializing the event stack (the scheduler)
		  addEvent(duration_opening,"Closing");
		  addEvent(arrival_min+(arrival_max-arrival_min)*random.nextFloat(),"UserEntrance");
		  return true;
	}

	@Override
	public boolean play(String action) {
		// Response to events
		if(action.equals("UserEntrance")) {
			// Increment the number of users
			nb_users_day++;
			// Manage queue and admission status
			if(queue>0) {queue++;}
			else {
				if(status==false) {queue=1;}
				else {
					status=false;
					if(closing) {nb_users_closing++;}
					addEvent(service_std*(float)random.nextGaussian()+service_mean,"EndService");
				}
			}
			// Add next user entrance if office is open
			if(!closing) {addEvent(arrival_min+(arrival_max-arrival_min)*random.nextFloat(),"UserEntrance");}
		}
		else if (action.equals("EndService")) {
			addEvent(delay_to_exam,examination,"UserEntrance");
			if(queue>0) {
				queue--;
				status=false;
				if(closing) {nb_users_closing++;}
				addEvent(service_std*(float)random.nextGaussian()+service_mean,"EndService");
			}
			else status=true;
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

}
