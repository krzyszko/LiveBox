#!/bin/bash
progname="livebox"
progpath=`dirname $0`
logfile=${progpath}/$progname.stdout
pidfile=${progpath}/$progname.pid

        
status(){
	ps x | grep -v grep | grep `cat $pidfile 2>/dev/null` > /dev/null 2>&1
	if [ $? -eq 0 ]
	then
		echo "[`cat $pidfile`] $progname is running"
		return 0
	else
		echo "$progname is not running"
		return 1
	fi 
	
}
# Start the service FOO
start() {
        status > /dev/null 2>&1
	if [ $? -eq 0 ]
	then
		echo "$progname is already running"
		exit 1;
	fi
	echo "Starting $progname server"
        ### Create the lock file ###
	java -cp ${progpath}/lib/*:${progpath}/conf:LiveBox-0.1.1.jar dom.LiveBoxReboot > $logfile 2>&1 & echo $!>$pidfile 
        echo $"$progname server startup:"
        status
}
# Restart the service FOO
stop() {
        status > /dev/null 2>&1
	if [ $? -ne 0 ]
	then
		echo "$progname is not running"
		exit 1;
	fi
	echo "Stopping LiveBox server: "
        kill `cat $pidfile`
	sleep 5
	status > /dev/null 2>&1
	if [ $? -eq 0 ]
	then
	        echo "$progname running"
		exit 1;
	else
		echo "$progname successfully stopped"
		return 0
	fi

	### Now, delete the lock file ###
        
}
### main logic ###
case "$1" in
  start)
        start
        ;;
  stop)
        stop
        ;;
  status)
        status 
        ;;
  restart|reload|condrestart)
        stop
        start
        ;;
  *)
        echo $"Usage: $0 {start|stop|restart|reload|status}"
        exit 1
esac
exit 0

