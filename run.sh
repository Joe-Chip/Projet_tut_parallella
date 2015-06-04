#!/bin/sh
sudo LD_LIBRARY_PATH=/opt/adapteva/esdk/tools/host/lib EPIPHANY_HDF=/opt/adapteva/esdk/bsps/current/platform.hdf java -Djava.library.path=C balayageK2/Main
