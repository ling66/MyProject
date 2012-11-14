//
//  Item.h
//  TestApp_1
//
//  Created by l l on 13/09/12.
//  Copyright (c) 2012 ll. All rights reserved.
//

#import <Foundation/Foundation.h>

@protocol ItemDisplay <NSObject>
//this method only use for calculating the view's width and height 
- (UIView *)viewForItem;
@end

@interface Item : NSObject<ItemDisplay>

@property (nonatomic,strong) UIView *view;

@end
