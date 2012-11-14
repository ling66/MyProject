//
//  TestView.h
//  TestApp_1
//
//  Created by l l on 13/09/12.
//  Copyright (c) 2012 ll. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ItemDataSource.h"

@interface TestView : UIScrollView

@property (nonatomic,strong) ItemDataSource *dataSource;
@property (nonatomic,strong) UIView *containView;

@end
