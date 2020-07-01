# RemoteWakeUp
Android App to allow you to remotely power on PC's using Wake-On-LAN

## Usage

The application allows you to wake up any PC remotely presuming you have the following information:

- The public hostname of the PC (must have a DDNS solution pointing to the IP address)
- The MAC address of the PC
- The port which redirects the packet to the machine to wake up.

The default port is 9 for WOL services. This requires a port forward rule in the firewall of your router or connection to the machine to allow some port to access port 9 of the machine.

The machine requires a static IP address assigned through DHCP to ensure that it does not change on each boot. This port forward rule will point to the address at port 9.
This will then allow the application to wake up the machine on a request.
