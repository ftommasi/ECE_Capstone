sudo echo 1 > /sys/bus/iio/devices/iio\:device0/scan_elements/in_voltage0_en 
echo 100 > /sys/bus/iio/devices/iio\:device0/buffer/length
echo 1 > /sys/bus/iio/devices/iio\:device0/buffer/enable
