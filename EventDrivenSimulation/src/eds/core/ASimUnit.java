/**
 * 
 */
package eds.core;

/**
 * the ancestor abstract class for all classes called by the 
 * timer of a simulation (implemented in class EventSchedule). 
 * Provides the abstract method play(action) and init().
 *
 * @author  Pierre E. Chauvet
 * @version 1.0
 * @see     EventSchedule
 */
public abstract class ASimUnit {

	public String name=null; // the unit's name
    public byte priority=0;  // priority (in case of event time collision)
    protected EventSchedule schedule=null; // associated scheduler 
    protected EDSimulator simulator=null; // parent simulator
 
    // Associate the scheduler to this object
    public void linkToSchedule(EventSchedule schedule) {
    	this.schedule=schedule;
    }
 
    // Return the current time from the scheduler
    public double getTime() {
       return schedule.getTime();
    }
    
    // Add an event to the scheduler
    public void addEvent(double step,String action) {
    	schedule.addEvent(step,this, action);
    }
 
   // Interface of the response to an action method 
    abstract public boolean play(String action);
 
   // Interface of the initialization method
    abstract public boolean init(double beginTime,double endTime);

}
