//
//  ImageDownloader.h
//  ClassicPhotos
//
//  Created by l l on 17/09/12.
//  Copyright (c) 2012 ll. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "PhotoRecord.h"

@protocol ImageDownloaderDelegate;

@interface ImageDownloader : NSOperation

@property (nonatomic, assign) id <ImageDownloaderDelegate> delegate;

@property (nonatomic, readonly, strong) NSIndexPath *indexPathInTableView;
@property (nonatomic, readonly, strong) PhotoRecord *photoRecord;

- (id)initWithPhotoRecord:(PhotoRecord *)record atIndexPath:(NSIndexPath *)indexPath delegate:(id<ImageDownloaderDelegate>) theDelegate;

@end

@protocol ImageDownloaderDelegate <NSObject>

- (void)imageDownloaderDidFinish:(ImageDownloader *)downloader;
@end
