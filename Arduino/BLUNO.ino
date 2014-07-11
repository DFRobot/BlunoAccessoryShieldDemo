#include "Arduino.h"
#include "PlainProtocol.h"
#include "U8glib.h"
#include "blunoAccessory.h"


#define RIGHT 1
#define UP 2
#define LEFT 3
#define DOWN 4
#define PUSH 5
#define MID 0

//PlainProtocol constructor, define the Serial port and the baudrate.
PlainProtocol myBLUNO(Serial,115200);

//blunoAccessory constructor, for setting the relay,buzzer, temperature, humidity, knob, joystick and RGBLED
blunoAccessory myAccessory;

//OLED constructor, for oled display
U8GLIB_SSD1306_128X64 myOled(U8G_I2C_OPT_NONE);

String oledDisplay="";      //the display string recieved from the mobile device

int humidity=0;             //humidity read from DHT11
int temperature=0;          //temperature read from DHT11

int ledRed=0;               //RGBLED red value
int ledGreen=0;             //RGBLED green value
int ledBlue=0;              //RGBLED blue value

int knob=0;                 //knob(potentiometer) value read from analog pin

int joyStick=0;             //joystick state

void setup() {
    myAccessory.begin();
    myBLUNO.init();
    myAccessory.setRGBLed(0,0,0);   //turn off the RGBLED
}


void draw (void)
{
    myOled.setFont(u8g_font_unifont);
    
    myOled.setPrintPos(10,16);      //set the print position
    myOled.print("H:");
    myOled.print(humidity);         //show the humidity on oled
    myOled.print("%");
    
    myOled.setPrintPos(10,32);
    myOled.print("T:");             //show the temperature on oled
    myOled.print(temperature);
    myOled.print("C");
    
    myOled.setPrintPos(88,16);
    myOled.print("R:");             //show RGBLED red value
    myOled.print(ledRed);
    myOled.setPrintPos(88,32);
    myOled.print("G:");             //show RGBLED green value
    myOled.print(ledGreen);
    myOled.setPrintPos(88,48);
    myOled.print("B:");             //show RGBLED blue value
    myOled.print(ledBlue);

    myOled.setPrintPos(10,48);
    myOled.print("Knob:");
    myOled.print(knob);             //show knob(potentiometer) value read from analog pin
   
    myOled.setPrintPos(10,60);
    if (oledDisplay.length()) {
        myOled.print(oledDisplay);  //if the display string recieved from the mobile device, show the string
    }
    else{
        myOled.print("Joystick:");  //if string is null, show the state of joystick
        switch (joyStick){
            case MID:
                myOled.print("Normal");
                break;
            case RIGHT:
                myOled.print("Right");
                break;
            case UP:
                myOled.print("Up");
                break;
            case LEFT:
                myOled.print("Left");
                break;
            case DOWN:
                myOled.print("Down");
                break;
            case PUSH:
                myOled.print("Push");
                break;
            default:
                break;
        }
    }
}


void loop()
{
    if (myBLUNO.available()) {    //receive valid command
        if (myBLUNO.equals("BUZZER")) {     //if the command name is "BUZZER"
            myAccessory.setBuzzer(myBLUNO.read());  //read the content and set the buzzer
        }
        else if (myBLUNO.equals("RELAY")){      //if the command name is "RELAY"
            myAccessory.setRelay(myBLUNO.read());   //read the content and set the relay
        }
        else if (myBLUNO.equals("DISP")){       //if the command name is "DISP"
            oledDisplay=myBLUNO.readString();   //read the string to local value
        }
        else if(myBLUNO.equals("RGBLED")){      //if the command name is "RGBLED"
            ledRed=myBLUNO.read();              //read the red value first
            ledGreen=myBLUNO.read();            //read the green value then
            ledBlue=myBLUNO.read();             //read the blue value at last
            
            myAccessory.setRGBLed(ledRed, ledGreen, ledBlue);   //set the color to the RGBLED
            }
        else if(myBLUNO.equals("TEMP")){    //if the command name is "TEMP"
            myBLUNO.write("TEMP", temperature);     //return the value of temperature to mobile device
            }
        else if(myBLUNO.equals("HUMID")){   //if the command name is "HUMID"
            myBLUNO.write("HUMID", humidity);   //return the value of humidity to mobile device
            }
        else if(myBLUNO.equals("KNOB")){    //if the command name is "KNOB"
            myBLUNO.write("KNOB", myAccessory.readKnob());  //return the value of the knob(potentiometer) to mobile device
            }
    }
    
    static unsigned long oledTimer=millis();        //every 200ms update the oled display
    if (millis() - oledTimer >= 200) {
        oledTimer=millis();
        myOled.firstPage();                         //for more information about the U8glib library, go to https://code.google.com/p/u8glib/
        do{
            draw();
        }
        while(myOled.nextPage());
    }
    
    static unsigned long DHT11Timer=millis();       //every 2s update the temperature and humidity from DHT11 sensor
    if (millis() - DHT11Timer >= 2000) {
        DHT11Timer=millis();
        temperature=myAccessory.readTemperature();
        humidity=myAccessory.readHumidity();
    }
    
    static unsigned long Timer500ms=millis();       //every 500ms update the knob(potentiometer) and the joystick
    if (millis() - Timer500ms >= 500) {
        Timer500ms=millis();
        knob=myAccessory.readKnob();
        joyStick=myAccessory.readJoystick();
    }
    
    if (myAccessory.joystickAvailable()) {          //if the state of joystick is changed
        joyStick=myAccessory.readJoystick();        //update the joystick
        myBLUNO.write("ROCKER", joyStick);          //send the command and value to mobile device
        oledDisplay="";                             //clear the oled display string to show the new state of joystick on oled
    }


}


