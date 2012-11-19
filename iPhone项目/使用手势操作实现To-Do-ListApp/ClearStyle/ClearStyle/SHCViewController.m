//
//  SHCViewController.m
//  ClearStyle
//
//  Created by l l on 6/11/12.
//  Copyright (c) 2012 ll. All rights reserved.
//

#import "SHCViewController.h"
#import "SHCToDoItem.h"
#import "SHCTableViewCell.h"
#import "SHCTableViewPinchToAdd.h"

@implementation SHCViewController{
    // an array of to-do items
    NSMutableArray *_toDoItems;
    
    // the offset applied to cells when entering “edit mode”
    float _editingOffset;
    
    SHCTableViewDragAddNew* _dragAddNew;
    
    SHCTableViewPinchToAdd* _pinchAddNew;
}

-(id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil {
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self)
    {
        // create a dummy to-do list
        _toDoItems = [[NSMutableArray alloc] init];
        [_toDoItems addObject:[SHCToDoItem toDoItemWithText:@"Feed the cat"]];
        [_toDoItems addObject:[SHCToDoItem toDoItemWithText:@"Buy eggs"]];
        [_toDoItems addObject:[SHCToDoItem toDoItemWithText:@"Pack bags for WWDC"]];
        [_toDoItems addObject:[SHCToDoItem toDoItemWithText:@"Rule the web"]];
        [_toDoItems addObject:[SHCToDoItem toDoItemWithText:@"Buy a new iPhone"]];
        [_toDoItems addObject:[SHCToDoItem toDoItemWithText:@"Find missing socks"]];
        [_toDoItems addObject:[SHCToDoItem toDoItemWithText:@"Write a new tutorial"]];
        [_toDoItems addObject:[SHCToDoItem toDoItemWithText:@"Master Objective-C"]];
        [_toDoItems addObject:[SHCToDoItem toDoItemWithText:@"Remember your wedding anniversary!"]];
        [_toDoItems addObject:[SHCToDoItem toDoItemWithText:@"Drink less beer"]];
        [_toDoItems addObject:[SHCToDoItem toDoItemWithText:@"Learn to draw"]];
        [_toDoItems addObject:[SHCToDoItem toDoItemWithText:@"Take the car to the garage"]];
        [_toDoItems addObject:[SHCToDoItem toDoItemWithText:@"Sell things on eBay"]];
        [_toDoItems addObject:[SHCToDoItem toDoItemWithText:@"Learn to juggle"]];
        [_toDoItems addObject:[SHCToDoItem toDoItemWithText:@"Give up"]];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.tableView.dataSource = self;
    self.tableView.backgroundColor = [UIColor blackColor];
    [self.tableView registerClassForCells:[SHCTableViewCell class]];
    
    _dragAddNew = [[SHCTableViewDragAddNew alloc] initWithTableView:self.tableView];
    _pinchAddNew = [[SHCTableViewPinchToAdd alloc] initWithTableView:self.tableView];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - UITableViewDataSource protocol methods
//-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
//    return _toDoItems.count;
//}
//
//-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
//    NSString *ident = @"cell";
//    // re-use or create a cell
//    SHCTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:ident forIndexPath:indexPath];
//    cell.textLabel.backgroundColor = [UIColor clearColor];
//    // find the to-do item for this index
//    int index = [indexPath row];
//    SHCToDoItem *item = _toDoItems[index];
//    // set the text
////    cell.textLabel.text = item.text;
//    
//    cell.delegate = self;
//    cell.todoItem = item;
//    
//    return cell;
//}

#pragma mark - UITableViewDataDelegate protocol methods
//-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
//    return 50.0f;
//}
//
//-(void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath {
//    cell.backgroundColor = [self colorForIndex:indexPath.row];
//}

#pragma mark - SHCTableViewDataSource methods
-(NSInteger)numberOfRows {
    return _toDoItems.count;
}

-(UITableViewCell *)cellForRow:(NSInteger)row {
//    NSString *ident = @"cell";
//    SHCTableViewCell *cell = [[SHCTableViewCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:ident];
    SHCTableViewCell* cell = (SHCTableViewCell*)[self.tableView dequeueReusableCell];
    SHCToDoItem *item = _toDoItems[row];
    cell.todoItem = item;
    cell.delegate = self;
    cell.backgroundColor = [self colorForIndex:row];
    return cell;
}

-(UIColor*)colorForIndex:(NSInteger) index {
    NSUInteger itemCount = _toDoItems.count - 1;
    float val = ((float)index / (float)itemCount) * 0.6;
//    return [UIColor colorWithRed: 1.0 green:val blue: 0.0 alpha:1.0];
    return [UIColor colorWithRed: 1.0 green:val blue: 0.0 alpha:1.0];
}

//-(void)itemAdded {
//    // create the new item
//    SHCToDoItem* toDoItem = [[SHCToDoItem alloc] init];
//    [_toDoItems insertObject:toDoItem atIndex:0];
//    // refresh the table
//    [_tableView reloadData];
//    // enter edit mode
//    SHCTableViewCell* editCell;
//    for (SHCTableViewCell* cell in _tableView.visibleCells) {
//        if (cell.todoItem == toDoItem) {
//            editCell = cell;
//            break;
//        }
//    }
//    [editCell.label becomeFirstResponder];
//}

-(void)itemAdded {
    [self itemAddedAtIndex:0];
}

-(void)itemAddedAtIndex:(NSInteger)index {
    // create the new item
    SHCToDoItem* toDoItem = [[SHCToDoItem alloc] init];
    [_toDoItems insertObject:toDoItem atIndex:index];
    
    // refresh the table
    [_tableView reloadData];
    
    // enter edit mode
    SHCTableViewCell* editCell;
    for (SHCTableViewCell* cell in _tableView.visibleCells) {
        if (cell.todoItem == toDoItem) {
            editCell = cell;
            break;
        }
    }
    [editCell.label becomeFirstResponder];
}


#pragma mark - delegate method
-(void) toDoItemDeleted:(SHCToDoItem*)todoItem {
    float delay = 0.0;
    
    // remove the model object
    [_toDoItems removeObject:todoItem];
    
    // find the visible cells
    NSArray* visibleCells = [self.tableView visibleCells];
    
    UIView* lastView = [visibleCells lastObject];
    bool startAnimating = false;
    
    // iterate over all of the cells
    for(SHCTableViewCell* cell in visibleCells) {
        if (startAnimating) {
            [UIView animateWithDuration:0.3
                                  delay:delay
                                options:UIViewAnimationOptionCurveEaseInOut
                             animations:^{
                                 cell.frame = CGRectOffset(cell.frame, 0.0f, -cell.frame.size.height);
                             }
                             completion:^(BOOL finished){
                                 if (cell == lastView) {
                                     [self.tableView reloadData];
                                 }
                             }];
            delay+=0.03;
        }
        
        // if you have reached the item that was deleted, start animating
        if (cell.todoItem == todoItem) {
            startAnimating = true;
            cell.hidden = YES;
        }
    }
}

-(void)cellDidBeginEditing:(SHCTableViewCell *)editingCell {
    _editingOffset = _tableView.scrollView.contentOffset.y - editingCell.frame.origin.y;
    for(SHCTableViewCell* cell in [_tableView visibleCells]) {
        [UIView animateWithDuration:0.3
                         animations:^{
                             cell.frame = CGRectOffset(cell.frame, 0, _editingOffset);
                             if (cell != editingCell) {
                                 cell.alpha = 0.3;
                             }
                         }];
    }
}

-(void)cellDidEndEditing:(SHCTableViewCell *)editingCell {
    for(SHCTableViewCell* cell in [_tableView visibleCells]) {
        [UIView animateWithDuration:0.3
                         animations:^{
                             cell.frame = CGRectOffset(cell.frame, 0, -_editingOffset);
                             if (cell != editingCell)
                             {
                                 cell.alpha = 1.0;
                             }
                         }];
    }
}

@end
