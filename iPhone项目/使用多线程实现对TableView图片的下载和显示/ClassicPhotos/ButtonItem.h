//
//  ButtonItem.h
//  TestApp_1
//
//  Created by l l on 13/09/12.
//  Copyright (c) 2012 ll. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Item.h"

@protocol ButtonItemAction <NSObject>
- (void)addTarget:(id)target action:(SEL)action forControlEvents:(UIControlEvents)event;
@end

@interface ButtonItem : Item<ButtonItemAction>

@property (nonatomic,copy) NSString *text;

@end
