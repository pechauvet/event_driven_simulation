/**
 * 
 */
package eds.core;

/**
 * A final class containing informations on an event:
 * the time of event, the kind of event (action) and 
 * the simulation unit (derived from class ASimUnit) 
 * receiving the event.
 *
 * @author Pierre E. Chauvet
 * @version 1.0
 * @see     EventSchedule , ASimUnit
 */
public final class Event {

    protected double time=0; // the trigger time of the event
    protected String action=""; // action to be taken
    protected ASimUnit unit=null; // simulation unit to trigger
 
    public Event(double time,ASimUnit unit, String action) {
    	this.time=time;
    	this.unit=unit;
    	this.action=action;
    }
 
    public boolean preceding(Event evt) {
       return (time<evt.time);
    }

	/**
	 * @return the time
	 */
	public final double getTime() {
		return time;
	}

	/**
	 * @return the action
	 */
	public final String getAction() {
		return action;
	}

	/**
	 * @return the unit
	 */
	public final ASimUnit getUnit() {
		return unit;
	}
 

}
