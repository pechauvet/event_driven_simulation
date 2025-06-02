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

## Example 1 : office with one employee
We consider a queue in front of an office with only one employee to provide the service.
The office is open 8 hours a day.
The queue is FIFO-like, and the service time at the office follows a normal distribution with a mean of 4 minutes and a standard deviation of 1 minute. 
Users have an inter-arrival time of uniform distribution between 2 and 5 minutes. 
Every user in the queue is served, even after closing. All times are expressed in seconds.
![Office_light](https://github.com/user-attachments/assets/9008f850-9fe4-4019-adbf-69ed4cf8da88)

This system is described in the class **SimpleOffice** (package eds.examples.simpleoffice, file **SimpleOffice.java**).
SimpleOffice also contains the main function, from where the simulation is built and launched. 

## Example 2 : office with several employees
We consider a queue in front of an office with *nres* employees to provide the service, where *nres*>0. Each employee (called a resource in this kind of simulation) can serve one user. Employees therefore work in parallel.

Like in example 1, the office is open 8 hours a day. The queue is FIFO-like, and the service time at the office follows a normal distribution with a mean of 6 minutes and a standard deviation of 1 minute. Users have an inter-arrival time of uniform distribution between 2 and 4 minutes. Every user in the queue is served, even after closing. All times are expressed in seconds.
![Office2_light](https://github.com/user-attachments/assets/c57f5c87-2c0d-43b9-99a3-95c78ddef935)

This system is described in the class **SimpleOffice2** (package eds.examples.simpleoffice2, file **SimpleOffice2.java**).
SimpleOffice2 also contains the main function, from where the simulation is built and launched.

## Example 3 : a medical analysis laboratory
In this example, the simulation is composed of two ASimUnit units (one for admission, the other for the examination) and the main program.

Consider a medical analysis laboratory, open for 8 hours a day. 
All users must first present their prescription to a medical secretary, they are then referred to a nurse who will perform the examination such as a blood test. Users have an inter-arrival time of uniform distribution between 2 and 5 minutes. The queue in front of the secretary is FIFO-like, and the secretary service time follows a normal distribution with a mean of 4 minutes and a standard deviation of 1 minute.
The laboratory employs nb_nurse nurses who each have a room to accommodate a user and carry out the examination. 
Any user already waiting is served, even if the laboratory closes. All times are expressed in seconds.
![MedLab_light](https://github.com/user-attachments/assets/61601a0a-352e-4039-b958-c49ab04701a3)

This system is described with three classes :
- **Admission** (package eds.examples.admission, file **Admission.java**) : the admission (one medical secretary) part of the system.
- **Examination** (package eds.examples.admission, file **Examination.java**) : the examination (several nurses/technicians) part of the system.
- **Main** (package eds.examples.admission, file **Main.java**) : the main function, from where the simulation is built and launched.
