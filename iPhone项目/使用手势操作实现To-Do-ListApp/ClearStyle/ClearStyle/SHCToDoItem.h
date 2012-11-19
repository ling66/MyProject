//
//  SHCToDoItem.h
//  ClearStyle
//
//  Created by l l on 6/11/12.
//  Copyright (c) 2012 ll. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface SHCToDoItem : NSObject

// A text description of this item.
@property (nonatomic, copy) NSString *text;

// A Boolean value that determines the completed state of this item.
@property (nonatomic) BOOL completed;

// Returns an SHCToDoItem item initialized with the given text.
-(id)initWithText:(NSString*)text;

// Returns an SHCToDoItem item initialized with the given text.
+(id)toDoItemWithText:(NSString*)text;

@end
