# Fibaro RGBW Controller (FGRGBWM-441)
https://github.com/codersaur/SmartThings/tree/master/devices/fibaro-rgbw-controller

Copyright (c) [David Lomas](https://github.com/codersaur)

## Overview

This SmartThings device handler has been written for the Fibaro RGBW Controller (FGRGBWM-441). It extends the native SmartThings device handler to support editing the device's parameters from the SmartThings GUI, and to support the use of one or more of the controller's channels in IN/OUT mode (i.e. analog sensor inputs).

### Key features:
* Physical device parameters can be edited from the Smartthings GUI, and verified in the IDE Log.
* Channels can be mapped to different colours without needing to physically rewire the device.
* Shortcut tiles for the built-in RGBW programs.
* Shortcut tiles for named colours.
* Multiple options for the calculation of aggregate `switch` and `level` attributes (useful when using a combination of inputs and outputs).
* Configurable thresholds for mapping the level of input channels to their corresponding `switch` (on/off) states.
* Implements "Energy Meter", "Power Meter", and "Polling" capabilities.
* For SmartApp developers, the `setColor()` command supports an extended range of colorMap key combinations:
 * red, green, blue, white
 * red, green, blue
 * hue, saturation, level
 * hex
 * name
* Extensive inline code comments to support community development.

### Screenshots:

<img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/fibaro-rgbw-controller/screenshots/screenshot_rgbw.png" width="200">
<img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/fibaro-rgbw-controller/screenshots/screenshot_rgb_plus_input.png" width="200">
<img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/fibaro-rgbw-controller/screenshots/screenshot_power_energy2.png" width="200">
<img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/fibaro-rgbw-controller/screenshots/screenshot_four_inputs.png" width="200">
<img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/fibaro-rgbw-controller/screenshots/screenshot_color_shortcuts.png" width="200">

## Installation
To install the device handler:

1. Follow [these instructions](https://github.com/codersaur/SmartThings#device-handler-installation-procedure) to install the device handler in the SmartThings IDE.

2. From the SmartThings IDE, edit the device handler code to suit your needs. Specifically, the tiles section will need to be customised to suit the channel configuration in use (see the use cases below). [If you have multiple FIbaro RGBW Controllers, each with different channel configurations, then you will need to create multiple copies of the device handler with different names.]

3. From the SmartThings app, edit the device settings to suit the channel configuration in use (see the use cases below) and hit _Done_. [It is possible to verify the device parameters from the Live Logging tab in the SmartThings IDE.]

4. Once the settings have been applied, power-cycle the Fibaro RGBW Controller.

### Example Use Cases

#### Four-channel RGBW LED strip:

By default, the device handler is configured for use with a four-channel RGBW LED strip, so there is no need to edit the device handler code. The SmartThings GUI should look like the following:

<img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/fibaro-rgbw-controller/screenshots/screenshot_rgbw.png" width="200">

#### Three-channel RGB LED strip, plus a 0-10V analog sensor input:

For this use case, it is recommended to use Channel #1 as Red, Channel #2 as Green, Channel #3 as Blue, and Channel #4 as the analog input.

In the device handler code, edit the tiles section to comment out the _White_ channel tiles:

    "switchWhite", "levelWhiteSlider", "levelWhiteTile",

Then uncomment the read-only input channel for Ch4:

    "switchCh4ReadOnly", "ch4Label", "levelCh4Tile",

The _Built-in Program Shortcut_ tiles can also be commented out as these will not function in this configuration.

<img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/fibaro-rgbw-controller/screenshots/tiles_code_rgb_plus_input.png" width="400">

In the SmartThings app, edit the device settings. Configure the channel mappings so that Channel #4 maps to `Input` and Parameter #14 so that Channels #1/2/3 are set to `9. OUT...` and Channel #4 is set to `8. IN - ANALOG 0-10V (SENSOR)`.

<img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/fibaro-rgbw-controller/screenshots/settings_mappings_rgb.png" width="200">
<img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/fibaro-rgbw-controller/screenshots/settings_params_rgb_plus_in.png" width="200">

The SmartThings GUI should end up looking like the following:

<img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/fibaro-rgbw-controller/screenshots/screenshot_rgb_plus_input.png" width="200">


#### Two single-channel output loads, and two 0-10V analog sensor inputs:

In this example, channels #1 and #2 are used as outputs (e.g. two circuits of white lights), and channels #3 & #4 are used for analog sensor inputs.

In the device handler code, edit the tiles section to comment out all of the colour channel tile lines:

    // RGBW Channels:
    //"switchRed","levelRedSlider", "levelRedTile",
    //"switchGreen","levelGreenSlider", "levelGreenTile",
    //"switchBlue","levelBlueSlider", "levelBlueTile",
    //"switchWhite", "levelWhiteSlider", "levelWhiteTile",

Uncomment the lines for the Ch1 and Ch2 OUT channels, and the Ch3 and Ch4 input tiles:

    // OUT Channels:
    "switchCh1","levelCh1Slider", "levelCh1Tile",
    "switchCh2","levelCh2Slider", "levelCh2Tile",
    ...

    // INPUT Channels (read-only, label replaced slider control):
    ...
    "switchCh3ReadOnly", "ch3Label", "levelCh3Tile",
    "switchCh4ReadOnly", "ch4Label", "levelCh4Tile",

The _Built-in Program Shortcut_ and _Color Shortcut_ tiles can also be commented out as these will not function in this configuration.

<img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/fibaro-rgbw-controller/screenshots/tiles_code_two_out_two_in.png" width="400">

In the device settings, configure the channel mappings so that Channels #3 & #4 map to `Input`, and configure Parameter #14 so that Channels #1 & #2 are set to an `OUT ...` mode, and Channels #3 & #4 are set to `8. IN - ANALOG 0-10V (SENSOR)`

<img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/fibaro-rgbw-controller/screenshots/settings_params_two_out_two_in.png" width="200">

The SmartThings GUI should end up looking like the following:

<img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/fibaro-rgbw-controller/screenshots/screenshot_two_out_two_in.png" width="200">


#### Four 0-10V analog sensor inputs:

In this example, all four channels on the RGBW controller are used as analog sensor inputs. As the output channels cannot be used the tiles configuration can be simplified.

In the device handler code, edit the tiles section to comment out the RGBW channel tiles:

    // RGBW Channels:
    //"switchRed","levelRedSlider", "levelRedTile",
    //"switchGreen","levelGreenSlider", "levelGreenTile",
    //"switchBlue","levelBlueSlider", "levelBlueTile",
    //"switchWhite", "levelWhiteSlider", "levelWhiteTile",

Uncomment the lines for all input tiles:

    // INPUT Channels (read-only, label replaced slider control):
    "switchCh1ReadOnly", "ch1Label", "levelCh1Tile",
    "switchCh2ReadOnly", "ch2Label", "levelCh2Tile",
    "switchCh3ReadOnly", "ch3Label", "levelCh3Tile",
    "switchCh4ReadOnly", "ch4Label", "levelCh4Tile",

Additionally, comment out the _Energy and Power_ tiles, the _Built-in Program Shortcut_ tiles, and the _Color Shortcut Tiles_ sections as none of these will function in this configuration.

<img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/fibaro-rgbw-controller/screenshots/tiles_code_four_inputs.png" width="400">

In the device settings, configure the channel mappings so that all channels map to `Input`. It is possible to alter the threshold values too, which control the level at which each input is considered "ON".

Configure Parameter #14 so that all channels are set to `8. IN - ANALOG 0-10V (SENSOR)`

<img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/fibaro-rgbw-controller/screenshots/settings_mappings_four_inputs.png" width="200">
<img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/fibaro-rgbw-controller/screenshots/settings_params_four_inputs.png" width="200">

The SmartThings GUI should end up looking like the following:

<img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/fibaro-rgbw-controller/screenshots/screenshot_four_inputs.png" width="200">


## Physical Device Notes:

Some general notes relating to the Fibaro RGBW Controller:

* Parameter #14 is used to control the mode of each channel. When editing this parameter keep in mind:
 * If using RGBW modes, all channels must have exactly the same mode.
 * Mixing RGBW channels with IN/OUT channels at the same time will cause weird behaviour, for example the IN channels may report incorrect levels (as the INPUT is treated as a switch input for the RGBW channels).
  * If you want to use one or more channels as analog inputs, then the remaining channels must be set to OUT mode.
  * If using IN/OUT channel modes, the OUT channels can still be mapped to colours, but the built-in "RGBW programs" will have no effect.
  * switchColorSet commands do not affect INPUT channels, but you can't use switchColorGet to get the level of an INPUT channel either.
  * Energy and power reports for individual channels are not available, only the aggregate device as a whole.

There are two known bugs in firmware 25.25, which this device handler attempts to work around:

* BUG: If the device's parameters are changed, the device may stop responding to many Z-Wave _Get_ commands. **It is therefore recommended to power-cycle the device after changing parameters.**
* BUG: If a basicSet or switchMultilevelSet command is issued to channel 0 or to an INPUT channel, then the levels of all INPUT channels may be incorrectly reported as zero. Incorrect reports will persist until there is a change to the input voltages that is greater than the 'input change threshold' defined by Paramter #43. To avoid this issue, this device handler does not send basicSet or switchMultilevelSet commands to channels in INPUT mode.

## Version History:

#### 2017-04-17: v0.04
 * installed(): Initialises attribute values in addition to state.
 * updated(): Added check to prevent double execution, and to call installed() if not run.
  
#### 2016-11-14: v0.03
 * Association Group Members can be edited from the SmartThings GUI.
  
#### 2016-11-13: v0.02
 * Fix to preferences definition to prevent crashes on Android.
 * on(): Restores saved levels of channels, but if all saved levels are zero, then all channels are set to 100%.
 * onChX(): If the saved level is zero, then the channel will be set to 100%.
 * installed(): state variables are pre-populated.
 * configure(): Removes all nodes from association group #5 before re-adding the hub's ID.

#### 2016-11-08: v0.01
 * Added support for channels in IN/OUT modes.
 * Physical device parameters can be changed from the Smartthings GUI, and verified in the IDE Log.
 * Added event handlers for: MeterReport, SwitchColorReport, AssociationReport.
 * reset() resets the accumulated energy usage, not the brightness.
 * Added three options for the calculation of aggregate `switch` and `level` attributes (IN Only / OUT Only / ALL).
 * Added support for channel mappings and thresholds.
 * on()/off(): Only sends commands to OUT channels, to avoid changing levels of INPUTS.
 * setLevel(): Added two modes for setting levels (SIMPLE / SCALE).
 * setColor(): Supports colorMaps with red/green/blue/white, hue/saturation/level, hex, or name keys.
 * updated(): Validates settings for parameter #14 and generates a warning if there's a mixture of RGBW and IN/OUT.
 * getSupportedCommands(): New method to encapsulate a map of the command class versions supported by the device.
 * color attribute is now a map [hue: x, saturation: y, ...], as per SmartThings Capabilities Reference.
 * level attributes and sliders now have a have range of 0-100 percent, instead of 0-99.
 * Added *activeProgram* attribute to support Program tiles.
 * Added *colorName* attribute to support Color Shortcut tiles.
 * Added Test tile and ability to interrogate current device parameters.
 * Added Polling capability, which polls all channels, plus energy and power.
 * configure(): Added workaround for bug in configurationV1.configurationSet().

 ## To Do:
 * Add an option to use the White channel in preference to RGB channels when the RGB values equate to white.
 * When sending commands, consider Parameter #42 (reporting). If the device is configured not to send reports, or to only send reports generated by inputs (and not the controller), then a request for the appropriate report must be issued (or call refresh()) a couple of seconds after the command has been issued.
 * Add new device preference "Update ST UI before sending commands": This will issue fake events to make the GUI more responsive when using long dimming durations.
 * Consider if `setLevel()` in SCALE mode should really be moved to new `setBrightness()` command. Slider on the multiTile can then be linked to either setLevel or setBrightness via settings.

## References
 Some useful links relevant to the development of this device handler:
* [Fibaro RGBW Controller Z-Wave certification information](http://products.z-wavealliance.org/products/1054)
* [Color Control Capability](https://community.smartthings.com/t/capability-color-control-color-attribute-command-ambiguous-incorrectly-documented-and-or-improperly-used-in-dths/58018/23)
* [Using the Switch Color Command Class](https://community.smartthings.com/t/color-switch-z-wave-command-class/19300)
* [RGB-RGBW colorMap conversion](http://stackoverflow.com/questions/21117842/converting-an-rgbw-color-to-a-standard-rgb-hsb-rappresentation)

## License

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at:

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
