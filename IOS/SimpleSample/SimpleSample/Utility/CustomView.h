//
//  CustomView.h
//  The man-machine efficacy
//
//  Created by Seifer on 13-03-02.
//  Copyright (c) 2013年 Seifer. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol CustomViewDelegate

@optional
- (void)CustomViewTouchesBeganPoints:(CGPoint*)points Count:(int)count  WhichView:(id)customView;
- (void)CustomViewTouchesMovedPoints:(CGPoint*)points Count:(int)count  WhichView:(id)customView;
- (void)CustomViewTouchesEndedPoints:(CGPoint*)points Count:(int)count  WhichView:(id)customView;

@end



@interface CustomView : UIView

@property (nonatomic,weak) id<CustomViewDelegate> delegate;

@end
