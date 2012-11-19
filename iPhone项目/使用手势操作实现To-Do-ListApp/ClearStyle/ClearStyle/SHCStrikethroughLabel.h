//
//  SHCStrikethroughLabel.h
//  ClearStyle
//
//  Created by l l on 14/11/12.
//  Copyright (c) 2012 ll. All rights reserved.
//

#import <UIKit/UIKit.h>

// A UILabel subclass that can optionally have a strikethrough.
@interface SHCStrikethroughLabel : UITextField

// A Boolean value that determines whether the label should have a strikethrough.
@property (nonatomic) bool strikethrough;

@end
