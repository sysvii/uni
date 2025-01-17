% Walkin Simulation Example - Initial Conditions
%     for Matlab 5.2 and up
%
%
% This file sets the initial conditions for a simulation
%
%
% Created by Martijn Wisse, Delft Bio-robotics Laboratory, August 2003
% Delft University of Technology

mode = 'stance phase leg 1';


%phi1 = 25*pi/180;      %initial angle leg1
phi2 = 10*pi/180;   % initial angle leg2
%phi1d = 1*pi;             % initial speed of rotation leg1
%phi2d = -1*pi;             % initial speed of rotation leg2

% independent initial conditions
phi1  = 0.2015;  % counter-clockwise rotation of leg 1 
%phi2 = - phi1;
phi1d =-1.4052; % velocity of counter-clockwise rotation of leg 1 
phi1d = -1.40;
phi2d =-1.1205; % velocity of counter-clockwise rotation of leg 2


% dependent initial conditions (the correct values 
xh    = 0;       % x-coordinate of hip joint
yh    = 0;       % y-coordinate of hip joint
%xh = l1*sin(phi1);
%yh = l1*cos(phi1);
xhd   = 0;      % velocity of x-coordinate of hip joint
yhd   = 0;      % velocity of y-coordinate of hip joint

xf1   = 0;      % location of foothold of leg 1 (equals x-coordinate of contact point if phi1=0)
xf2   = 0;      % location of foothold of leg 2 (equals x-coordinate of contact point if phi2=0)

q  = [xh,  yh,  phi1,  phi2];
qd = [xhd, yhd, phi1d, phi2d];
footholds = [xf1, xf2];

