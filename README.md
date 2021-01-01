# eMobility
A system that models ChargingStations belonging to a particular store and Tenant.
The ChargingStation entity implement two methods of note: 
* isChargingStationOpenDuring :- Accepts a timestamp and returns a boolen indicating whether a the Charging station is currently open. 
* nextOpenClosedStatusChange :- Accepts a timestamp and returns another timestamp which indicate when the next station open/close 
status will change.
