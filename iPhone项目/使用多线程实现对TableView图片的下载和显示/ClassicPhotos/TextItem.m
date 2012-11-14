//
//  TextItem.m
//  TestApp_1
//
//  Created by l l on 13/09/12.
//  Copyright (c) 2012 ll. All rights reserved.
//

#import "TextItem.h"

@implementation TextItem

@synthesize text = _text, bgColor = _bgColor;

- (UIView *)viewForItem
{
    if (self.view == nil)
    {
        CGRect screenRect = [[UIScreen mainScreen] bounds];
        //    NSLog(@"width:%f height:%f",screenRect.size.width,screenRect.size.height);
        CGSize maxSize = CGSizeMake(screenRect.size.width-6, CGFLOAT_MAX);
        CGSize viewSize =[self.text sizeWithFont:[UIFont systemFontOfSize:12]
                               constrainedToSize:maxSize
                                   lineBreakMode:UILineBreakModeWordWrap];
        
        UILabel *itemTitle = [[UILabel alloc] initWithFrame:CGRectZero];
        itemTitle.text = self.text;
        itemTitle.numberOfLines = 0;
        itemTitle.font = [UIFont boldSystemFontOfSize:12.0];
        itemTitle.lineBreakMode = UILineBreakModeWordWrap;
        itemTitle.backgroundColor = self.bgColor;
        self.view = itemTitle;
        self.view.frame = CGRectMake(0, 0, viewSize.width, viewSize.height);
    }
    return self.view;
}

@end
