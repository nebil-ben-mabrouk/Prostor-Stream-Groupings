# Prostor-Stream-Grouping

## Overview
Prostor-Stream-Grouping is a software module that defines custom [stream groupings](http://storm.apache.org/releases/current/Concepts.html) for Apache Storm. 

Before using Prostor-Scheduler, it is recommended to be familiar with the concepts of [Apache Storm](http://storm.apache.org/).

A stream grouping allows data to be transmitted from the instances of a source component to the instances of a target component (a component can be a spout or a bolt).

In particular, Prostor-Stream-Grouping defines:

1-  LocalGrouping, which allows the instance of the source component to communicate with the LOCAL instances of the target component( i.e., target instances deployed on the same host as the source ones).

2-  LocationAwareGrouping, which allows the instances of the source component to communicate with those instances of the target component SPECIFIED BY THE DEVELOPER of Storm topologies.

LocationAwareGrouping needs to know the identifiers of component instances (that are generated at run-time when deploying the topology), in order to stream data to the appropriate target.
To get such identifiers, it communicates with the module [Prostor-Scheduler](https://github.com/nebil-ben-mabrouk/Prostor-Scheduler). Specifically, it communicates with a REST server embedded in this module.
The address of the scheduling REST server is specified in the file '/src/main/resources/Grouping.properties'. To change it, developers should change the property "schedule.resource.uri".

The Prostor-Stream-Grouping module is to be integrated within Storm topologies.

## Using LocationAwareGrouping 
In Storm topologies' configurations, developers have to add the streaming information for the components concerned with LocationAwareGrouping. 

These information are specified (as constructor arguments) in the form "hostnameOfTheSourceComponentInstance->hostnameOfTheSourceComponentInstance" (e.g., "hostname1->hostname2").

Below, is an example of a yaml configuration file for a Storm Flux topology with LocationAwareGrouping specification:

```
  name: "myTopology"

  spouts:
    - id: "Source"
      className: "myPackage.mySpout"
      parallelism: 2

  bolts:
    - id: "ProcessingComponent"
      className: "myPackage.myComponent"
      parallelism: 2
    
  streams:
    - name: "Source --> ProcessingComponent"
      from: "Source"
      to: "ProcessingComponent"
      grouping: 
        streamId: "comparisonStream"
        type: CUSTOM
        customClass:
          className: "orange.labs.srol.fog.iot.dataflow.groupings.LocationAwareGrouping"
          constructorArgs:
          - ["hostname1->hostname3","hostname2->hostname4"]
        
  config:
    topology.workers: 2
    Source: "hostname1_hostname2"
    ProcessingComponent: "hostname3_hostname4"
```

In the above example, LocationAwareGrouping creates two streams:
1- FROM the Source instance depoloyed on hostname1 TO the ProcessingComponent instance depoloyed on hostname3.
2- FROM the Source instance depoloyed on hostname2 TO the ProcessingComponent instance depoloyed on hostname4.


