# SmartThings
https://github.com/codersaur/SmartThings

Copyright (c) 2017 [David Lomas](https://github.com/codersaur)

## Overview

This repository contains device handlers and SmartApps for use with Samsung's [SmartThings](http://www.smartthings.com) home automation platform.

## SmartApps

#### [Evohome (Connect) - BETA](https://github.com/codersaur/SmartThings/tree/master/smartapps/evohome-connect):
 - This SmartApp connects your Honeywell Evohome System to SmartThings.
 - Note, the Evohome Heating Zone device handler (below) must also be installed.

#### [InfluxDB Logger](https://github.com/codersaur/SmartThings/tree/master/smartapps/influxdb-logger):
 - This SmartApp logs SmartThings device attributes to an [InfluxDB](https://influxdata.com/) database.

### SmartApp Installation Procedure

#### Part One: Install the code using the SmartThings IDE

1. Within the SmartThings IDE, click '*My SmartApps*', then '*+ New SmartApp*'. 
2. Select the '*From Code*' tab and paste in the contents of the relevant groovy file.
3. Click '*Create*', and then '*Publish*' *(For Me)*.

#### Part Two: Create a SmartApp instance

1. Using the SmartThings app on your phone, navigate to the '*Marketplace*'.
2. Select '*SmartApps*', then browse to '*My Apps*' at the bottom of the list.
3. Select the new SmartApp, complete the configuration options and press '*Done*'.

**Note:** Some SmartApps may support multiple instances, whereas others may only allow one instance.

## Device Handlers

#### [Aeon Home Energy Meter (GEN2 - UK - 1 Clamp)](https://github.com/codersaur/SmartThings/tree/master/devices/aeon-home-energy-meter):
 - This device handler is written specifically for the Aeon Home Energy Meter Gen2 UK version, with a single clamp.
 - It supports live reporting of energy, power, current, and voltage, as well as energy and cost statistics over multiple pre-defined periods.

#### [Evohome Heating Zone - BETA](https://github.com/codersaur/SmartThings/tree/master/devices/evohome):
 - This device handler is required for the Evohome (Connect) SmartApp.

#### [Fibaro Dimmer 2 (FGD-212)](https://github.com/codersaur/SmartThings/tree/master/devices/fibaro-dimmer-2):
 - An advanced device handler for the Fibaro Dimmer 2 (FGD-212) Z-Wave Dimmer, with support for full parameter synchronisation, multi-channel device associations, protection modes, fault reporting, and advanced logging options.
 - The _Nightmode_ function forces the dimmer to switch on at a specific level (e.g. low-level during the night). It can be enabled/disabled manually using the _Nightmode_ tile, or scheduled from the device's settings.  
   <img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/fibaro-dimmer-2/screenshots/fd2-ss-tiles-on.png" width="200">

#### [Fibaro Flood Sensor (FGFS-101)](https://github.com/codersaur/SmartThings/tree/master/devices/fibaro-flood-sensor):
 - An advanced SmartThings device handler for the Fibaro Flood Sensor (FGFS-101) (EU), with support for full parameter synchronisation, multi-channel device associations, and advanced logging options.  
   <img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/fibaro-flood-sensor/screenshots/ffs-ss-tiles-wet.png" width="200">

#### [Fibaro RGBW Controller (FGRGBWM-441)](https://github.com/codersaur/SmartThings/tree/master/devices/fibaro-rgbw-controller):
 - This device handler is written specifically for the Fibaro RGBW Controller (FGRGBWM-441).
 - It extends the native SmartThings device handler to support editing the device's parameters from the SmartThings GUI, and to support the use of one or more of the controller's channels in IN/OUT mode (i.e. analog sensor inputs).  
   <img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/fibaro-rgbw-controller/screenshots/screenshot_rgbw.png" width="200">
 
#### [GreenWave PowerNode (Single) (NS210-G-EN)](https://github.com/codersaur/SmartThings/tree/master/devices/greenwave-powernode-single):
  - An advanced SmartThings device handler for the GreenWave PowerNode (Single socket) Z-Wave power outlet, with support for power and energy reporting, the _Room Colour Wheel_, local and RF protection modes, an _Auto-off Timer_, full parameter synchronisation, and advanced logging options.  
   <img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/greenwave-powernode-single/screenshots/gwpn-ss-tiles-main.png" width="200">

#### [Philio Dual Relay (PAN04)](https://github.com/codersaur/SmartThings/tree/master/devices/philio-dual-relay):
 - This device handler is written specifically for the Philio Dual Relay (PAN04), when used as a single switch/relay only.
 - It supports live reporting of energy, power, current, voltage, and power factor,  as well as energy and cost statistics over multiple pre-defined periods.
 
#### [TKB Metering Switch (TZ88E-GEN5)](https://github.com/codersaur/SmartThings/tree/master/devices/tkb-metering-switch):
 - This device handler is written specifically for the TKB Metering Switch (TZ88E-GEN5).
 - It supports live reporting of energy, power, current, voltage, and power factor,  as well as energy and cost statistics over multiple pre-defined periods.
 
#### [Z-Wave Tweaker](https://github.com/codersaur/SmartThings/tree/master/devices/zwave-tweaker):
 - A SmartThings device handler to assist with interrogating and tweaking Z-Wave devices. Useful for end-users and SmartThings developers.  
   <img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/zwave-tweaker/screenshots/zwt-ss-tiles-main.png" width="200">

### Device Handler Installation Procedure

#### Part One: Install the device handler code using the SmartThings IDE

1. Within the SmartThings IDE, click on '*My Device Handlers*'.
2. Click the '*+ Create New Device Handler*' button. 
3. Select the '*From Code*' tab and paste in the contents of the relevant groovy file.
4. Click '*Create*'.
5. Click '*Publish*' *(For Me)*.

#### Part Two: Connect your device to SmartThings

If your device is already connected to SmartThings, you can skip straight to part three, however if your physical device is not yet connected to SmartThings, you will need to follow [these instructions to _Add a Thing_](https://support.smartthings.com/hc/en-gb/articles/205956950-How-to-connect-and-configure-new-devices).

During the joining process SmartThings will select an appropriate device handler, if the correct device handler (installed in part one) is selected, you can skip to part four, otherwise you will need to change the handler as described in part three.

#### Part Three: Update existing device types

When you add new devices, SmartThings will automatically select the device handler with the closest-matching *fingerprint*. However, this process is not perfect and it often fails to select the desired device handler. You may also have pre-existing devices you want to switch to new device handler. In these cases, you need to change the device type of each device instance from the IDE.

1. Within the SmartThings IDE, click on '*My Devices*'.
2. Click on the appropriate device to bring up its properties.
3. Click the '*Edit*' button at the bottom.
4. Change the '*Type*' using the drop-down box (custom devices will be near the bottom of the list).
5. Hit the '*Update*' button at the bottom.

#### Part Four: Update existing device settings

If you have changed the type of an existing device, it is very important to update the device's settings to ensure the device instance is fully initialised and ready for use with the new device handler.

1. Within the SmartThings IDE, click on the '*Live Logging*' tab to monitor an messages generated by the following steps. 
2. In the SmartThings app on your phone, navigate to the device (you should find the GUI has changed to reflect the new tiles configuration).
3. Press the gear icon to edit the device's settings.
4. Review each setting to ensure it has a suitable value, then press '*Done*'.
5. Back in the SmartThings IDE, review any messages from the device in the '*Live Logging*' screen. 
 
**Note:** Android users may encounter some errors in the SmartThings app after a device type has been changed. This can usually be resolved by completely closing the SmartThings app and restarting it.

## License

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
in compliance with the License. You may obtain a copy of the License at:

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
for the specific language governing permissions and limitations under the License.
