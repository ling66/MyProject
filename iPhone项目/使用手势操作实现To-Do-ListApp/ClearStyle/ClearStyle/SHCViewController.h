//
//  SHCViewController.h
//  ClearStyle
//
//  Created by l l on 6/11/12.
//  Copyright (c) 2012 ll. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "SHCTableViewCellDelegate.h"
#import "SHCTableView.h"
#import "SHCTableViewDataSource.h"
#import "SHCTableViewDragAddNew.h"

@interface SHCViewController : UIViewController<SHCTableViewCellDelegate,SHCTableViewDataSource>

@property (weak, nonatomic) IBOutlet SHCTableView *tableView;

@end
