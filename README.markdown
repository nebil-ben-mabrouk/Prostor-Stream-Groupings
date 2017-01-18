# Prostor Stream Groupings

Contact: Nebil BEN MABROUK (nebil.benmabrouk@gmail.com)

## Overview
Prostor-Stream-Grouping is a software module that defines custom [stream groupings](http://storm.apache.org/releases/current/Concepts.html) for Apache Storm. 

Before using Prostor-Stream-Grouping, it is recommended to be familiar with the concepts of [Apache Storm](http://storm.apache.org/).

A stream grouping allows data to be streamed from the instances of a source component to the instances of a target component (a component can be a spout or a bolt).

In particular, Prostor-Stream-Grouping defines:

1-  LocalGrouping, which enables the instances of a source component to communicate with the LOCAL instances of the target component ( i.e., target instances deployed on the same host as the source ones).

2-  LocationAwareGrouping, which allows a location-aware communication between the instances of a source component, with those instances of the target component SPECIFIED BY THE DEVELOPER (of Storm topologies). 


## Using LocationAwareGrouping 

### Configuring the address of Prostor-Scheduler (the location-aware scheduler)
LocationAwareGrouping needs to know the identifiers of component instances (that are generated at run-time when deploying the topology), in order to stream data to the appropriate target.
To get such identifiers, it communicates with the module [Prostor-Scheduler](https://github.com/nebil-ben-mabrouk/Prostor-Scheduler) (which is a location-aware scheduler for Apache Storm). Specifically, it communicates with a REST server embedded in this module. The address of the scheduling REST server is specified in the file '/src/main/resources/Grouping.properties'. To change it, developers should edit the property "schedule.resource.uri".

The Prostor-Stream-Groupings module is to be integrated within Storm topologies.

### Specifying location-aware data streams
In Storm topologies' configurations, developers have to add the streaming information for the components concerned with LocationAwareGrouping. 

The streaming information are defined as an array of streams, which is passed as a constructor argument of the LocationAwareGrouping class. 

A stream is specified in the form of "sourceHostname->targetHostname", where:
 - sourceHostname means the hostname on which the source component instance will be deployed, and
 - targetHostname means the hostname on which the target component instance will be deployed.


Below, is an example of a yaml configuration file of a Storm Flux topology with LocationAwareGrouping specification:

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
          className: "orange.labs.iot.computational.storage.storm.stream.groupings.LocationAwareGrouping"
          constructorArgs:
          - ["hostname1->hostname3","hostname2->hostname4"]
        
  config:
    topology.workers: 2
    Source: "hostname1_hostname2"
    ProcessingComponent: "hostname3_hostname4"
```

In the above example, LocationAwareGrouping creates two streams:

- FROM the Source instance depoloyed on hostname1 TO the ProcessingComponent instance depoloyed on hostname3.
- FROM the Source instance depoloyed on hostname2 TO the ProcessingComponent instance depoloyed on hostname4.


