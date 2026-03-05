#!/bin/bash
set -e

export ANDROID_HOME=$HOME/android-sdk
export DISPLAY=:99

# Create X11 socket directory
sudo mkdir -p /tmp/.X11-unix
sudo chmod 1777 /tmp/.X11-unix

# Start Xvfb
Xvfb :99 -screen 0 1080x1920x24 -ac +extension GLX +render -noreset &
sleep 2
echo "Xvfb started"

# Start x11vnc
x11vnc -display :99 -forever -nopw -shared -rfbport 5900 &
sleep 1
echo "VNC on port 5900"

# Start noVNC
websockify --web=/usr/share/novnc 6080 localhost:5900 &
sleep 1
echo "noVNC on port 6080"

# Start emulator (foreground to keep script alive)
echo "Starting emulator..."
$ANDROID_HOME/emulator/emulator \
  -avd test_device \
  -no-audio \
  -gpu swiftshader_indirect \
  -no-snapshot \
  -no-boot-anim \
  -memory 1536
