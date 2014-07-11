//
//  CustomView.m
//  The man-machine efficacy
//
//  Created by Seifer on 13-03-02.
//  Copyright (c) 2013年 Seifer. All rights reserved.
//

#import "CustomView.h"

@implementation CustomView

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
    }
    return self;
}

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    
    [super touchesBegan:touches withEvent:event];
    int count = (int)[touches count];
    CGPoint points[count];
    for (int i = 0; i < count; i++) {
        UITouch *touch = [touches anyObject];
        CGPoint touchPoint = [touch locationInView:self];
        points[i] = touchPoint;
    }
    
    if ([((NSObject*)_delegate) respondsToSelector:@selector(CustomViewTouchesBeganPoints:Count:WhichView:)])
    {
        [_delegate CustomViewTouchesBeganPoints:points Count:count  WhichView:self];
    }

    
}

- (void)touchesMoved:(NSSet *)touches withEvent:(UIEvent *)event{
    
    [super touchesMoved:touches withEvent:event];
    int count = (int)[touches count];
    CGPoint points[count];
    for (int i = 0; i < count; i++) {
        UITouch *touch = [touches anyObject];
        CGPoint touchPoint = [touch locationInView:self];
        points[i] = touchPoint;
    }
    
    if ([((NSObject*)_delegate) respondsToSelector:@selector(CustomViewTouchesMovedPoints:Count:WhichView:)])
    {
        [_delegate CustomViewTouchesMovedPoints:points Count:count  WhichView:self];
    }
    
}

- (void)touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event{
    [super touchesEnded:touches withEvent:event];
    int count = (int)[touches count];
    CGPoint points[count];
    for (int i = 0; i < count; i++) {
        UITouch *touch = [touches anyObject];
        CGPoint touchPoint = [touch locationInView:self];
        points[i] = touchPoint;
    }
    
    if ([((NSObject*)_delegate) respondsToSelector:@selector(CustomViewTouchesEndedPoints:Count:WhichView:)])
    {
        [_delegate CustomViewTouchesEndedPoints:points Count:count  WhichView:self];
    }
}



@end
