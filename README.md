# SmartThings
https://github.com/codersaur/SmartThings

Copyright (c) 2016 [David Lomas](https://github.com/codersaur)

##Overview

This repository contains device handlers and SmartApps for use with Samsung's [SmartThings](http://www.smartthings.com) home automation platform.

### SmartApps

* [Evohome (Connect) - BETA](https://github.com/codersaur/SmartThings/tree/master/smartapps/Evohome):
 - This SmartApp connects your Honeywell Evohome System to SmartThings.
 - You must install the Evohome Heating Zone device handler (below).
 - It uses the EMEA Evohome API (therefore may not work for US Evohome users).


* [InfluxDB Logger](https://github.com/codersaur/SmartThings/tree/master/smartapps/InfluxDB%20Logger):
 - This SmartApp logs device states to an [InfluxDB](https://influxdata.com/) database.

#### SmartApp Installation Procedure

1. Within the SmartThings IDE, click '*My SmartApps*', then '*+ New SmartApp*'. 
2. Select the '*From Code*' tab and paste in the contents of the relevant groovy file.
3. Click '*Create*', and then '*Publish*' *(For Me)*.
4. Now using the SmartThings smartphone app, navigate to the '*Marketplace*', select '*SmartApps*', then browse to '*My Apps*' at the bottom of the list. You should see the new SmartApp available for installation.
5. Select the new SmartApp, complete the configuraiton options and press '*Done*'.

  
### Device Handlers

* [Aeon Home Energy Meter (GEN2 - UK - 1 Clamp)](https://github.com/codersaur/SmartThings/tree/master/devices/Aeon%20Home%20Energy%20Meter%20(GEN2%20-%20UK%20-%201%20Clamp)):
 - This device handler is written specifically for the Aeon Home Energy Meter Gen2 UK version, with a single clamp.
 - It supports live reporting of energy, power, current, and voltage, as well as energy and cost statistics over multiple pre-defined periods.

* [Evohome Heating Zone - BETA](https://github.com/codersaur/SmartThings/tree/master/devices/Evohome):
 - This device handler is required for the Evohome (Connect) SmartApp.

* [Philio Dual Relay (PAN04)](https://github.com/codersaur/SmartThings/tree/master/devices/Philio%20Dual%20Relay%20(PAN04)):
 - This device handler is written specifically for the Philio Dual Relay (PAN04), when used as a single switch/relay only.
 - It supports live reporting of energy, power, current, voltage, and power factor,  as well as energy and cost statistics over multiple pre-defined periods.
 
* [TKB Metering Switch (TZ88E-GEN5)](https://github.com/codersaur/SmartThings/tree/master/devices/TKB%20Metering%20Switch%20(TZ88E-GEN5)):
 - This device handler is written specifically for the TKB Metering Switch (TZ88E-GEN5).
 - It supports live reporting of energy, power, current, voltage, and power factor,  as well as energy and cost statistics over multiple pre-defined periods.
 
####Device Handler Installation Procedure

1. Within the SmartThings IDE, click '*My Device Handlers*', then '*+ Create New Device Handler*'. 
2. Select the '*From Code*' tab and paste in the contents of the relevant groovy file.
3. Click '*Create*', then '*Publish*' *(For Me)*.
4. If your device is already connected to your SmartTHings hub, you will need to change the device type using the SmartThings IDE. From the '*My Devices*' tab, click the relevent device, then '*Edit*'. Change the '*Type*' using the drop-down box, custom devices will be near the bottom of the list. 

  
##License


Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
in compliance with the License. You may obtain a copy of the License at:

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
for the specific language governing permissions and limitations under the License.
