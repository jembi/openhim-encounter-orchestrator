[![Build Status](https://travis-ci.org/jembi/openhim-encounter-orchestrator.png?branch=develop)](https://travis-ci.org/jembi/openhim-encounter-orchestrator)

OpenHIM Encounter Orchestrator
==============================

This mediator was developed for the Rwandan Health Information Exchange as part of the RHEA project.

An orchestrator service for the RHIE OpenHIM implementation to orchestrate encounter transactions and supports the following workflows:

Save Encounter
--------------
The mediator accepts HL7v2 ORU_R01 encounters in XML format and orchestrates requests as follows:
* Validates the patient and enriches the request with their enterprise identifier using either PIX or custom OpenEMPI XML
* Validates the healthcare provider and enriches the request with their enterprise identifier using either CSD or LDAP
* Validates the facility and enriches the request with the enterprise facility code using either CSD or custom ResourceMap requests
* Validates the terminology codes in the request using custom HTTP requests
* Forwards the enriched request onto a Shared Health Record system using either XDS.b Provide and Register or OpenMRS HTTP

Get and Query Encounters
------------------------
The mediator supports the retrieval of patient encounters using either XDS.b Registry Stored Query and Retrieve Document transactions or HTTP requests (OpenMRS).
