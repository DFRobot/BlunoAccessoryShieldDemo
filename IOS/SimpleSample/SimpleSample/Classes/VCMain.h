//
//  VCMain.h
//  SimpleSample
//
//  Created by Seifer on 13-10-15.
//  Copyright (c) 2013年 DFRobot. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <CoreBluetooth/CoreBluetooth.h>
#import "BLEDevice.h"
#import "CustomView.h"
#import "Vector2.h"

enum TabState
{
    LED,
    ROCKER,
    POT
};

@interface VCMain : UIViewController<CBCentralManagerDelegate,CBPeripheralDelegate,CustomViewDelegate>
@property (weak, nonatomic) IBOutlet UIButton *btnLed;
@property (weak, nonatomic) IBOutlet UIButton *btnPot;
@property (weak, nonatomic) IBOutlet UIButton *btnJoy;
@property (weak, nonatomic) IBOutlet UIButton *btnComplete;
@property (weak, nonatomic) IBOutlet UIButton *btnClear;
@property (weak, nonatomic) IBOutlet UIButton *btnSubmit;
@property (weak, nonatomic) IBOutlet UILabel *lbTemp;
@property (weak, nonatomic) IBOutlet UILabel *lbHum;

@property (strong,nonatomic) CBCentralManager* centralManager;
//@property (strong,nonatomic) CBPeripheral*     device;
@property (strong,nonatomic) CBPeripheral*     peripheral;
@property (strong,nonatomic) BLEDevice*        bleDevice;

@property (nonatomic, strong) Vector2* stickCenterPoint;
@property (nonatomic, strong) Vector2* currentStickPoint;
@property (nonatomic, strong) NSMutableArray* aryMessages;
@property (weak, nonatomic) IBOutlet UIImageView *imgviewPoint;
@property (weak, nonatomic) IBOutlet UIImageView *imgviewRocker;
@property (weak, nonatomic) IBOutlet CustomView *viewPot;
@property (weak, nonatomic) IBOutlet CustomView *viewJoy;
@property (weak, nonatomic) IBOutlet CustomView *viewLed;
@property (weak, nonatomic) IBOutlet CustomView *viewPicker;
@property (weak, nonatomic) IBOutlet UIImageView *imgviewPotmeter;
@property (weak, nonatomic) IBOutlet UITextField *txtMessage;
@property (weak, nonatomic) IBOutlet UIImageView *imgviewTitle;
@property (weak, nonatomic) IBOutlet UIImageView *imgviewLedColor;
@property (weak, nonatomic) IBOutlet UIView *viewMainFrame;
@property (weak, nonatomic) IBOutlet UIView *viewMask;


- (IBAction)actionLed:(id)sender;
- (IBAction)actionJoy:(id)sender;
- (IBAction)actionPot:(id)sender;
- (IBAction)actionBuzzer:(id)sender;
- (IBAction)actionRelay:(id)sender;
- (IBAction)actionComplete:(id)sender;
- (IBAction)actionSubmit:(id)sender;
- (IBAction)actionClear:(id)sender;

-(NSMutableDictionary *) makeSensorTagConfiguration;

@end
