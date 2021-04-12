# cumulocity-microservice-templates

## Summary

Collection of templates / examples to give the developer a starting point how common features can be implemented in a Microservice by using Cumulocity's [Microservice SDK for Java](https://cumulocity.com/guides/microservice-sdk/java/).

## Templates & Examples

- [basic](basic) 
    - how to communicate with Cumulocity's REST API
    - how to implement REST endpoints, e.g. GET or POST
    - how to implement a scheduler
    - how to work with different contexts

- [agent](agent)
    - how to create an agent representation for your Microservice
    - how to subscribe and receive configuration updates for your Microservice
    - how to write events
    - how to create measurement for microservice diagnostic (total, used and free memory)

- [realtime](realtime)
    - how to subscribe and listen for realtime notifications
    - how to get notified once a managedObject is being updated
    - how to get notified once a new measurement has been created for a device
    - how to receive operations and update their status

- [secret](secret)
    - how to use tenant options
    - how to encrypt data and store it as a tenant option

- [ehcache](ehcache)
    - how to use ehcache to implement a caching mechanism for External Ids and managedObjects

- [multischeduler](multischeduler)
    - how to start a separate scheduler thread for each tenant to run tasks in parallel.
    
- [metrics](metrics)
    - how to use the Micrometer Framework with custom metrics to enable fine grainular monitoring of a microservice and its custom business logic.

## Disclaimer

These tools are provided as-is and without warranty or support. They do not constitute part of the Software AG product suite. Users are free to use, fork and modify them, subject to the license agreement. While Software AG welcomes contributions, we cannot guarantee to include every contribution in the master project.

## Contact

For more information you can Ask a Question in the [TECHcommunity Forums](http://tech.forums.softwareag.com/techjforum/forums/list.page?product=cumulocity).

You can find additional information in the [Software AG TECHcommunity](http://techcommunity.softwareag.com/home/-/product/name/cumulocity).

_________________
Contact us at [TECHcommunity](mailto:technologycommunity@softwareag.com?subject=Github/SoftwareAG) if you have any questions.
