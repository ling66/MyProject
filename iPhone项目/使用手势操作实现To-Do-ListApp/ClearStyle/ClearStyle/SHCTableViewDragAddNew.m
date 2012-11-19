//
//  SHCTableViewDragAddNew.m
//  ClearStyle
//
//  Created by l l on 16/11/12.
//  Copyright (c) 2012 ll. All rights reserved.
//

#import "SHCTableViewDragAddNew.h"
#import "SHCTableViewCell.h"

@implementation SHCTableViewDragAddNew {
    // the table that this gesture is associated with
    SHCTableView* _tableView;
    
    // a cell that is rendered as a placeholder to indicate where a new item is added
    SHCTableViewCell* _placeholderCell;
    
    // indicates the state of this behavior
    BOOL _pullDownInProgress;
}

//-(id)initWithCoder:(NSCoder *)aDecoder {
//    self = [super initWithCoder:aDecoder];
//    if (self) {
//        _placeholderCell = [[SHCTableViewCell alloc] init];
//        _placeholderCell.backgroundColor = [UIColor redColor];
//    }
//    return self;
//}

-(id)initWithTableView:(SHCTableView *)tableView {
    self = [super init];
    if (self) {
        _placeholderCell = [[SHCTableViewCell alloc] init];
        _placeholderCell.backgroundColor = [UIColor redColor];
        _tableView = tableView;
        
        _tableView.delegate = self;
    }
    return self;
}

#pragma mark - UIScrollViewDelegate
-(void)scrollViewWillBeginDragging:(UIScrollView *)scrollView {
    // this behaviour starts when a user pulls down while at the top of the table
    _pullDownInProgress = scrollView.contentOffset.y <= 0.0f;
    NSLog(@"contentOffset.y is: %f",scrollView.contentOffset.y);
    if (_pullDownInProgress) {
        // add your placeholder
        [_tableView insertSubview:_placeholderCell atIndex:0];
    }
}

-(void)scrollViewDidScroll:(UIScrollView *)scrollView {
    if (_pullDownInProgress && _tableView.scrollView.contentOffset.y <= 0.0f) {
        // maintain the location of the placeholder
        _placeholderCell.frame = CGRectMake(0, - _tableView.scrollView.contentOffset.y - SHC_ROW_HEIGHT,
                                            _tableView.frame.size.width, SHC_ROW_HEIGHT);
        _placeholderCell.label.text = -_tableView.scrollView.contentOffset.y > SHC_ROW_HEIGHT ?
        @"Release to Add Item" : @"Pull to Add Item";
        _placeholderCell.alpha = MIN(1.0f, - _tableView.scrollView.contentOffset.y / SHC_ROW_HEIGHT);
    } else {
        _pullDownInProgress = false;
    }
}

-(void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate {
    // check whether the user pulled down far enough
    if (_pullDownInProgress && - _tableView.scrollView.contentOffset.y > SHC_ROW_HEIGHT) {
        [_tableView.dataSource itemAdded];
    }
    _pullDownInProgress = false;
    [_placeholderCell removeFromSuperview];
}

@end
