//
//  SHCToDoItem.m
//  ClearStyle
//
//  Created by l l on 6/11/12.
//  Copyright (c) 2012 ll. All rights reserved.
//

#import "SHCToDoItem.h"

@implementation SHCToDoItem

-(id)initWithText:(NSString*)text {
    if (self = [super init]) {
        self.text = text;
    }
    return self;
}

+(id)toDoItemWithText:(NSString *)text {
    return [[SHCToDoItem alloc] initWithText:text];
}

@end
