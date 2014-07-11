//
//  VCBase.m
//  SimpleSample
//
//  Created by Seifer on 13-10-15.
//  Copyright (c) 2013年 DFRobot. All rights reserved.
//

#import "VCBase.h"

@interface VCBase ()
{
    VCMain* _vcMain;
}

@end

@implementation VCBase

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    _vcMain = [[VCMain alloc] initWithNibName:@"VCMain" bundle:nil];
    [self.view addSubview:_vcMain.view];
    
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}
- (UIStatusBarStyle)preferredStatusBarStyle
{
    return UIStatusBarStyleLightContent;
}
- (BOOL)prefersStatusBarHidden
{
    return YES;
}

@end
