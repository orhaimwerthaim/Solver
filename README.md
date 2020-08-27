# Solver
A java project for the ROS-POMDP solver.
 



## Dependencies
* java sdk 14.
* jar dependencies are located in the project /lib directory.

## Runing
download and build the repository.
startup class is `src/Solver.java`.

Program Arguments:
* `use_ros`- posiible input is `false` or (defalut) `true`. When `false` the `Solver` will run commands on an internal simulation. When `true` commands are sent to ROS using `localhost` port 1770 (IP and port can be changed on class POMDP_Solver.SendToROS.java). 
* `rddl_dir`- contains the absolut to the directory in which the rddl domain file and instance file are located. When this argument is not sent the `Solver` will be in Java simulator mode (see the [ROS-POMDP](https://github.com/orhaimwerthaim/Solver/blob/master/ROS-POMDP.pdf) article) 



java -cp ~/IdeaProjects/Solver_local/out/artifacts/Solver_local_jar -jar Solver_local.jar "rddl_dir=/home/or/IdeaProjects/PLP2RDDL_JAVA_out/production/PLP2RDDL_JAVA/Created_RDDLs" "rddl_instance=created_plps_instance" "rddl_initial_state_instance=init_instance" "use_ros=false" "rddl_initial_state_dir=/home/or/IdeaProjects/PLP2RDDL_JAVA_out/production/PLP2RDDL_JAVA/Created_RDDLs/InitStateRDDLs"

