//
//  Vector2.m
//  TestTouches
//
//  Created by Seifer on 13-2-14.
//  Copyright (c) 2013年 Seifer. All rights reserved.
//

#import "Vector2.h"


@implementation Vector2

#pragma mark- OtherElse
- (Vector2*) addVector2:(Vector2*)vector
{
    double nX = _x + vector.x;
    double nY = _y + vector.y;
    Vector2* vector_ = [[Vector2 alloc] initWithV1:nX V2:nY];
    return vector_;
}

- (Vector2*) subVector2:(Vector2*)vector
{
    double nX = _x - vector.x;
    double nY = _y - vector.y;
    Vector2* vector_ = [[Vector2 alloc] initWithV1:nX V2:nY];
    return vector_;
}

- (Vector2*) multiplyNum:(double)number
{
    double nX = _x * number;
    double nY = _y * number;
    Vector2* vector_ = [[Vector2 alloc] initWithV1:nX V2:nY];
    return vector_;
}

- (void) normalize
{
    double length = sqrt(pow(_x, 2) + pow(_y, 2));
    _x /= length;
    _y /= length;
    
}

- (double) getLengthFromVector:(Vector2*) vector
{
    double nX = _x - vector.x;
    double nY = _y - vector.y;
    double length = sqrt(pow(nX, 2) + pow(nY, 2));
    return length;
}

#pragma mark- CycleLife



- (id) initWithV1:(double)x V2:(double)y
{
    self = [super init];
    if (self)
    {
        _x = x;
        _y =  y;

    }
    return self;
    
}

@end
