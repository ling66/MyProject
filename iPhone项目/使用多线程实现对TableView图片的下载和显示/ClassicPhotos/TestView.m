//
//  TestView.m
//  TestApp_1
//
//  Created by l l on 13/09/12.
//  Copyright (c) 2012 ll. All rights reserved.
//

#import "TestView.h"
#import "Item.h"
#import "ButtonItem.h"

@implementation TestView

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self)
    {
        self.dataSource = [[ItemDataSource alloc] init];
        
        self.containView = [[UIView alloc] initWithFrame:frame];
        [self addSubview:self.containView];
        
        for (Item *item in self.dataSource.items)
        {
            UIView *view = [item viewForItem];
            [self.containView addSubview:view];
            
            if ([item conformsToProtocol:@protocol(ButtonItemAction)])
            {
                [(id<ButtonItemAction>)item addTarget:self action:@selector(StoryModeReleased:) forControlEvents:UIControlEventTouchUpInside];
            }
        }
    }
    return self;
}

- (void)layoutSubviews
{
    CGFloat offset = 0.f;
    for (Item *item in self.dataSource.items)
    {
        UIView *view = [item viewForItem];
        view.frame = CGRectMake(3, offset, view.frame.size.width, view.frame.size.height);
        offset += view.frame.size.height;
    }
    self.containView.frame = CGRectMake(0, 0, self.bounds.size.width, offset);
    self.contentSize = self.containView.frame.size;
}

//button press action
-(void)StoryModeReleased:(id)sender
{
    // Make sure it's a UIButton
    if (![sender isKindOfClass:[UIButton class]])
        return;
    
    NSString *title = [(UIButton *)sender currentTitle];
    NSLog(@"Button press: %@",title);
}

@end
