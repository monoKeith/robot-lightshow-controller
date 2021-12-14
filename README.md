# Robot-Lightshow

## Overview

Inspired by commercial drone light shows, I intended to build a software for designing 2D light
show patterns, which can control multiple robots to fulfill the designated animations or images.
And finally see the results by using a simulator.

## Motivation
Intel Cooperation have been offering light shows which are performed by drones. In a total of 100
to 500 drones, each of them carries a LED light which can change to any color and flies to pre-
defined locations, performing stunning animations and offers eye-catching visual effects. These
complex drone operations are all designed prior to the event, using Intel’s proprietary software.


## Goal

Similar to the idea of drone light shows, but instead of making the hardware, I will attempt to
implement a simplified 2-dimensional light show in a simulated environment ([Webots](https://cyberbotics.com/)) with robots.

In total of 10 to 50 robots (users can configure the amount) each with a LED light, able to move to
pre-defined locations and control the pixels (LED lights), forming a designated image (or
animation).

The most important part of this project is the controller. It is responsible for providing an interface
to design the frames, as well as controlling all robots to move simultaneously. At each frame, each
robot will have different location and destination, controller should provide the option to adjust
their speed automatically, so they arrive at the same time, resulting in smoother animation. The
duration of a frame will have a lower bound determined by traveling distance. I will need to do
some research and develop an algorithm in order to achieve this.
The user interface of the controller software should have:

- An area where user can drag representation (e.g. dots) of robots, to make a frame
- A scrollable timeline representing an array of frames, also work as frame selector
- Options to configure behavior (e.g. color) for each robot


## Demo Video
[YouTube](https://youtu.be/m3dJQ_ByKjo)


## Project structure
- `Controller` directory containes source code of the controller software
- `Webots_Env` directory containes webots world file and robot design file


## How to run?

### Startup Solace PubSub+ Event Broker as Docker container

`docker run -d -p 8080:8080 -p 55555:55555 -p 8008:8008 -p 1883:1883 -p 8000:8000 -p 5672:5672 -p 9000:9000 -p 2222:2222 --shm-size=2g --env username_admin_globalaccesslevel=admin --env username_admin_password=admin --name=solace solace/solace-pubsub-standard`

### Run Webots

Open `Webots_Env/world/testField_30.wbt`

### Run Controller

In project `ControllerAPP_JavaFX`, run main function in `BotControllAPP` class.
