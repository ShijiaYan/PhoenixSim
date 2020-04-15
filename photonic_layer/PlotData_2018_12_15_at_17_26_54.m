xVec_0 = [5.0, 16.0, 17.0, 18.0, 19.0, 20.0, 21.0, 22.0, 24.0, 25.0];
yVec_0 = [205.0, 185.0, 120.0, 75.0, 50.0, 35.0, 25.0, 15.0, 10.0, 5.0];
error_0 = [0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0];
xVec_1 = [5.0, 16.0, 17.0, 18.0, 19.0, 20.0, 21.0, 22.0, 24.0, 25.0];
yVec_1 = [360.0, 340.0, 220.0, 150.0, 100.0, 60.0, 40.0, 30.0, 20.0, 10.0];
error_1 = [0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0];
xVec_2 = [5.0, 16.0, 17.0, 18.0, 19.0, 20.0, 21.0, 22.0, 23.0, 25.0, 30.0];
yVec_2 = [442.0, 425.0, 306.0, 204.0, 136.0, 102.0, 68.0, 51.0, 34.0, 17.0, 0.0];
error_2 = [0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0];
xVec_3 = [5.0, 17.0, 18.0, 19.0, 20.0, 21.0, 22.0, 24.0, 28.0];
yVec_3 = [350.0, 300.0, 225.0, 150.0, 100.0, 75.0, 50.0, 25.0, 0.0];
error_3 = [0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0];
%x = [xVec_0 ; xVec_1 ; xVec_2 ; xVec_3 ; ];
%y = [yVec_0 ; error_0 ; yVec_1 ; error_1 ; yVec_2 ; error_2 ; yVec_3 ; error_3 ; ];

f = figure;

markerSize = 12;

ax = axes;
hold on;
set(ax,'FontSize',14);

p0 = plot(xVec_0, yVec_0,'Color',cmap(1,:),'LineWidth',2);
p1 = plot(xVec_1, yVec_1,'Color',cmap(2,:),'LineWidth',2);
p2 = plot(xVec_2, yVec_2,'Color',cmap(3,:),'LineWidth',2);
p3 = plot(xVec_3, yVec_3,'Color',cmap(4,:),'LineWidth',2);
hold off;
xlabel('FSR (nm)');
ylabel('Aggregate line rate (Gb/s)');
legend('[<Channel rate (Gb/s):5.0>]', '[<Channel rate (Gb/s):10.0>]', '[<Channel rate (Gb/s):17.0>]', '[<Channel rate (Gb/s):25.0>]');
grid on;
