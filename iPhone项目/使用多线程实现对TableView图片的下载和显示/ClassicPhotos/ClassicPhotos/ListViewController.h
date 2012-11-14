//
//  ListViewController.h
//  ClassicPhotos
//
//  Created by l l on 15/09/12.
//  Copyright (c) 2012 ll. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "PhotoRecord.h"
#import "PendingOperations.h"
#import "ImageDownloader.h"
#import "ImageFiltration.h"
#import "AFNetworking.h"

#import "EGORefreshTableHeaderView.h"
#import "MBProgressHUD.h"

#import "DetailViewController.h"

//#define kDatasourceURLString @"http://www.raywenderlich.com/downloads/ClassicPhotosDictionary.plist"

#define kDatasourceURLString @"https://sites.google.com/site/soheilsstudio/tutorials/nsoperationsampleproject/ClassicPhotosDictionary.plist"


@interface ListViewController : UITableViewController<ImageDownloaderDelegate,ImageFiltrationDelegate,EGORefreshTableHeaderDelegate>
{
    EGORefreshTableHeaderView *_refreshHeaderView;
	
	//  Reloading var should really be your tableviews datasource
	//  Putting it here for demo purposes
	BOOL _reloading;
}

@property (nonatomic,strong) NSMutableArray *photos;

@property (nonatomic,strong) PendingOperations *pendingOperations;

- (void)downloadTableViewDataSource;
- (void)reloadTableViewDataSource;
- (void)doneLoadingTableViewData;

-(IBAction)pressRefresh:(id)sender;

@end
