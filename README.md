# OCCI CQoS Proxy for OPTIMIS

OCCICQoSProxy
1.0.0-SNAPSHOT
20.02.2013

## License and Copyright

Copyright [2013] [Oriol Collell Martin]

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

## Introduction

The OCCI CQoS Proxy component is part of the OPTIMIS Europeran research project (http://www.optimis-project.eu/) 
and it used by some of its tools to manage Service-Level Agreements (SLAs) with OCCI (http://occi-wg.org/) compliant 
Infrastructure Providers (IPs). 
An agreement includes the Service Manifest as its main part of the contract. 
The Service Manifest describes the requirements that the Service Provider requires for the deployment 
of the service. It also describes the cost, trust, eco efficiency and risk factors that the IP will 
comply to. More information about OPTIMIS and the service Manifest can be fouund in its project website.

The CQoS Proxy component was implemented by using the WSAG4J framework [1]. The WS-Agreement for 
Java framework is a tool to create and manage service level agreements (SLAs) in distributed systems. 
It is an implementation of the OGF WS-Agreement standard [2]. WSAG4J helps in designing and 
implementing SLAs for a service and automates typical SLA management tasks like SLA offer validation,
service level monitoring, persistence, accounting, and more.


[1]	wsag4j framework, Oliver Waeldrich, http://packcs-e0.scai.fraunhofer.de/wsag4j/index.html
[2]	WS-Agreement specification, OGF, http://www.ogf.org/documents/GFD.107.pdf

## Functions

This component implements the following functionality:
* Get Template: Retrieves an agreement template provided by an IP.
* Service Deployment Negotiation: Negotiation between an SP and an IP over the deployment of the service onto the IP's infrastructure.
* Create Agreement: Creates an SLA which initiates the service deployment.
* Terminate Agreement: Terminates an agreement which undeploy a service.

## Using the Software
**IMPORTANT NOTE**: This software can only be used in conjunction with a full OPTIMIS deployment, it cannot be executed as a standalone application.

### Software Dependencies

This component has been written in Java, therefore it needs a JVM to run. We have used Maven to manage the dependencies and build process of the project,
therefore, you will need to have Maven installed too.

The OCCI CQoS Proxy component expects an admission control gateway and a cloud optimizer gateway installed. In addition, it needs to include the 
the Service Manifest API and the Monitoring Manager:

* Admission Control - Version: 1.0.0-SNAPSHOT	
* Cloud Optimizer - Version:	1.0.0-SNAPSHOT	
* Service Manifest API - Version: 1.0.6
* Monitoring Manager - Version: 0.0.1-scai-8638

Dependencies with other libraries external to OPTIMIS are described in the POM


### Installation and Execution Instructions

This component can be installed by running the "mvn clean install" command on the project root. This generates a ".war" archive inside the
"target" directory that can be deployed on a compliant Web Server, such as Tomcat.
Once the app has been deployed and Tomcat has been started, Opening the URL http://127.0.0.1:8080/optimis-sla in a Web-Browser 
should display the Welcome Page of the WSAG4J server application.

## Contributors
Oriol Collell Martin

## Contact Information and Website

http://www.optimis-project.eu/

We welcome your feedback, suggestions and contributions. Contact us
via email if you have questions, feedback, code submissions, 
and bug reports.

For general inquiries, see http://www.optimis-project.eu/contact

You can submit bug, patches, software contributions, and feature 
requests using Bugzilla.  
Access Bugzilla at: 
http://itforgebugzilla.atosresearch.eu/bugzilla/enter_bug.cgi?product=Optimis 
