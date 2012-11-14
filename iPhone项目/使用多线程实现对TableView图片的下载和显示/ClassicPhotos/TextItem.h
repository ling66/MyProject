//
//  TextItem.h
//  TestApp_1
//
//  Created by l l on 13/09/12.
//  Copyright (c) 2012 ll. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Item.h"

@interface TextItem : Item

@property (nonatomic,copy) NSString *text;
@property (nonatomic,strong) UIColor *bgColor;

@end
