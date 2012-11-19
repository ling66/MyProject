//
//  SHCTableViewCell.h
//  ClearStyle
//
//  Created by l l on 8/11/12.
//  Copyright (c) 2012 ll. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "SHCTableViewCellDelegate.h"
#import "SHCStrikethroughLabel.h"

// A custom table cell that renders SHCToDoItem items.
@interface SHCTableViewCell : UITableViewCell<UITextFieldDelegate>

// The item that this cell renders.
@property (nonatomic) SHCToDoItem *todoItem;

// The object that acts as delegate for this cell.
@property (nonatomic, assign) id<SHCTableViewCellDelegate> delegate;

// the label used to render the to-do text
@property (nonatomic, strong, readonly) SHCStrikethroughLabel* label;

@end
