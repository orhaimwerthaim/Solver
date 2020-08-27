# Solver
A java project for the ROS-POMDP solver.
 



## Dependencies
* java sdk 14.
* jar dependencies are located in the project /lib directory.

## Runing
download and build the repository.
startup class is `src/Solver.java`.

Program Arguments (see the [ROS-POMDP](https://github.com/orhaimwerthaim/Solver/blob/master/ROS-POMDP.pdf) article):
* `use_ros`- posiible input is `false` or (defalut) `true`. When `false` the `Solver` will run commands on an internal simulation. When `true` commands are sent to ROS using `localhost` port 1770 (IP and port can be changed on class POMDP_Solver.SendToROS.java). 
* `rddl_dir`- contains the absolut to the directory in which the rddl domain file and instance file are located. When this argument is not sent the `Solver` will be in Java simulator mode.
* `rddl_instance`- is used to specify the rddl inastance name (default name is `created_plps_instance`).
* `rddl_initial_state_dir`- When the initail state is probabilistic, this argument should contain the directory of the rddl domain and instance to generate the initial state.  
* `rddl_initial_state_instance`- can be used to change the default instance name for generating the probabilistic initial state (default is `init_instance`).
* `illegal_action_penalty`- default is -400.
* `min_num_of_particles`- Minimum number of states in the particles filter (larger particle filters are more accurate but cost more computationally), default is 40.
*`ucb_c`- changes the exploration exploitation behaviour of POMCP, default is 100.
* `planning_time`- The number of milliseconds for planning per action, default is 1000.

Possible running configuration (arguments):
* Using RDDL with probabilistic initial state in an internal simulation- `rddl_dir=<path_to_rddls_dir> rddl_instance=created_plps_instance rddl_initial_state_instance=init_instance use_ros=false rddl_initial_state_dir=<path_to_initial_state_rddls_dir>`
* Using RDDL with a deterministic initial state and an internal simulation- `rddl_dir=<path_to_rddls_dir> rddl_instance=created_plps_instance use_ros=false `
*Using java simulator with an internal simulation- `use_ros=false`
*Using java simulator with ROS- no arguments are needed.

An example for a command line activation (after generating jar file from the project):
`java -cp ~/IdeaProjects/Solver_local/out/artifacts/Solver_local_jar -jar Solver.jar "rddl_dir=/home/lab/IdeaProjects/PLP2RDDL_JAVA_out/production/PLP2RDDL_JAVA/Created_RDDLs" "rddl_instance=created_plps_instance" "rddl_initial_state_instance=init_instance" "use_ros=false" "rddl_initial_state_dir=/home/lab/IdeaProjects/PLP2RDDL_JAVA_out/production/PLP2RDDL_JAVA/Created_RDDLs/InitStateRDDLs"`

