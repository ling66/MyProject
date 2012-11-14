//
//  ButtonItem.m
//  TestApp_1
//
//  Created by l l on 13/09/12.
//  Copyright (c) 2012 ll. All rights reserved.
//

#import "ButtonItem.h"

@implementation ButtonItem

- (UIView *)viewForItem
{
    if (self.view == nil)
    {
        UIButton *button = [UIButton buttonWithType:UIButtonTypeRoundedRect];
        [button setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
        [button setTitle:self.text forState:UIControlStateNormal];
        button.backgroundColor = [UIColor grayColor];
        button.frame = CGRectMake(0, 0, 80.0f, 50.0f);
        self.view = button;
    }
    return self.view;
}

- (void)addTarget:(id)target action:(SEL)action forControlEvents:(UIControlEvents)event
{
    UIButton *button = (UIButton *)[self viewForItem];
    [button addTarget:target action:action forControlEvents:event];
}

@end
