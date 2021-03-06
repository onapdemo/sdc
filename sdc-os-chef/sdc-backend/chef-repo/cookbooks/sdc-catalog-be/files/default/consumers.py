#!/usr/bin/python
import subprocess
#from time import sleep
import time
from datetime import datetime

class bcolors:
    HEADER    = '\033[95m'
    OKBLUE    = '\033[94m'
    OKGREEN   = '\033[92m'
    WARNING   = '\033[93m'
    FAIL      = '\033[91m'
    ENDC      = '\033[0m'
    BOLD      = '\033[1m'
    UNDERLINE = '\033[4m'


##############################
#    Functions
##############################
def checkBackend():
    command="curl -s -o /dev/null -I -w \"%{http_code}\" -i http://localhost:8080/sdc2/rest/v1/user/jh0003"

    proc = subprocess.Popen( command , shell=True , stdout=subprocess.PIPE )
    (out, err) = proc.communicate()
    result = out.strip()
    return result


def checkConsumer(consumerName):
    command="curl -s -o /dev/null -I -w \"%{http_code}\" -i -H \"Accept: application/json; charset=UTF-8\" -H \"Content-Type: application/json\" -H \"USER_ID: jh0003\"   http://localhost:8080/sdc2/rest/v1/consumers/" + consumerName

    proc = subprocess.Popen( command , shell=True , stdout=subprocess.PIPE )
    (out, err) = proc.communicate()
    result = out.strip()
    return result


def createConsumer( consumerName, consumerSalt, consumerPass ):
    print '[INFO] ' + consumerName
    command="curl -s -o /dev/null -w \"%{http_code}\" -X POST -i -H \"Accept: application/json; charset=UTF-8\" -H \"Content-Type: application/json\" -H \"USER_ID: jh0003\" http://localhost:8080/sdc2/rest/v1/consumers/ -d '{\"consumerName\": '" + consumerName + "', \"consumerSalt\": '" + consumerSalt + "',\"consumerPassword\": '" + consumerPass + "'}'"

    proc = subprocess.Popen( command , shell=True , stdout=subprocess.PIPE)

    (out, err) = proc.communicate()
    result = out.strip()
    return result




##############################
#    Definitions
##############################
consumersList = [ "aai" , "appc" , "dcae" , "mso" , "sdnc" , "vid" , "cognita", "clamp" , "vfc" ]
salt = "9cd4c3ad2a6f6ce3f3414e68b5157e63"
password = "35371c046f88c603ccba152cb3db34ec4475cb2e5713f2fc0a43bf18a5243495"
beStat=0


##############################
#    Main
##############################

for i in range(1,10):
    myResult = checkBackend()
    if myResult == '200':
        print '[INFO]: Backend is up and running'
        beStat=1
        break
    else:
        currentTime = datetime.now()
        print '[ERROR]: ' + currentTime.strftime('%Y/%m/%d %H:%M:%S') + bcolors.FAIL + ' Backend not responding, try #' + str(i) + bcolors.ENDC
        time.sleep(10)

if beStat == 0:
    print '[ERROR]: ' + time.strftime('%Y/%m/%d %H:%M:%S') + bcolors.FAIL + 'Backend is DOWN :-(' + bcolors.ENDC
    exit()

for consumer in consumersList:
    myResult = checkConsumer(consumer)
    if myResult == '200':
        print '[INFO]: ' + consumer + ' already exists'
    else:
        myResult = createConsumer( consumer, salt, password )
        if myResult == '201':
            print '[INFO]: ' + consumer + ' created, result: [' + myResult + ']'
        else:
            print '[ERROR]: ' + bcolors.FAIL + consumer + bcolors.ENDC + ' error creating , result: [' + myResult + ']'
