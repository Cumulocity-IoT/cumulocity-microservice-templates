# cumulocity-microservice-templates

## Summary

Collection of templates / examples to give the developer a starting point how common features can be implemented in a Microservice by using Cumulocity's [Microservice SDK for Java](https://cumulocity.com/docs/microservice-sdk/java/).

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

- [interprocess communication - IPC](ipc)
    - how to implement a interprocess communication between microservices based on REST. The solution is using a declarative REST client (Feign Spring Cloud) to call another microservice.
- [context](context)
    - how to use user & tenant context in microservices.

# Useful links 

📘 Explore the Knowledge Base   
Dive into a wealth of Cumulocity IoT tutorials and articles in our [Tech Community](https://techcommunity.cumulocity.com).  

💡 Get Expert Answers    
Stuck or just curious? Ask the Cumulocity IoT experts directly on our [Forum](https://techcommunity.cumulocity.com/c/forum/5).   

🚀 Try Cumulocity IoT    
See Cumulocity IoT in action with a [Free Trial](https://www.cumulocity.com/start-your-journey/free-trial).   

✍️ Share Your Feedback    
Your input drives our innovation. If you find a bug, please create an issue in the repository. If you'd like to share your ideas or feedback, please post them [here](https://techcommunity.cumulocity.com/c/feedback-ideas/14). 

   
# Authors 

[Alexander Pester](mailto:alexander.pester@cumulocity.com)

# Disclaimer

These tools are provided as-is and without warranty or support. They do not constitute part of the Cumulocity product suite. Users are free to use, fork and modify them, subject to the license agreement. While Cumulocity welcomes contributions, we cannot guarantee to include every contribution in the master project.
