/**
 * 
 */
package eds.examples.admission;

import eds.core.EDSimulator;
import eds.core.EDSimulatorEvt;
import eds.core.EDSimulatorListener;

/**
* Consider a medical analysis laboratory, open for 8 hours a day. 
* All users must first present their prescription to a medical secretary, they are then referred 
* to a nurse who will perform the examination such as a blood test.
* Users have an inter-arrival time of uniform distribution between 2 and 5 minutes.
* The queue in front of the secretary is FIFO-like, and the secretary service time follows 
* a normal distribution with a mean of 4 minutes and a standard deviation of 1 minute.
* The laboratory employs nb_nurse nurses who each have a room to accommodate a user and 
* carry out the examination.
* Any user already waiting is served, even if the laboratory closes.
* All times are expressed in seconds.
 * 
 * @author Pierre E. Chauvet
 * @see    Admission, Examination
 *
 */
public final class MedLab {

	// System parameters
	static int nb_nurse=2; // number of nurses in the laboratory
	
	// Final statistics
	static float nb_users_day_mean=0; // average number of users examined per day
	static float nb_users_closing_mean=0; // average number of users examined after closing
	static double[] percent_busy_employee_mean=new double[nb_nurse+1]; // average percentage of time that n nurses are busy, for n=0 to nb_nurse
	
	/**
	 * Main program
	 */
	public static void main(String[] args) {
		final Examination exam=new Examination(2); // two nurses in the laboratory
		final Admission admin=new Admission(exam);
		final EDSimulator simulator=new EDSimulator(0,admin.getDuration_opening());
		simulator.addSimUnit(exam);
		simulator.addSimUnit(admin);
		simulator.setNbRuns(5); // five runs
		simulator.addSimulatorListener(new EDSimulatorListener() {
			@Override
			public void simulationStepped(EDSimulatorEvt e) {
				int p=e.getPercent();
				if(p>100) p=100;
				System.out.println(e.getMessage()+": "+p+"%");
			}
			@Override
			public void simulationTerminated(EDSimulatorEvt e) {
				// Compute mean observations
				nb_users_day_mean=nb_users_day_mean/simulator.getRunsCount();
				nb_users_closing_mean=nb_users_closing_mean/simulator.getRunsCount();
				for(int i=0;i<=nb_nurse;i++) {
					percent_busy_employee_mean[i]=percent_busy_employee_mean[i]/simulator.getRunsCount();
				}	
				// Show result
				System.out.println(e.getMessage());
				System.out.println("Average number of users per day = "+nb_users_day_mean);
				System.out.println("Average number of users after closing = "+nb_users_closing_mean);
				for(int i=0;i<=nb_nurse;i++) {
					System.out.println("Average duration (in %) with "+i+" employee(s) busy = "+percent_busy_employee_mean[i]);
				}	
				System.out.println("Simulation duration = "+simulator.getDuration()+"s");
			}
			@Override
			public void simulationRunEnded(EDSimulatorEvt e) {
				System.out.println(e.getMessage());
				System.out.println("Run # "+e.getPercent());
				System.out.println("Number of users per day = "+admin.getNb_users_day()+" | "+exam.getNb_users_day());
				System.out.println("Number of users after closing = "+admin.getNb_users_closing());
				System.out.println("Working time (mn) after closing = "+(simulator.getLastEventTime()-admin.getDuration_opening())/60);
				exam.computePercentBusy();
				double[] percent_busy_employee=exam.getPercent_busy_nurse();
				for(int i=0;i<=nb_nurse;i++) {
					System.out.println("Duration (in %) with "+i+" employee(s) busy = "+percent_busy_employee[i]);
					percent_busy_employee_mean[i]+=percent_busy_employee[i];
				}	
				System.out.println();
				nb_users_day_mean+=admin.getNb_users_day();
				nb_users_closing_mean+=admin.getNb_users_closing();
			}	
		});
		simulator.start();
	}

}
