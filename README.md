# event_driven_simulation
A simple event driven simulation framework coded in Java (using the Eclipse IDE), based on the schedule method.

## Objective
The proposed framework is used in a simulation course to illustrate how the schedule method works. 
It also serves to show that the discrete event approach allows for the mixing of discrete event systems and dynamic (continuous-time) systems. This is particularly useful for simulating a system composed of subsystems with different time scales and models of different nature (differential equations, discrete systems, etc.).
Therefore, in addition to the basic framework, I added two classes to represent and numerically solve partial differential equations of the convection-diffusion type, in 2 dimensions.

The proposed framework, which is very simple, does not replace existing professional tools (such as, for example, Arena Simulation Software).
It was made so that students could code some basic examples on their own, such as the one shown below, while developing their Java skills.

## Framework overview
The EDSimulator class, which implements the Runnable interface, must be instantiated to build a simulation. 
Any instance of EDSimulator must be provided with:
- the initial and final times of the simulation,
- the number of runs to execute,
- the simulation units to simulate (instances of ASimUnit),
- eventual listeners, to trace and show results. 

A simulation unit is a subsystem to be simulated: it must implement the methods init() and play() inherited from the abstract class ASimUnit.
It is in the play() method of simulation units that the state transition logic and event response must be coded. 
Here the class diagram of the framework :
![diagram_eds](https://github.com/user-attachments/assets/7a408101-2580-4e1b-8984-a604dbc28364)

## Example : the office
We consider a queue in front of an office with only one employee to provide the service.
The office is open 8 hours a day.
The queue is FIFO-like, and the service time at the office follows a normal distribution with a mean of 4 minutes and a standard deviation of 1 minute. 
Users have an inter-arrival time of uniform distribution between 2 and 5 minutes. 
Every user in the queue is served, even after closing. All times are expressed in seconds.

This system is described in the class **SimpleOffice** (package eds.examples.simpleoffice, file **SimpleOffice.java**).
SimpleOffice also contains the main function, from where the simulation is built and launched. 
