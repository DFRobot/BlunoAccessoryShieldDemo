//
//  Vector2.h
//  TestTouches
//
//  Created by Seifer on 13-2-14.
//  Copyright (c) 2013年 Seifer. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface Vector2 : NSObject

@property( nonatomic, assign) double x;
@property( nonatomic, assign) double y;

- (id) initWithV1:(double)x V2:(double)y;
- (Vector2*) addVector2:(Vector2*)vector;
- (Vector2*) subVector2:(Vector2*)vector;
- (Vector2*) multiplyNum:(double)number;
- (void)     normalize;
- (double)   getLengthFromVector:(Vector2*) vector;

@end
