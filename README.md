# Tick-borne-pathogen-spillover-risk-evaluation-system-TAPES-

TAPES is an integrated modeling framework designed to evaluate and predict the spillover risks of tick-borne pathogens in human populations. This system combines ecological and social factors through a dual-submodel approach, incorporating both environmental hazard assessment and human exposure patterns. By simulating tick population dynamics, pathogen transmission cycles, and human behavioral patterns, TAPES provides a comprehensive tool for assessing and managing tick-borne disease risks under various environmental and socioeconomic scenarios.

Principles 
•	Submodel 1: A cell-based “hazard” model to simulate the density of infectious ticks. 
o	Tick population ecology model– to include the influence of temperature.
o	Susceptible - Infectious model for borrelia transmission.
o	Random walk model for generalised types of host animals.
•	Submodel 2: An agent-based “exposure” model to simulate the pattern of human risk activities. 

Model dimensions
•	Spatial – Cell-based, cell size can vary (1 ha to 1 km2).  
•	Temporal – Weekly.

Application on Hungary
•	Option 1: Map the density of infectious ticks for the whole Hungary at 1 km2 level using the “hazard” model only – Model calibration or validation on the two case cities.
•	Option 2: Map the local contact possibilities between ticks and human at 1 ha via integrating RUG outcome (household distribution) and Aporia data (if forest management is targeted). 
•	To test the effects of future climate and socio-economic conditions (scenarios) on Lyme disease risks. 

Data requirements 
•	Option 1:
o	Forest/vegetation cover map(s) for Hungary would be preferable, but could use CORINE if not available <<same as RUG data requirements>>
o	Tick data (i.e. seasonal tick abundance in a few places, if possible, with their infection prevalence rate) - the data presented in the Egyed2012 article “Seasonal activity and tick-borne pathogen infection rates of Ixodes ricinus ticks in Hungary” would be prefect.
o	Weekly temperature and relative humidity data that cover the places and the periods of tick sampling.
o	Deer distribution and density
o	Rodent distribution – good to have it; if not possible, then can estimate based on publications.
•	Additional data for option 2:
o	Statistics on time use (especially outdoor recreations), participation rate of forest recreation, and forest attractiveness.
