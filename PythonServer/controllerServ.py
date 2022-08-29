import socket
import time
PORT = 11888
totalEnd = True
prevLog = 'begin'


DEADZONE = 75
import os
os.system("sudo pigpiod")
time.sleep(1)
import pigpio
ESCL1 = 4
ESCL2 = 17
ESCR1 = 27
ESCR2 = 22
pi=pigpio.pi();
pi.set_servo_pulsewidth(ESCL1,0)
pi.set_servo_pulsewidth(ESCL2,0)
pi.set_servo_pulsewidth(ESCR1,0)
pi.set_servo_pulsewidth(ESCR2,0)
max_value = 2000
min_value = 700
speedL = 1500
speedR = 1500
scale = 2

serv1 = 13
serv2 = 19
serv3 = 26
pi.set_mode(serv1,pigpio.OUTPUT)
pi.set_mode(serv2,pigpio.OUTPUT)
pi.set_mode(serv3,pigpio.OUTPUT)
time.sleep(1)

def controlFunc(x, y):
    print(x,y,sep=',')
    y = -y
    speedL = scale * ((y*100) - x - (y*abs(x)*50) + (x*abs(y)*50))
    if (speedL < 0):
        speedL = 1500 - DEADZONE + speedL
    else:
        speedL = 1500 + DEADZONE + speedL
    speedR = scale * ((y*100) + (x*100) - (y*abs(x)*50) - (x*abs(y)*50))
    if (speedR < 0  ):
        speedR = 1500 - DEADZONE + speedR
    else:
        speedR = 1500 + DEADZONE + speedR
    print("Left Speed: ", speedL)
    print("Right Speed: ", speedR)
    pi.set_servo_pulsewidth(ESCL1,speedL)
    pi.set_servo_pulsewidth(ESCL2,speedL)
    pi.set_servo_pulsewidth(ESCR1,speedR)
    pi.set_servo_pulsewidth(ESCR2,speedR)
    return
    
if __name__ == '__main__':
    while(True):
        try:
            while (totalEnd):
                s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                s.bind(('', PORT))        
                print ("Socket binded to %s" %(PORT))
                s.listen(5)    
                print ("Server is currently listening...")           
                c, addr = s.accept()
                while True:
                    latency = int(round(time.time() * 1000))
                    inLog = ''
                    inLog = c.recv(1024).decode()
                    if inLog[:-2] == 'quit':
                        print("quit.")
                        break
                    elif inLog[:-2] == 'totalQuit':
                        print("total quit.")
                        totalEnd = False
                        break
                    if (True):
                        latency = (int(round(time.time() * 1000)) - int(inLog.split("<SEP>")[-1]))
                        print("Latency:", latency, sep=' ')
                        control = inLog.split("<SEP>")
                        print(control)
                        if (control[0][5] == '-'):
                            x = control[0][5:10]
                            if(control[0][11] == '-'):
                                y = control[0][11:16]
                            else:
                                y = control[0][11:15]
                        else:
                            x = control[0][5:9]
                            if(control[0][10] == '-'):
                                y = control[0][10:15]
                            else:
                                y = control[0][10:14]
                        
                        x = float(x)
                        y = float(y)
                        controlFunc(x, y)
                        
                        seekOne = control[2][4:]
                        print("Servo Position One Is: " + seekOne, sep='')
                        servFunc(serv1, seekOne)
                        seekTwo = control[3][4:]
                        print("Servo Position Two Is: " + seekTwo, sep='')
                        servFunc(serv2, seekTwo)
                        seekThree = control[4][4:]
                        print("Servo Position Three Is: " + seekThree, sep='')
                        servFunc(serv3, seekThree)
                    
                    prevLog = inLog
                    messCheck = "Test: " + inLog
                    c.send(messCheck.encode())
        except:
            continue
        finally:
            print("DISCONNECT")
            s.close()
            c.close()
    s.close()
